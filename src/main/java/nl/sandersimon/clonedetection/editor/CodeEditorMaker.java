package nl.sandersimon.clonedetection.editor;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import nl.sandersimon.clonedetection.model.Location;

public class CodeEditorMaker {

	private CodeEditorMaker() {}

	public static void create(List<Location> locs) {
		for(int i = 0; i<locs.size(); i++) {
			final int j = i;
			SwingUtilities.invokeLater(() -> {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				Location location = locs.get(j);
				new CodeEditor(location.file(), location.getBeginLine(), location.getEndLine(), j, locs.size()).setVisible(true);
			});
		}
	}
}
