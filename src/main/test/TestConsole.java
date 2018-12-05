import org.junit.Test;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSenderWrapper;
import nl.sandersimon.clonedetection.CloneCommand;
import nl.sandersimon.clonedetection.CloneDetection;

public class TestConsole {

	@Test
	public void test() {
		CloneDetection d = new CloneDetection();
		d.preInit(null);
		d.init(null);
		CloneCommand c = new CloneCommand();
		try {
			c.execute(null, new CommandSenderWrapper(null, null, null, null, null, false), new String[] {"smallsql0.21_src"});
		} catch (CommandException e) {
			e.printStackTrace();
		}
	}

}
