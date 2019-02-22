import org.junit.Test;

import nl.sandersimon.clonedetection.model.CloneClass;
import nl.sandersimon.clonedetection.model.Location;

public class PackageTest {
	@Test
	public void testPackage() {
		CloneClass c = new CloneClass();
		c.getLocations().add(new Location("/home/simon/.clone/projects/ProjectWithDuplicateBetweenFiles/src/nl/sandersimon/dup3/Main2.java"));
		System.out.println(c.getPackage());
	}
}
