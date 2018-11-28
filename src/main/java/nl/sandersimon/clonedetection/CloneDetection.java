package nl.sandersimon.clonedetection;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.Logger;
import org.rascalmpl.library.experiments.Compiler.Commands.Bootstrap;
import org.rascalmpl.library.experiments.Compiler.Commands.RascalC;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = CloneDetection.MODID, name = CloneDetection.NAME, version = CloneDetection.VERSION)
public class CloneDetection
{
	public static final String MODID = "clonedetection";
	public static final String NAME = "Clone Detection";
	public static final String VERSION = "1.0";
	
	private static Process childProcess;

	private static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		System.out.println("PREINIT");
		Thread thread = new Thread() {
			  public void run() {
				  System.out.println("============Sysout from thread============");
				  try {
					Bootstrap.main(new String[] {"bin/","latest","latest","/home/simon/rascal/CloneDetection/rascal-master/","/home/simon/rascal/CloneDetection/rascal-comp/", "/tmp"});
					System.out.println("============Bootstrapping DONE!============");
				  } catch (Exception e) {
					e.printStackTrace();
				}
				  System.out.println("============GOING TO MAIN============");
				  RascalC.main(new String[] {});
			  }
			 };
		thread.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("DONE");
	}

	/*@EventHandler
	public void init(FMLInitializationEvent event)
	{
		System.out.println("DOING STUFF");
		try {
			Bootstrap.main(new String[] {"","","","",""});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			IValueFactory vf = ValueFactoryFactory.getValueFactory();
			CommandOptions cmdOpts = new CommandOptions("rascalc");

			cmdOpts.pathConfigOptions()

			.locOption("reloc")       
			.locDefault(cmdOpts.getDefaultRelocLocation())
			.help("Relocate source locations")

			.boolOption("noLinking")	
			.help("Do not link compiled modules")

			.strOption("apigen")
			.strDefault("")
			.strDefault("")
			.help("Package name for generating api for Java -> Rascal")

			.locOption("src-gen")
			.help("Package name for generating api for Java -> Rascal")

			.locOption("src-gen")
			.locDefault((co) -> (ISourceLocation) co.getCommandLocsOption("src").get(0))
			.help("Target directory for generated source code")

			.boolOption("help") 		
			.help("Print help message for this command")

			.boolOption("trace") 		
			.help("Print Rascal functions during execution of compiler")

			.boolOption("profile") 		
			.help("Profile execution of compiler")

			.boolOption("optimize")
			.boolDefault(true)
			.help("Apply code optimizations")

			.boolOption("enableAsserts")
			.help("Enable checking of assertions")

			.boolOption("verbose")
			.help("Make the compiler verbose")

			.modules("List of module names to be compiled or a single location for a directory to compile all modules from")

			.handleArgs(new String[0]);

			ISourceLocation srcGen = cmdOpts.getCommandLocOption("src-gen");

			PathConfig pcfg = cmdOpts.getPathConfig();
			IKernel kernel = Java2Rascal.Builder.bridge(vf, pcfg, IKernel.class)
					.trace(cmdOpts.getCommandBoolOption("trace"))
					.profile(cmdOpts.getCommandBoolOption("profile"))
					.verbose(cmdOpts.getCommandBoolOption("verbose"))
					.build();


			boolean ok = true;

			IList modules = cmdOpts.getModules();

			IList programs = kernel.compileAndLink(modules, pcfg.asConstructor(kernel),
					kernel.kw_compileAndLink()
					.reloc(cmdOpts.getCommandLocOption("reloc"))
					);

			ok = handleMessages(programs, pcfg);
			if(!ok){
				System.out.println("The system has deemed it NOT OK");
			}
			String pckg = cmdOpts.getCommandStringOption("apigen");
			if(!pckg.isEmpty()){


				for(IValue mod : modules){
					String moduleName = ((IString) mod).getValue();
					ISourceLocation binary = Rascal.findBinary(cmdOpts.getCommandLocOption("bin"), moduleName);

					RVMExecutable exec = RVMExecutable.read(binary);

					try {
						String api = ApiGen.generate(exec, moduleName, pckg);
						String modulePath;

						int i = moduleName.lastIndexOf("::");
						if(i >= 0){
							modulePath = moduleName.substring(0, i+2) + "I" + moduleName.substring(i+2);
							modulePath = modulePath.replaceAll("::",  "/");
						} else {
							modulePath = "I" + moduleName;
						}

						String path = srcGen.getPath() + "/" + modulePath + ".java";
						ISourceLocation apiLoc = URIUtil.correctLocation(srcGen.getScheme(), srcGen.getAuthority(), path);
						System.out.println(apiLoc);
						System.out.println(api);
						OutputStream apiOut = URIResolverRegistry.getInstance().getOutputStream(apiLoc, false);
						apiOut.write(api.getBytes());
						apiOut.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {}
	}

	public static boolean handleMessages(IList programs, PathConfig pcfg) {
		boolean failed = false;

		for(IValue iprogram : programs){
			IConstructor program = (IConstructor) iprogram;
			if (program.has("main_module")) {
				program = (IConstructor) program.get("main_module");
			}

			if (!program.has("messages")) {
				throw new InternalCompilerError("unexpected output of compiler, has no messages field");
			}

			ISet messages = (ISet) program.get("messages");

			failed |= handleMessages(pcfg, messages);
		}

		return failed;
	}

	public static boolean handleMessages(PathConfig pcfg, ISet messages) {
		boolean failed = false;
		int maxLine = 0;
		int maxColumn = 0;

		for (IValue val : messages) {
			ISourceLocation loc = (ISourceLocation) ((IConstructor) val).get("at");
			maxLine = Math.max(loc.getBeginLine(), maxLine);
			maxColumn = Math.max(loc.getBeginColumn(), maxColumn);
		}


		int lineWidth = (int) Math.log10(maxLine + 1) + 1;
		int colWidth = (int) Math.log10(maxColumn + 1) + 1;

		synchronized (System.err) {
			for (IValue val : messages) {
				IConstructor msg = (IConstructor) val;
				if (msg.getName().equals("error")) {
					failed = true;
				}

				ISourceLocation loc = (ISourceLocation) msg.get("at");
				int col = loc.getBeginColumn();
				int line = loc.getBeginLine();

				System.err.println(msg.getName() + "@" + abbreviate(loc, pcfg) 
				+ ":" 
				+ String.format("%0" + lineWidth + "d", line)
				+ ":"
				+ String.format("%0" + colWidth + "d", col)
				+ ": "
				+ ((IString) msg.get("msg")).getValue()
						);
			}
		}

		return !failed;
	}

	private static String abbreviate(ISourceLocation loc, PathConfig pcfg) {
		for (IValue src : pcfg.getSrcs()) {
			String path = ((ISourceLocation) src).getURI().getPath();

			if (loc.getURI().getPath().startsWith(path)) {
				return loc.getURI().getPath().substring(path.length()); 
			}
		}

		return loc.getURI().getPath();
	}
	
	/**
	 * COMPILER BLAH BLAH
	 * @param command
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	
	/*private static int runChildProcess(String[] command) throws IOException, InterruptedException {
        synchronized (Bootstrap.class) {
            childProcess = new ProcessBuilder(command).inheritIO().start();
            childProcess.waitFor();
            int exitValue = childProcess.exitValue();
            if (exitValue != 0) {
                System.out.println("Command failed: " + Arrays.stream(command).reduce("", ((s,e) -> s + (e.toString() + " "))));
            }
            return exitValue;
        }
    }
	
	private static String maxMemory = "-Xmx2G";
	private static int runRascalCompiler(String classPath, String... arguments) throws IOException, InterruptedException {
        String[] javaCmd = new String[] {"java", "-cp", classPath, maxMemory, "-Dfile.encoding=UTF-8", /*"-Xdebug -Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=n","org.rascalmpl.library.experiments.Compiler.Commands.RascalC" };
        return runChildProcess(concat(javaCmd, arguments));
    }
	
	private static String[] concat(String[]... arrays) {
        return Stream.of(arrays).flatMap(Stream::of).toArray(sz -> new String[sz]);
    }
	
	private static Path phaseFolder(int phase, Path tmp) {
        Path result = tmp.resolve("phase" + phase);
        result.toFile().mkdir();
        return result;
    }
    
    private static Path phaseTestFolder(int phase, Path tmp) {
        Path result = tmp.resolve("phase-test" + phase);
        result.toFile().mkdir();
        return result;
    }
    
    private static void compileModule(int phase, String classPath, String boot, String sourcePath, Path phaseResult,
            String module, String reloc) throws IOException, InterruptedException, BootstrapMessage {
        
        String[] paths;
        paths = phase >= 2 ? new String [] { "--bin", phaseResult.toAbsolutePath().toString(), "--src", sourcePath, "--boot", boot , "--reloc", reloc }
                           : new String [] { "--bin", phaseResult.toAbsolutePath().toString(), "--src", sourcePath, "--boot", boot};
        String[] otherArgs = new String[] {module};

        if (runRascalCompiler(classPath, concat(paths, otherArgs)) != 0) {
            throw new BootstrapMessage(phase);
        }
    }

	private static Path compilePhase(Path tmp, int phase, String sourcePath, String classPath, String bootPath, String testClassPath, String reloc, Path targetFolder) throws Exception {
	      Path phaseResult = phaseFolder(phase, tmp);
	      Path testResults = phaseTestFolder(phase, tmp);

	      compileModule   (phase, classPath, bootPath, sourcePath, phaseResult, "lang::rascal::boot::Kernel", reloc);

	      if (phase == 1) {
	          // the new parser generator would refer to classes which may not exist yet. Until stage 2 we still run against this old version
	          // we now copy an old version of the generator to be used in phase 2.
	          copyParserGenerator(jarFileSystem(classPath).getRootDirectories().iterator().next(), phaseResult);
	      }
	      else {
	          time("- compile ParserGenerator", () -> compileModule   (phase, classPath, bootPath, sourcePath, phaseResult, "lang::rascal::grammar::ParserGenerator", reloc));
	      }
	      
	      if(phase == 2){
	          time("- generate and compile RascalParser", () -> generateAndCompileRascalParser(phase, classPath, sourcePath, bootPath, phaseResult, targetFolder));
	      }

	      if (phase >= 2) {
	          // phase 1 tests often fail for no other reason than an incompatibility.
	          time("- compile simple tests",           () -> compileTests    (phase, classPath, phaseResult.toAbsolutePath().toString(), sourcePath, testResults, testModules));
	          time("- run simple tests",               () -> runTests        (phase, testClassPath, phaseResult.toAbsolutePath().toString(), sourcePath, testResults, testModules));
	      }
	      
	      if (phase > 2) {
	          // phase 2 tests can not succeed in case the parser generator changed, so we can only test this after phase 3 has completed
	          time("- compile syntax tests",           () -> compileTests    (phase, classPath, phaseResult.toAbsolutePath().toString(), sourcePath, testResults, syntaxTestModules));
	          time("- run syntax tests",               () -> runTests        (phase, testClassPath, phaseResult.toAbsolutePath().toString(), sourcePath, testResults, syntaxTestModules));
	      }
	      
	      return phaseResult;
	    }*/

}
