package com.simonbaars.codearena.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.editor.CodeEditorMaker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MetricProblem implements Comparable<MetricProblem>{

	private String metric;
	private int lines;
	private List<Location> locations = new ArrayList<>();
	
	public MetricProblem(String metric, int lines) {
		super();
		this.metric = metric;
		this.lines = lines;
	}

	public MetricProblem() {
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
	public int compareTo(MetricProblem o) {
		return Integer.compare(volume(), o.volume());
	}

	public void add(Location construct) {
		locations.add(construct);
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
		String javaSrc = "src/main/java";
		final String folderIn = new File(fileName).getParent();
		int javaSrcPath = folderIn.lastIndexOf(javaSrc);
		if(javaSrcPath == -1) {
			javaSrc = "src";
			javaSrcPath = folderIn.lastIndexOf(javaSrc);
			if(javaSrcPath == -1)
				return folderIn.replace('/', '.');
		}
		return folderIn.substring(javaSrcPath+javaSrc.length()+1).replace('/', '.');
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}
}
