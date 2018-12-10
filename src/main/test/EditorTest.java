import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.junit.Test;

import nl.sandersimon.clonedetection.editor.CodeEditor;

public class EditorTest {

	@Test
	public void test() throws InterruptedException {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//					UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
			} catch (Exception e) {
				e.printStackTrace();
			}
			new CodeEditor(new File("/home/simon/.clone/projects/ProjectWithDuplicateBetweenFiles/src/nl/sandersimon/dup3/Main.java"), 15, 20).setVisible(true);
		});
		
		Thread.sleep(100000);
	}

}
