package name.ulbricht.jigsaw.application;

import javax.swing.SwingUtilities;

import name.ulbricht.jigsaw.application.gui.MainWindow;

public final class Main {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(MainWindow::initialize);
	}
}
