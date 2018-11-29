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
import nl.sandersimon.clonedetection.common.ResourceCommons;
import nl.sandersimon.clonedetection.common.TestingCommons;

@Mod(modid = CloneDetection.MODID, name = CloneDetection.NAME, version = CloneDetection.VERSION)
public class CloneDetection
{
	public static final String MODID = "clonedetection";
	public static final String NAME = "Clone Detection";
	public static final String VERSION = "1.0";
	
	Process rascal;
	BufferedWriter rascalOut;
	BufferedReader rascalIn;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		ResourceCommons.extractResources();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		try {
			rascal = getProcess("java -jar "+ResourceCommons.getResource("Rascal.jar").getAbsolutePath(), ResourceCommons.getResource(""));
			System.out.println("RASCAL IS STARTED");
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
		//prb.directory(dir);
		Process pr = prb.start();
		rascalOut = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));
		rascalIn = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		waitUntilExecuted();
		return pr;
	}
	
	//private String executeRascal(String statement) {
	//	rascal.
	//}
	
	private String waitUntilExecuted() {
		List<String> lines = new ArrayList<>();
		String readValue;
		while(true) {
			try {
				if(rascalIn.ready())
					System.out.println("[RASCAL] "+rascalIn.readLine());
				else Thread.sleep(100);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
