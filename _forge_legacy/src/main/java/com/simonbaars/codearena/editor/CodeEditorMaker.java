package com.simonbaars.codearena.editor;

import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.model.MetricProblem;
import com.simonbaars.codearena.thread.ProblemDetectionThread;

import net.minecraft.client.Minecraft;

public class CodeEditorMaker {

	private CodeEditorMaker() {}

	public static void create(MetricProblem cloneClass) {
		ProblemDetectionThread.startWorker(Minecraft.getMinecraft().player, cloneClass, true);
		for(int i = 0; i<cloneClass.size(); i++) {
			final int j = i;
			SwingUtilities.invokeLater(() -> {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				Location location = cloneClass.get(j);
				CodeEditor e = new CodeEditor(cloneClass, location.getFile().toFile(), location.getRange().begin.line, location.getRange().end.line, j, cloneClass.size());
				CloneDetection.get().openEditors.add(e);
				e.setVisible(true);
			});
		}
		CodeEditor.locked = false;
	}
	
	public static void create(File file, String metric) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			CodeEditor e = new CodeEditor(file, false, metric);
					CloneDetection.get().openEditors.add(e);
			e.setVisible(true);
		});
		CodeEditor.locked = false;
	}
}
