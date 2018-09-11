package name.ulbricht.jigsaw.application;

import javax.swing.SwingUtilities;

import name.ulbricht.jigsaw.application.gui.MainFrame;

public final class Main {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(MainFrame::initialize);
	}
}
