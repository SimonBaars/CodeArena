package nl.sandersimon.clonedetection;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import nl.sandersimon.clonedetection.common.Commons;

public class EndCommand implements ICommand {

	@Override
	public int compareTo(ICommand arg0) {
		return 5;
	}

	@Override
	public String getName() {
		return "end";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/end>";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = Lists.<String>newArrayList();
		aliases.add("/end");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		CloneDetection c = CloneDetection.get();
		if(c.getArena() == null)
			sender.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.RED, "There is no Code Arena running"));
		else c.getArena().endChallengeForAllPlayers();
		
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		return new ArrayList<>();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}
}
