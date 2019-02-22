package nl.sandersimon.clonedetection.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.editor.CodeEditorMaker;

public class CloneClass implements Comparable<CloneClass>{

	private int lines;
	private List<Location> locations = new ArrayList<>();
	
	public CloneClass(CloneMetrics metrics) {
		super();
		metrics.getTotalNumberOfCloneClasses().incrementScore();
	}
	
	public CloneClass() {
		super();
	}

	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	@Override
	public int compareTo(CloneClass o) {
		return Integer.compare(volume(), o.volume());
	}

	public void add(CloneMetrics metrics, Location construct) {
		locations.add(construct);
		metrics.getTotalNumberOfClones().incrementScore();
		metrics.getTotalCloneVolume().increaseScore(lines);
		CloneScore mostLines = metrics.getMostLinesCloneClass();
		if(lines > mostLines.getScorePoints())
			mostLines.setScorePoints(lines);
		CloneScore mostOccurrent = metrics.getMostOccurrentClone();
		if(size() > mostOccurrent.getScorePoints())
			mostOccurrent.setScorePoints(size());
		CloneScore highestVolume = metrics.getBiggestCloneClass();
		if(volume() > highestVolume.getScorePoints())
			highestVolume.setScorePoints(volume());
		if(locations.size() == 1) {
			final String myPackage = getPackage();
			if(!CloneDetection.packages.contains(myPackage)) {
				CloneDetection.packages.add(myPackage);
				ItemStack itemStackIn = new ItemStack(Items.DIAMOND, 1);
				itemStackIn.setStackDisplayName(myPackage);
				Minecraft.getMinecraft().player.inventory.addItemStackToInventory(itemStackIn);
			}
		}
	}
	
	public int size() {
		return locations.size();
	}

	public Location get(int j) {
		return locations.get(j);
	}

	@Override
	public String toString() {
		return "CloneClass"+System.lineSeparator()+"lines=" + lines + System.lineSeparator()+"volume=" + volume() + System.lineSeparator()+"locations=" + Arrays.toString(locations.toArray());
	}
	
	public int volume() {
		return lines * size();
	}

	public void open() {
		CodeEditorMaker.create(this);
	}

	public String getName() {
		if(size() == 0)
			return "error";
		return get(0).getName();
	}

	public String rascalLocList() {
		return "["+locations.stream().map(Location::rascalFile).collect(Collectors.joining(","))+"]";
	}
	
	public String getPackage() {
		String fileName = locations.get(0).file;
		final String javaSrc = "src/main/java";
		final String folderIn = new File(fileName).getParent();
		int javaSrcPath = folderIn.indexOf(javaSrc);
		if(javaSrcPath == -1) 
			return folderIn.replace(File.pathSeparatorChar, '.');
		return folderIn.substring(javaSrcPath+javaSrc.length()).replace(File.pathSeparatorChar, '.');
	}
}
