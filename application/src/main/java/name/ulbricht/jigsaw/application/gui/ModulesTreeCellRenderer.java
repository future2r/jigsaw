package name.ulbricht.jigsaw.application.gui;

import java.awt.Component;

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
			label.setIcon(node.getType().getIcon());
		}

		return component;
	}
}
