package name.ulbricht.jigsaw.application.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.xml.bind.JAXB;

import name.ulbricht.jigsaw.application.xml.Person;
import name.ulbricht.jigsaw.application.xml.Persons;

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
		tabbedPane.addTab("System Properties", createSystemPropertiesComponent());
		tabbedPane.addTab("Modules", createModulesComponent());
		add(tabbedPane, BorderLayout.CENTER);

		pack();
		setLocationByPlatform(true);
	}

	private static Component createJAXBComponent() {
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

	private static Component createModulesComponent() {
		final var tree = new JTree(new ModulesTreeModel());
		tree.setCellRenderer(new ModulesTreeCellRenderer());
		return new JScrollPane(tree);
	}

	private static Component createSystemPropertiesComponent() {
		final var table = new JTable(new SystemPropertiesTableModel());
		return new JScrollPane(table);
	}
}