import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import nl.sandersimon.clonedetection.CloneDetection;

public class RascalConsole {
	@Test
	public void startConsole() {
		CloneDetection d = new CloneDetection();
		d.preInit(null);
		d.init(null);
		Scanner scanner = new Scanner(System.in);
		while(true) {
			List<String> output = d.executeRascal(scanner.nextLine());
			for(String o : output) {
				System.out.println(o);
			}
		}
	}
}
