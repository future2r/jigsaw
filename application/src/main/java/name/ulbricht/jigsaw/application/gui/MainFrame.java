package name.ulbricht.jigsaw.application.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ServiceLoader;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.border.EmptyBorder;
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
			log.log(Level.SEVERE, ex, () -> ex.getMessage());
		}

		final var mainFrame = new MainFrame();
		mainFrame.setVisible(true);
	}

	private MainFrame() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Jigsaw Application");

		setLayout(new BorderLayout());

		final var tabbedPane = new JTabbedPane();
		tabbedPane.addTab("JAXB", createJAXBComponent());
		tabbedPane.addTab("Services", createServicesComponent());
		tabbedPane.addTab("System Properties", createSystemPropertiesComponent());
		tabbedPane.addTab("Modules", createModulesComponent());
		add(tabbedPane, BorderLayout.CENTER);

		pack();
		setLocationByPlatform(true);
	}

	private static JComponent createJAXBComponent() {
		final var text = new StringBuilder();
		text.append("This application demonstrates the usage of JAXB as an optional module.\n\n");

		final var persons = new Persons();
		persons.getPersons().add(new Person(42, "John", "Smith"));
		persons.getPersons().add(new Person(43, "Jane", "Miller"));

		try (final var sw = new StringWriter()) {
			JAXB.marshal(persons, sw);
			text.append(sw.toString());
		} catch (final IOException ex) {
			log.log(Level.SEVERE, ex, () -> ex.getMessage());
		}

		final var textArea = new JTextArea(text.toString());
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		return new JScrollPane(textArea);
	}

	@SuppressWarnings("unchecked")
	private JComponent createServicesComponent() {
		final var serviceLoader = ServiceLoader.load(GreetingsHandler.class);
		final var serviceProviders = serviceLoader.stream().collect(Collectors.toCollection(Vector::new));

		final var servicesLabel = new JLabel("Available service implementations:");
		final var servicesComboBox = new JComboBox<ServiceLoader.Provider<GreetingsHandler>>(serviceProviders);
		servicesLabel.setLabelFor(servicesComboBox);
		servicesComboBox.setRenderer(new ServiceProviderListCellRenderer<GreetingsHandler>());
		if (servicesComboBox.getItemCount() > 0) servicesComboBox.setSelectedIndex(0);

		final var messageLabel = new JLabel("Message:");
		final var messageTextField = new JTextField("Hello World!");
		messageLabel.setLabelFor(messageTextField);

		final var sendButton = new JButton("Send Message");
		sendButton.addActionListener(e -> sendMessage((ServiceLoader.Provider<GreetingsHandler>) servicesComboBox.getSelectedItem(), messageTextField.getText()));

		final var panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(8, 8, 8, 8));
		panel.add(servicesLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		panel.add(servicesComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
		panel.add(messageLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		panel.add(messageTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
		panel.add(sendButton, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));

		return panel;
	}

	private void sendMessage(final ServiceLoader.Provider<GreetingsHandler> serviceProvider, final String message) {
		if (serviceProvider != null) {
			final var greetings = Greetings.createMessage(message).withSource(getTitle());
			final var service = serviceProvider.get(); 
			service.sendGreetings(greetings);
		}
	}

	private static JComponent createModulesComponent() {
		final var tree = new JTree(new ModulesTreeModel());
		tree.setCellRenderer(new ModulesTreeCellRenderer());
		return new JScrollPane(tree);
	}

	private static JComponent createSystemPropertiesComponent() {
		final var table = new JTable(new SystemPropertiesTableModel());
		return new JScrollPane(table);
	}
}