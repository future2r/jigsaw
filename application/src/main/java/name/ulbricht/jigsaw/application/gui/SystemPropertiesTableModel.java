package name.ulbricht.jigsaw.application.gui;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Two column table model for all system properties.
 *
 * @author Frank.Ulbricht
 */
final class SystemPropertiesTableModel implements TableModel {

	private final EventListenerList eventListeners = new EventListenerList();
	private final List<String> systemProperties;

	public SystemPropertiesTableModel() {
		this.systemProperties = System.getProperties().keySet().stream() //
				.map(Objects::toString) //
				.sorted() //
				.collect(Collectors.toList());
	}

	@Override
	public int getRowCount() {
		return this.systemProperties.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(final int columnIndex) {
		switch (columnIndex) {
			case 0 :
				return "Key";
			case 1 :
				return "Value";
			default :
				throw new IllegalArgumentException(Integer.toString(columnIndex));
		}
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		switch (columnIndex) {
			case 0 :
				return this.systemProperties.get(rowIndex);
			case 1 :
				return Objects.toString(System.getProperty(this.systemProperties.get(rowIndex)));
			default :
				throw new IllegalArgumentException(Integer.toString(columnIndex));
		}
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTableModelListener(final TableModelListener l) {
		this.eventListeners.add(TableModelListener.class, l);

	}

	@Override
	public void removeTableModelListener(final TableModelListener l) {
		this.eventListeners.remove(TableModelListener.class, l);
	}
}