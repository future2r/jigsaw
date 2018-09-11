package name.ulbricht.jigsaw.application.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;

import name.ulbricht.jigsaw.application.xml.Person;
import name.ulbricht.jigsaw.application.xml.Persons;
import name.ulbricht.jigsaw.greetings.Greetings;
import name.ulbricht.jigsaw.greetings.GreetingsHandler;

@SuppressWarnings("serial")
public final class MainFrame extends JFrame {

	private static final Logger log = Logger.getLogger(MainFrame.class.getPackageName());

	public static void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			log.log(Level.SEVERE, ex, ex::getMessage);
		}

		new MainFrame().setVisible(true);
	}

	private static final class ControlPanel extends JPanel {

		ControlPanel(final LayoutManager layout) {
			super(layout);
			setOpaque(false);
			setBorder(new EmptyBorder(8, 8, 8, 8));
		}
	}

	private MainFrame() {
		final var resources = Resources.of("application");

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(resources.getString("name"));
		setIconImages(resources.getImages("icons"));

		createContent();

		getRootPane().setBorder(new EmptyBorder(8, 8, 8, 8));
		pack();
		setLocationByPlatform(true);
	}

	private void createContent() {
		final var resources = Resources.of("MainFrame");

		final var tabbedPane = new JTabbedPane();
		addModulesComponent(tabbedPane);
		addServicesComponent(tabbedPane);
		addJAXBComponent(tabbedPane);
		addPropertiesComponent(tabbedPane);

		final var buttonPanel = new ControlPanel(new BorderLayout());
		buttonPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
		buttonPanel.add(createButton(resources.with("closeButton"), this::dispose), BorderLayout.EAST);

		add(tabbedPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private static void addJAXBComponent(final JTabbedPane tabbedPane) {
		final var resources = Resources.of("MainFrame.jaxbTab");

		final var persons = new Persons();
		persons.getPersons().add(new Person(42, "John", "Smith"));
		persons.getPersons().add(new Person(43, "Jane", "Miller"));

		String xml;
		try (final var sw = new StringWriter()) {
			JAXB.marshal(persons, sw);
			xml = sw.toString();
		} catch (final IOException | DataBindingException ex) {
			log.log(Level.SEVERE, ex, ex::getMessage);
			xml = ex.getMessage();
		}

		final var textArea = new JTextArea(xml);
		textArea.setEditable(false);

		final var panel = new ControlPanel(new BorderLayout(0, 8));
		panel.add(createLabelFor(resources.with("descrLabel"), textArea), BorderLayout.NORTH);
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

		addTab(tabbedPane, resources, panel);
	}

	private void addServicesComponent(final JTabbedPane tabbedPane) {
		final var resources = Resources.of("MainFrame.servicesTab");

		final var servicesComboBox = new JComboBox<ServiceLoader.Provider<GreetingsHandler>>(new ServiceProviderComboBoxModel());
		servicesComboBox.setRenderer(new ServiceProviderListCellRenderer<GreetingsHandler>());
		if (servicesComboBox.getItemCount() > 0) servicesComboBox.setSelectedIndex(0);

		final var messageTextField = new JTextField(resources.getString("messageTextField.text"));

		final var sendButton = createButton(resources.with("sendButton"),
			()->((ServiceProviderComboBoxModel) servicesComboBox.getModel()).getSelectedProvider()
				.ifPresent(p -> sendMessage(p, messageTextField.getText())));

		final var panel = new ControlPanel(new GridBagLayout());
		panel.add(createLabelFor(resources.with("servicesLabel"), servicesComboBox), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		panel.add(servicesComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
		panel.add(createLabelFor(resources.with("messageLabel"), messageTextField), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		panel.add(messageTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
		panel.add(sendButton, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));

		addTab(tabbedPane, resources, panel);
	}

	private void sendMessage(final ServiceLoader.Provider<GreetingsHandler> serviceProvider, final String message) {
		if (serviceProvider != null) {
			serviceProvider.get().sendGreetings(Greetings.createMessage(message).withSource(getTitle()));
		}
	}

	private static void addModulesComponent(final JTabbedPane tabbedPane) {
		final var resources = Resources.of("MainFrame.modulesTab");

		final var tree = new JTree(new ModulesTreeModel());
		tree.setCellRenderer(new ModulesTreeCellRenderer());

		final var panel = new ControlPanel(new BorderLayout(0, 8));
		panel.add(createLabelFor(resources.with("descrLabel"), tree), BorderLayout.NORTH);
		panel.add(new JScrollPane(tree), BorderLayout.CENTER);

		addTab(tabbedPane, resources, panel);
	}

	private static void addPropertiesComponent(final JTabbedPane tabbedPane) {
		final var resources = Resources.of("MainFrame.propertiesTab");

		final var table = new JTable(new SystemPropertiesTableModel());

		final var panel = new ControlPanel(new BorderLayout(0, 8));
		panel.add(createLabelFor(resources.with("descrLabel"), table), BorderLayout.NORTH);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);

		addTab(tabbedPane, resources, panel);
	}

	private static JLabel createLabelFor(final Resources resources, final JComponent target) {
		final var label = new JLabel(resources.getString("text"));
		label.setLabelFor(target);
		return label;
	}

	private static JButton createButton(final Resources resources, final Runnable action) {
		final var button = new JButton(resources.getString("text"));
		button.addActionListener(e -> action.run());
		return button;
	}

	private static void addTab(final JTabbedPane tabbedPane, final Resources resources, final JComponent content) {
		tabbedPane.addTab(resources.getString("title"), resources.getIcon("icon"), content);
	}
}