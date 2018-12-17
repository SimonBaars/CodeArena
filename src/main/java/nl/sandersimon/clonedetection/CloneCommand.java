package nl.sandersimon.clonedetection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.thread.CloneDetectionThread;
import scala.actors.threadpool.Arrays;

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
		return "/codeclones <(optional) project_name> <(optional) clone type>";
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
			sender.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.BLUE, "Please enter the number of the project you'd like to search for code clones:"));
			String[] projects = new File(SavePaths.getProjectFolder()).list();
			IntStream.range(0, projects.length).forEach(i -> sender.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.WHITE, "["+(i+1)+"] "+projects[i])));
			CloneDetection.dialoge = 1;
			return;
		}
		CloneDetectionThread.startWorker(server, sender, args[0]);
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		return Arrays.asList(new File(SavePaths.getProjectFolder()).list());
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}
}
