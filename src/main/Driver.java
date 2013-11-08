package main;

import gui.MainWindow;

import javax.swing.SwingUtilities;

import classes.InvertedIndex;

public class Driver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainWindow ex = new MainWindow(new InvertedIndex());
				ex.setVisible(true);
			}
		});

	}

}
