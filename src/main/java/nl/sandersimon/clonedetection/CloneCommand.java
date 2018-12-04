package nl.sandersimon.clonedetection;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.thread.CloneDetectionThread;

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
			sender.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.BLUE, "Please choose the project you'd like to search for code clones:"));
			return;
		}
		CloneDetectionThread.startWorker(sender, args[0]);
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
}
