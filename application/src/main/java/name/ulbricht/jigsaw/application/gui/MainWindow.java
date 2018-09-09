package name.ulbricht.jigsaw.application.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ServiceLoader;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
public final class MainWindow extends JFrame {

	private static final Logger log = Logger.getLogger(MainWindow.class.getPackageName());

	public static void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			log.log(Level.SEVERE, ex, () -> ex.getMessage());
		}

		final var mainWindow = new MainWindow();
		mainWindow.setVisible(true);
	}

	private MainWindow() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Jigsaw Application");
		setIconImages(IntStream.of(16, 24, 32, 48, 64, 128, 256, 512) //
			.mapToObj(size -> String.format("app%s.png", Integer.toString(size))) //
			.map(name -> getClass().getResource(name)) //
			.map(url -> Toolkit.getDefaultToolkit().createImage(url)) //
			.collect(Collectors.toList()));
		setLayout(new BorderLayout());
		setBackground(UIManager.getColor("TabbedPane.background"));
		getRootPane().setBorder(new EmptyBorder(4, 4, 4, 4));
		
		final var tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Modules", createModulesComponent());
		tabbedPane.addTab("Services", createServicesComponent());
		tabbedPane.addTab("JAXB", createJAXBComponent());
		tabbedPane.addTab("Properties", createSystemPropertiesComponent());

		add(tabbedPane, BorderLayout.CENTER);

		final var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setOpaque(false);
		final var closeButton = new JButton("Close");
		closeButton.addActionListener(e -> dispose());
		buttonPanel.add(closeButton);
		add(buttonPanel, BorderLayout.SOUTH);

		pack();
		setLocationByPlatform(true);
	}

	private static JComponent createJAXBComponent() {
		final var persons = new Persons();
		persons.getPersons().add(new Person(42, "John", "Smith"));
		persons.getPersons().add(new Person(43, "Jane", "Miller"));

		String xml;
		try (final var sw = new StringWriter()) {
			JAXB.marshal(persons, sw);
			xml = sw.toString();
		} catch (final IOException ex) {
			log.log(Level.SEVERE, ex, () -> ex.getMessage());
			xml = ex.getMessage();
		}

		final var descrLabel = new JLabel("Using the deprected JAXB module:");
		final var textArea = new JTextArea(xml);
		descrLabel.setLabelFor(textArea);
		textArea.setEditable(false);
		
		final var panel = new JPanel(new BorderLayout(0, 8));
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(8, 8, 8, 8));
		panel.add(descrLabel, BorderLayout.NORTH);
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

		return panel;
	}

	@SuppressWarnings("unchecked")
	private JComponent createServicesComponent() {
		final var serviceLoader = ServiceLoader.load(GreetingsHandler.class);
		final var serviceProviders = serviceLoader.stream().collect(Collectors.toCollection(Vector::new));

		final var servicesLabel = new JLabel("Services:");
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
		panel.setOpaque(false);
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
		final var descrLabel = new JLabel("Currently available Java modules:");
		final var tree = new JTree(new ModulesTreeModel());
		tree.setCellRenderer(new ModulesTreeCellRenderer());
		descrLabel.setLabelFor(tree);

		final var panel = new JPanel(new BorderLayout(0, 8));
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(8, 8, 8, 8));
		panel.add(descrLabel, BorderLayout.NORTH);
		panel.add(new JScrollPane(tree), BorderLayout.CENTER);

		return panel;
	}

	private static JComponent createSystemPropertiesComponent() {
		final var descrLabel = new JLabel("Currenty set Java system properties:");
		final var table = new JTable(new SystemPropertiesTableModel());
		descrLabel.setLabelFor(table);

		final var panel = new JPanel(new BorderLayout(0, 8));
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(8, 8, 8, 8));
		panel.add(descrLabel, BorderLayout.NORTH);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);

		return panel;
	}
}