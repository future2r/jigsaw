package name.ulbricht.jigsaw.application.gui;

import java.lang.module.ModuleDescriptor;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Lazily populated tree model for the current module layer.
 *
 * @author Frank.Ulbricht
 */
final class ModulesTreeModel implements TreeModel {

	private static final Logger log = Logger.getLogger(ModulesTreeModel.class.getPackageName());

	enum NodeType {
		MODULES, MODULE, PACKAGES, PACKAGE, INTERFACES, INTERFACE, CLASSES, CLASS, PROPERTY;
	}

	static final class Node<T> {
		private final NodeType type;
		private final T data;
		private final Function<T, String> nameFunction;
		private final Function<T, Stream<Node<?>>> childrenFunction;
		private List<Node<?>> children;

		Node(final NodeType type, final T data, final Function<T, String> nameFunction) {
			this(type, data, nameFunction, null);
		}

		Node(final NodeType type, final T data, final Function<T, String> nameFunction,
				final Function<T, Stream<Node<?>>> childrenFunction) {
			this.type = Objects.requireNonNull(type);
			this.data = Objects.requireNonNull(data);
			this.nameFunction = Objects.requireNonNull(nameFunction);
			this.childrenFunction = childrenFunction;
		}

		NodeType getType() {
			return this.type;
		}

		T getData() {
			return this.data;
		}

		String getName() {
			return this.nameFunction.apply(this.data);
		}

		boolean isLeaf() {
			return this.childrenFunction == null;
		}

		int getChildCount() {
			return getChildren() //
					.map(List::size) //
					.orElse(Integer.valueOf(0)).intValue();
		}

		Node<?> getChild(final int index) {
			return getChildren(). //
					map(c -> c.get(index)) //
					.orElse(null);
		}

		int getIndexOfChild(final Node<?> child) {
			return getChildren() //
					.map(c -> Integer.valueOf(c.indexOf(child))) //
					.orElse(Integer.valueOf(-1)).intValue();
		}

		private Optional<List<Node<?>>> getChildren() {
			if (this.children == null && this.childrenFunction != null) {
				this.children = this.childrenFunction.apply(this.data) //
						.collect(Collectors.toList());
			}
			return Optional.ofNullable(this.children);
		}
	}

	private enum Property {
		AUTOMATIC("automatic: %s"), //
		OPEN("open: %s"), //
		VERSION("version: %s"), //
		MAIN_CLASS("main class: %s"), //
		QUALIFIED("qualified: %s");

		private final String namePattern;

		Property(final String namePattern) {
			this.namePattern = namePattern;
		}

		String createName(final boolean value) {
			return createName(value ? "yes" : "no");
		}

		String createName(final String value) {
			return String.format(this.namePattern, value);
		}
	}

	private final EventListenerList eventListeners = new EventListenerList();
	private final Node<ModuleLayer> root;

	public ModulesTreeModel() {
		this.root = createModulesNode();
	}

	@Override
	public Object getRoot() {
		return this.root;
	}

	@Override
	public boolean isLeaf(final Object node) {
		return ((Node<?>) node).isLeaf();
	}

	@Override
	public int getChildCount(final Object parent) {
		return ((Node<?>) parent).getChildCount();
	}

	@Override
	public Object getChild(final Object parent, final int index) {
		return ((Node<?>) parent).getChild(index);
	}

	@Override
	public int getIndexOfChild(final Object parent, final Object child) {
		return ((Node<?>) parent).getIndexOfChild((Node<?>) child);
	}

