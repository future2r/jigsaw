package name.ulbricht.jigsaw.application.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;

import name.ulbricht.jigsaw.application.xml.Person;
import name.ulbricht.jigsaw.application.xml.Persons;
import name.ulbricht.jigsaw.greetings.Greetings;
import name.ulbricht.jigsaw.greetings.GreetingsHandler;

public final class MainWindow extends JFrame {

	private static final Logger log = Logger.getLogger(MainWindow.class.getPackageName());

	public static void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			log.log(Level.SEVERE, ex, ex::getMessage);
		}

		new MainWindow().setVisible(true);
	}

	private static final class ControlPanel extends JPanel {

		ControlPanel(final LayoutManager layout) {
			super(layout);
			setOpaque(false);
			setBorder(new EmptyBorder(8, 8, 8, 8));
		}
	}

	private MainWindow() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Jigsaw Application");
		setIconImages(IntStream.of(16, 24, 32, 48, 64, 128, 256, 512) //
			.mapToObj(size -> "app" + size + ".png") //
			.map(Resources::getImage) //
			.collect(Collectors.toList()));
		setBackground(UIManager.getColor("TabbedPane.background"));

		final var tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Modules", Resources.getIcon("modules.png"), createModulesComponent());
		tabbedPane.addTab("Services", Resources.getIcon("interfaces.png"), createServicesComponent());
		tabbedPane.addTab("JAXB", Resources.getIcon("xml.png"), createJAXBComponent());
		tabbedPane.addTab("Properties", Resources.getIcon("property.png"), createPropertiesComponent());

		final var closeButton = new JButton("Close");
		closeButton.addActionListener(e -> dispose());

		final var buttonPanel = new ControlPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(closeButton);

		final var contentPane = new ControlPanel(new BorderLayout());
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		setContentPane(contentPane);

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
		} catch (final IOException | DataBindingException ex) {
			log.log(Level.SEVERE, ex, ex::getMessage);
			xml = ex.getMessage();
		}

		final var textArea = new JTextArea(xml);
		textArea.setEditable(false);

		final var descrLabel = new JLabel("Using the deprected JAXB module:");
		descrLabel.setLabelFor(textArea);

		final var panel = new ControlPanel(new BorderLayout(0, 8));
		panel.add(descrLabel, BorderLayout.NORTH);
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

		return panel;
	}

	@SuppressWarnings("unchecked")
	private JComponent createServicesComponent() {
		final var servicesComboBox = new JComboBox<ServiceLoader.Provider<GreetingsHandler>>(new ServiceProviderComboBoxModel());
		servicesComboBox.setRenderer(new ServiceProviderListCellRenderer<GreetingsHandler>());
		if (servicesComboBox.getItemCount() > 0) servicesComboBox.setSelectedIndex(0);

		final var servicesLabel = new JLabel("Services:");
		servicesLabel.setLabelFor(servicesComboBox);

		final var messageTextField = new JTextField("Hello World!");

		final var messageLabel = new JLabel("Message:");
		messageLabel.setLabelFor(messageTextField);

		final var sendButton = new JButton("Send Message");
		sendButton.addActionListener(e -> ((ServiceProviderComboBoxModel) servicesComboBox.getModel()).getSelectedProvider() //
			.ifPresent(p -> sendMessage(p, messageTextField.getText())));

		final var panel = new ControlPanel(new GridBagLayout());
		panel.add(servicesLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		panel.add(servicesComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
		panel.add(messageLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		panel.add(messageTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0));
		panel.add(sendButton, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));

		return panel;
	}

	private void sendMessage(final ServiceLoader.Provider<GreetingsHandler> serviceProvider, final String message) {
		if (serviceProvider != null) {
			serviceProvider.get().sendGreetings(Greetings.createMessage(message).withSource(getTitle()));
		}
	}

	private static JComponent createModulesComponent() {
		final var tree = new JTree(new ModulesTreeModel());
		tree.setCellRenderer(new ModulesTreeCellRenderer());

		final var descrLabel = new JLabel("Currently available Java modules:");
		descrLabel.setLabelFor(tree);

		final var panel = new ControlPanel(new BorderLayout(0, 8));
		panel.add(descrLabel, BorderLayout.NORTH);
		panel.add(new JScrollPane(tree), BorderLayout.CENTER);

		return panel;
	}

	private static JComponent createPropertiesComponent() {
		final var table = new JTable(new SystemPropertiesTableModel());

		final var descrLabel = new JLabel("Currenty set Java system properties:");
		descrLabel.setLabelFor(table);

		final var panel = new ControlPanel(new BorderLayout(0, 8));
		panel.add(descrLabel, BorderLayout.NORTH);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);

		return panel;
	}
}