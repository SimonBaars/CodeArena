import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.junit.Test;

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
			new RSTAUIDemoApp(new File("/home/simon/.clone/projects/ProjectWithDuplicateBetweenFiles/src/nl/sandersimon/dup3/Main.java")).setVisible(true);
		});
		
		Thread.sleep(100000);
	}

}
