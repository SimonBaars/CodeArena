import org.junit.Test;

import nl.sandersimon.clonedetection.thread.CloneDetectionThread;

public class TestLocConverter {

	@Test
	public void test() {
		String input = "[<8,[|java+compilationUnit:///home/simon/.clone/projects/ProjectWithDuplicateBetweenFiles/src/nl/sandersimon/dup3/Main2.java|(0,436,<12,0>,<19,1>),|java+compilationUnit:///home/simon/.clone/projects/ProjectWithDuplicateBetweenFiles/src/nl/sandersimon/dup3/Main.java|(0,0,<14,0>,<21,0>)]>]";
		new CloneDetectionThread("", false).populateResult(input);
		
	}

}