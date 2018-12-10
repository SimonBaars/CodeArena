package nl.sandersimon.clonedetection.editor;

import java.io.File;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import nl.sandersimon.clonedetection.model.Location;

public class CodeEditorMaker {

	private CodeEditorMaker() {}

	public static void create(List<Location> locs) {
		for(Location loc : locs) {
			SwingUtilities.invokeLater(() -> {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//						UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
				} catch (Exception e) {
					e.printStackTrace();
				}
				new CodeEditor(new File("/home/simon/.clone/projects/ProjectWithDuplicateBetweenFiles/src/nl/sandersimon/dup3/Main.java")).setVisible(true);
			});
		}
	}
}
