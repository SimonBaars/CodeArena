import org.junit.Test;

import com.simonbaars.codearena.model.Location;
import com.simonbaars.codearena.model.MetricProblem;

public class PackageTest {
	@Test
	public void testPackage() {
		MetricProblem c = new MetricProblem();
		c.getLocations().add(new Location("/home/simon/.clone/projects/ProjectWithDuplicateBetweenFiles/src/nl/sandersimon/dup3/Main2.java"));
		System.out.println(c.getPackage());
	}
}
