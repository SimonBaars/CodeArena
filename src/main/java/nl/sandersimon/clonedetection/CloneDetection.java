package nl.sandersimon.clonedetection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import nl.sandersimon.clonedetection.common.ResourceCommons;
import nl.sandersimon.clonedetection.common.TestingCommons;
import scala.actors.threadpool.Arrays;

@Mod(modid = CloneDetection.MODID, name = CloneDetection.NAME, version = CloneDetection.VERSION)
public class CloneDetection
{
	public static final String MODID = "clonedetection";
	public static final String NAME = "Clone Detection";
	public static final String VERSION = "1.0";

	Process rascal;
	BufferedWriter rascalOut;
	InputStreamReader rascalIn;
	private static CloneDetection cloneDetection;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		cloneDetection = this;
		ResourceCommons.extractResources();
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
		event.registerServerCommand(new CloneCommand());
	}

	public static CloneDetection get() {
		return cloneDetection;
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		try {
			System.out.println("Starting Rascal..");
			rascal = getProcess("java -jar "+ResourceCommons.getResource("Rascal.jar").getAbsolutePath(), ResourceCommons.getResource("rascal"));
			executeRascal("import packagename::ClassName;");
			System.out.println("Rascal Started!");
		} catch (IOException e) {
			throw new RuntimeException("Rascal could not be started!", e);
		}
	}

	private Process getProcess(String command, File dir) throws IOException {
		String[] com;
		if(TestingCommons.getOS().isUnix())
			com = new String[]{"bash", "-c", command};
		else 
			com = command.split(" ");
		ProcessBuilder prb = new ProcessBuilder(com);
		prb.directory(dir);
		Process pr = prb.start();
		rascalOut = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));
		rascalIn = new InputStreamReader(pr.getInputStream());
		waitUntilExecuted();
		return pr;
	}

	public List<String> executeRascal(String statement){
		return executeRascal(statement, getRascalReadyState());
	}
	
	public List<String> executeRascal(String statement, char till) {
		System.out.println("Executing Rascal: "+statement);
		try {
			rascalOut.write(statement);
			rascalOut.newLine();
			rascalOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return waitUntilExecuted(till);
	}

	public String executeSingleLineRascal(String statement) {
		return executeRascal(statement).get(1);
	}
	
	public String executeTill(String statement, char till) {
		return executeRascal(statement, till).get(0);
	}
	
	public List<String> waitUntilExecuted() {
		return waitUntilExecuted(getRascalReadyState());
	}

	private char getRascalReadyState() {
		return '>';
	}

	public List<String> waitUntilExecuted(char till) {
		List<String> lines = new ArrayList<>();
		outerloop: while(true) {
			StringBuilder buffer = new StringBuilder();
			try {
				while(rascalIn.ready()) {
					int read = rascalIn.read();
					System.out.print((char)read+"");
					if((char)read == till) {
						lines.add(buffer.toString());
						break outerloop;
					}
					if(read == '\n') {
						lines.add(buffer.toString());
						buffer.setLength(0);
					} else buffer.append((char)read);
				}
				Thread.sleep(400);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		return lines;
	}
	
	public String readBuffer(int bufferSize) {
		char[] cbuf = new char[bufferSize];
		try {
			int read = rascalIn.read(cbuf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(cbuf);
	}
}
