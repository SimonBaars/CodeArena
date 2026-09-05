import org.junit.Test;

import com.simonbaars.codearena.CloneCommand;
import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.editor.CodeEditorMaker;
import com.simonbaars.codearena.thread.ProblemDetectionThread;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSenderWrapper;

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
		while(ProblemDetectionThread.getWorker().isAlive())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		//CodeEditorMaker.create(d.getClones().get(0));
		
		//Thread.sleep(100000);
	}

}
