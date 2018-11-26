package nl.sandersimon.clonedetection;

import java.io.OutputStream;

import org.apache.logging.log4j.Logger;
import org.rascalmpl.library.experiments.Compiler.Commands.CommandOptions;
import org.rascalmpl.library.experiments.Compiler.Commands.Rascal;
import org.rascalmpl.library.experiments.Compiler.RVM.Interpreter.InternalCompilerError;
import org.rascalmpl.library.experiments.Compiler.RVM.Interpreter.RVMExecutable;
import org.rascalmpl.library.experiments.Compiler.RVM.Interpreter.java2rascal.ApiGen;
import org.rascalmpl.library.experiments.Compiler.RVM.Interpreter.java2rascal.Java2Rascal;
import org.rascalmpl.library.lang.rascal.boot.IKernel;
import org.rascalmpl.library.util.PathConfig;
import org.rascalmpl.uri.URIResolverRegistry;
import org.rascalmpl.uri.URIUtil;
import org.rascalmpl.values.ValueFactoryFactory;

import io.usethesource.vallang.IConstructor;
import io.usethesource.vallang.IList;
import io.usethesource.vallang.ISet;
import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IString;
import io.usethesource.vallang.IValue;
import io.usethesource.vallang.IValueFactory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = CloneDetection.MODID, name = CloneDetection.NAME, version = CloneDetection.VERSION)
public class CloneDetection
{
	public static final String MODID = "clonedetection";
	public static final String NAME = "Clone Detection";
	public static final String VERSION = "1.0";

	private static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
	}

	/*@EventHandler
	public void init(FMLInitializationEvent event)
	{
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
				System.exit(1);
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
						System.err.println(apiLoc);
						System.err.println(api);
						OutputStream apiOut = URIResolverRegistry.getInstance().getOutputStream(apiLoc, false);
						apiOut.write(api.getBytes());
						apiOut.close();
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
			System.exit(0);

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
	}*/

}
