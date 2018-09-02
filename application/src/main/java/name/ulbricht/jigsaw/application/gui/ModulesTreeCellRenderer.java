package name.ulbricht.jigsaw.application.gui;

import java.awt.Component;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * Tree cell renderer for nodes in the tree model for modules.
 *
 * @author Frank.Ulbricht
 */
final class ModulesTreeCellRenderer implements TreeCellRenderer {

	private static final Map<ModulesTreeModel.NodeType, Icon> icons = new HashMap<>();

	private static Icon getIcon(final ModulesTreeModel.NodeType nodeType) {
		return icons.computeIfAbsent(nodeType, t -> new ImageIcon(
				ModulesTreeCellRenderer.class.getResource(t.name().toLowerCase(Locale.ENGLISH) + ".png")));
	}

	private final DefaultTreeCellRenderer delegate = new DefaultTreeCellRenderer();

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected,
			final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {

		final var node = (ModulesTreeModel.Node<?>) value;

		final var text = node.getName();
		final var component = this.delegate.getTreeCellRendererComponent(tree, text, selected, expanded, leaf, row,
				hasFocus);

		if (component instanceof JLabel) {
			final var label = (JLabel) component;
			label.setIcon(getIcon(node.getType()));
		}

		return component;
	}
}
