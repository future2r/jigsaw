package name.ulbricht.jigsaw.application.gui;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import javax.swing.ComboBoxModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataListener;

import name.ulbricht.jigsaw.greetings.GreetingsHandler;

final class ServiceProviderComboBoxModel implements ComboBoxModel<ServiceLoader.Provider<GreetingsHandler>> {

	private final List<ServiceLoader.Provider<GreetingsHandler>> services;
	private ServiceLoader.Provider<GreetingsHandler> selectedItem;
	private final EventListenerList eventListeners = new EventListenerList();

	ServiceProviderComboBoxModel() {
		this.services = ServiceLoader.load(GreetingsHandler.class).stream().collect(Collectors.toList());
	}

	@Override
	public int getSize() {
		return this.services.size();
	}

	@Override
	public ServiceLoader.Provider<GreetingsHandler> getElementAt(final int index) {
		return this.services.get(index);
	}

	@Override
	public void addListDataListener(final ListDataListener l) {
		this.eventListeners.add(ListDataListener.class, l);
	}

	@Override
	public void removeListDataListener(final ListDataListener l) {
		this.eventListeners.remove(ListDataListener.class, l);
	}

	@Override
	public Object getSelectedItem() {
		return this.selectedItem;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setSelectedItem(final Object anItem) {
		this.selectedItem = (ServiceLoader.Provider<GreetingsHandler>) anItem;
	}

	Optional<ServiceLoader.Provider<GreetingsHandler>> getSelectedProvider() {
		return Optional.ofNullable(this.selectedItem);
	}
}