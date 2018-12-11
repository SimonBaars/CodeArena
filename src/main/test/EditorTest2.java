import org.junit.Test;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSenderWrapper;
import nl.sandersimon.clonedetection.CloneCommand;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.editor.CodeEditorMaker;
import nl.sandersimon.clonedetection.thread.CloneDetectionThread;

public class EditorTest2 {

	@Test
	public void test() throws InterruptedException {
		CloneDetection d = new CloneDetection();
		d.preInit(null);
		d.init(null);
		CloneCommand c = new CloneCommand();
		try {
			c.execute(null, new CommandSenderWrapper(null, null, null, null, null, false), new String[] {"ProjectWithDuplicateBetweenFiles"});
		} catch (CommandException e) {
			e.printStackTrace();
		}
		while(CloneDetectionThread.getWorker().isAlive())
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		CodeEditorMaker.create(d.getClones().get(0));
		
		Thread.sleep(100000);
	}

}
