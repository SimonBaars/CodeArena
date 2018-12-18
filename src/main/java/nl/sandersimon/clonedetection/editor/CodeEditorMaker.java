package nl.sandersimon.clonedetection.editor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.minecraft.client.Minecraft;
import nl.sandersimon.clonedetection.model.CloneClass;
import nl.sandersimon.clonedetection.model.Location;
import nl.sandersimon.clonedetection.thread.ChangesScannerThread;

public class CodeEditorMaker {

	private CodeEditorMaker() {}

	public static List<CodeEditor> create(CloneClass cloneClass) {
		List<CodeEditor> codeEditors = new ArrayList<>();
		ChangesScannerThread.startWorker(Minecraft.getMinecraft().player, cloneClass, true);
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
				codeEditors.add(e);
				e.setVisible(true);
			});
		}
		return codeEditors;
	}
}
