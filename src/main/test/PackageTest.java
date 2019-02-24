import org.junit.Test;

import nl.sandersimon.clonedetection.model.MetricProblem;
import nl.sandersimon.clonedetection.model.Location;

public class PackageTest {
	@Test
	public void testPackage() {
		MetricProblem c = new MetricProblem();
		c.getLocations().add(new Location("/home/simon/.clone/projects/ProjectWithDuplicateBetweenFiles/src/nl/sandersimon/dup3/Main2.java"));
		System.out.println(c.getPackage());
	}
}
