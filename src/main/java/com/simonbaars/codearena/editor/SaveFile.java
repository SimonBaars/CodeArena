package com.simonbaars.codearena.editor;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.simonbaars.codearena.common.TestingCommons;

public class SaveFile extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4940861690942712089L;
	private File file;
	private JTextArea textArea;
	private JLabel statusBar;
	
	public SaveFile(File file, JTextArea textArea, JLabel statusBar) {
		super();
		this.file = file;
		this.textArea = textArea;
		this.statusBar = statusBar;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TestingCommons.writeStringToFile(file, textArea.getText());
			statusBar.setText("File successfully saved!");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
