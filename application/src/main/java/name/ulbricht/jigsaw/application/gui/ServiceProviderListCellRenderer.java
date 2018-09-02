package name.ulbricht.jigsaw.application.gui;

import java.awt.Component;
import java.util.ServiceLoader;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.DefaultListCellRenderer;

final class ServiceProviderListCellRenderer<T> implements ListCellRenderer<ServiceLoader.Provider<T>> {

    private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();

    @Override
    public Component getListCellRendererComponent(final JList<? extends ServiceLoader.Provider<T>> list, final ServiceLoader.Provider<T> value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        final var text = value != null ? value.type().getName() : "";
        return delegate.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
    }

}