	@Override
	public void valueForPathChanged(final TreePath path, final Object newValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTreeModelListener(final TreeModelListener l) {
		this.eventListeners.add(TreeModelListener.class, l);
	}

	@Override
	public void removeTreeModelListener(final TreeModelListener l) {
		this.eventListeners.remove(TreeModelListener.class, l);
	}

	private static Node<ModuleLayer> createModulesNode() {
		log.info(() -> "Reading modules");

		final var moduleLayer = ModuleLayer.boot();
		return new Node<>(NodeType.MODULES, moduleLayer, ml -> "Modules", //
				ml -> ml.modules().stream() //
						.map(Module::getDescriptor) //
						.sorted() //
						.map(ModulesTreeModel::createModuleNode));
	}

	private static Node<ModuleDescriptor> createModuleNode(final ModuleDescriptor moduleDescriptor) {
		return new Node<>(NodeType.MODULE, moduleDescriptor, ModuleDescriptor::name, //
				md -> Stream.of( //
						new Node<>(NodeType.PROPERTY, Property.AUTOMATIC,
								p -> p.createName(moduleDescriptor.isAutomatic())), //
						new Node<>(NodeType.PROPERTY, Property.OPEN, p -> p.createName(moduleDescriptor.isOpen())), //
						new Node<>(NodeType.PROPERTY, Property.VERSION,
								p -> p.createName(moduleDescriptor.version().map(Object::toString).orElse(""))), //
						new Node<>(NodeType.PROPERTY, Property.MAIN_CLASS,
								p -> p.createName(moduleDescriptor.mainClass().orElse(""))), //
						new Node<>(NodeType.MODULES, moduleDescriptor, md2 -> "required modules",
								ModulesTreeModel::createRequiresNodes), //
						new Node<>(NodeType.PACKAGES, moduleDescriptor, md2 -> "exported packages",
								ModulesTreeModel::createExportsNodes), //
						new Node<>(NodeType.PACKAGES, moduleDescriptor, md2 -> "opened packages",
								ModulesTreeModel::createOpensNodes), //
						new Node<>(NodeType.INTERFACES, moduleDescriptor, md2 -> "provided services",
								ModulesTreeModel::createProvidesNodes), //
						new Node<>(NodeType.INTERFACES, moduleDescriptor, md2 -> "used services",
								ModulesTreeModel::createUsesNodes) //
				));
	}

	private static Stream<Node<?>> createRequiresNodes(final ModuleDescriptor moduleDescriptor) {
		log.info(() -> String.format("Reading required modules for module '%s'", moduleDescriptor.name()));

		return moduleDescriptor.requires().stream() //
				.sorted() //
				.map(r -> new Node<>(NodeType.MODULE, r, ModuleDescriptor.Requires::name));
	}

	private static Stream<Node<?>> createExportsNodes(final ModuleDescriptor moduleDescriptor) {
		log.info(() -> String.format("Reading exported packages for module '%s'", moduleDescriptor.name()));

		return moduleDescriptor.exports().stream() //
				.sorted() //
				.map(ModulesTreeModel::createExportsNode);
	}

	private static Node<ModuleDescriptor.Exports> createExportsNode(final ModuleDescriptor.Exports exports) {
		return new Node<>(NodeType.PACKAGE, exports, ModuleDescriptor.Exports::source, //
				e -> Stream.of( //
						new Node<>(NodeType.PROPERTY, Property.QUALIFIED, p -> p.createName(exports.isQualified())), //
						new Node<>(NodeType.MODULES, exports.targets(), t -> "for targets",
								t -> createStringNodes(NodeType.MODULE, t)) //
				));
	}

	private static Stream<Node<?>> createOpensNodes(final ModuleDescriptor moduleDescriptor) {
		log.info(() -> String.format("Reading opened packages for module '%s'", moduleDescriptor.name()));

		return moduleDescriptor.opens().stream() //
				.sorted() //
				.map(ModulesTreeModel::createOpensNode);
	}

	private static Node<ModuleDescriptor.Opens> createOpensNode(final ModuleDescriptor.Opens opens) {
		return new Node<>(NodeType.PACKAGE, opens, ModuleDescriptor.Opens::source, //
				e -> Stream.of( //
						new Node<>(NodeType.PROPERTY, Property.QUALIFIED, p -> p.createName(opens.isQualified())), //
						new Node<>(NodeType.MODULES, opens.targets(), t -> "for targets",
								t -> createStringNodes(NodeType.MODULE, t)) //
				));
	}

	private static Stream<Node<?>> createProvidesNodes(final ModuleDescriptor moduleDescriptor) {
		log.info(() -> String.format("Reading provided services for module '%s'", moduleDescriptor.name()));

		return moduleDescriptor.provides().stream() //
				.sorted() //
				.map(ModulesTreeModel::createProvidesNode);
	}

	private static Node<ModuleDescriptor.Provides> createProvidesNode(final ModuleDescriptor.Provides provides) {
		return new Node<>(NodeType.INTERFACE, provides, ModuleDescriptor.Provides::service, //
				e -> Stream.of( //
						new Node<>(NodeType.CLASSES, provides.providers(), t -> "providers",
								t -> createStringNodes(NodeType.CLASS, t)) //
				));
	}

	private static Stream<Node<?>> createUsesNodes(final ModuleDescriptor moduleDescriptor) {
		log.info(() -> String.format("Reading used services for module '%s'", moduleDescriptor.name()));

		return createStringNodes(NodeType.INTERFACE, moduleDescriptor.uses());
	}

	private static Stream<Node<?>> createStringNodes(final NodeType type, final Collection<String> strings) {
		return strings.stream() //
				.sorted() //
				.map(s -> new Node<>(type, s, String::toString));
	}
}