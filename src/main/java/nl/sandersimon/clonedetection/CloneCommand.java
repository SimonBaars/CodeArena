package nl.sandersimon.clonedetection;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import nl.sandersimon.clonedetection.model.Location;

public class CloneCommand implements ICommand {

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "codeclones";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/codeclones <project_name>";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = Lists.<String>newArrayList();
		aliases.add("/codeclones");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length==0) {
			sender.sendMessage(format(net.minecraft.util.text.TextFormatting.RED, "Please specify the project you want to check for code clones, like so: /codeclones <project_name>"));
			return;
		}
		sender.sendMessage(format(net.minecraft.util.text.TextFormatting.DARK_GREEN, "Searching for clones, please wait..."));
		CloneDetection.get().executeTill("calculateCodeDuplication(|file:///home/simon/.clone/projects/"+args[0]+"/|)", '\n');
		String bufferSizeString = CloneDetection.get().waitUntilExecuted('\n').get(0);
		int bufferSize = Integer.parseInt(bufferSizeString);
		String res = CloneDetection.get().readBuffer(bufferSize);
		CloneDetection.get().waitUntilExecuted();
		System.out.println("Amount of locs = "+ populateResult(res).get(0).size());
		System.out.println("DONE!");
	}
	
	public List<List<Location>> populateResult(String res){
		List<List<Location>> locs = new ArrayList<>();
		int listLoc = 1;
		while (listLoc < res.length() && res.charAt(listLoc) == '[') {
			List<Location> loc = new ArrayList<>();
			listLoc = parseList(loc, res, listLoc+1)+1;
			locs.add(loc);
		}
		return locs;
	}

	private int parseList(List<Location> loc, String res, int elementLoc) {
		while(res.charAt(elementLoc) == '|') {
			int indexOf = res.indexOf(')', elementLoc+1);
			if(indexOf == -1)
				break; // Not a valid location
			String stringRep = res.substring(elementLoc+1, indexOf);
			loc.add(Location.construct(stringRep));
			elementLoc += stringRep.length()+3;
		}
		return elementLoc;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}
	
	private TextComponentTranslation format(TextFormatting color, String str, Object... args)
    {
        TextComponentTranslation ret = new TextComponentTranslation(str, args);
        ret.getStyle().setColor(color);
        return ret;
    }
}
