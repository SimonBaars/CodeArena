package nl.sandersimon.clonedetection.editor;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.minecraft.client.Minecraft;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.model.Location;
import nl.sandersimon.clonedetection.model.MetricProblem;
import nl.sandersimon.clonedetection.thread.ProblemDetectionThread;

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
				CodeEditor e = new CodeEditor(cloneClass, location.file(), location.getBeginLine(), location.getEndLine(), j, cloneClass.size());
				CloneDetection.get().openEditors.add(e);
				e.setVisible(true);
			});
		}
	}
}
