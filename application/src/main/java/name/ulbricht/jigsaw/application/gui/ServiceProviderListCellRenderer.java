package name.ulbricht.jigsaw.application.gui;

import java.awt.Component;
import java.util.ServiceLoader;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

final class ServiceProviderListCellRenderer<T> implements ListCellRenderer<ServiceLoader.Provider<T>> {

private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();

	private static final Resources resources = Resources.of("ServiceProviderListCellRenderer");

	@Override
	public Component getListCellRendererComponent(final JList<? extends ServiceLoader.Provider<T>> list, final ServiceLoader.Provider<T> value, final int index, final boolean isSelected, final boolean cellHasFocus) {
		final var text = value != null ? value.type().getName() : "";
		final var component = delegate.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);

		if (component instanceof JLabel) {
			final var label = (JLabel) component;
			label.setIcon(resources.getIcon("icon"));
		}

		return component;
	}
}