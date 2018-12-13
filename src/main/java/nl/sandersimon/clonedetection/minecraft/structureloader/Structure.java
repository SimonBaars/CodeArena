package nl.sandersimon.clonedetection.minecraft.structureloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Structure
{
	public static final int STRUCTURE_BLOCK_LIMIT = 100000;

	public int length;
	public int height;
	public int width;

	protected String fileName;
	public InputStream fileStream;
	protected String blockMode;
	protected boolean blockUpdate;

	public Float centerX;
	public Float centerY;
	public Float centerZ;
	protected Vec3d centerPos;

	public Structure(String fileName) 
	{
		this.blockMode = "replace";
		this.blockUpdate = true;
		this.fileName = "/structures/"+fileName+".structure";
		//System.out.println("Registered as "+this.fileName);
		this.fileStream = Structure.class.getResourceAsStream(this.fileName);
	}
	

	public Structure(String fileName, boolean useless) 
	{
		this.blockMode = "replace";
		this.blockUpdate = true;
		this.fileName = "structures/"+fileName+".structure";
		//System.out.println("Registered as "+this.fileName);
		try {
			this.fileStream = new FileInputStream(new File(this.fileName)+".structure");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void readFromFile()
	{

	}
	
	public void doNotReplaceAir(){
		blockMode="overlay";
	}

	public void process(World serverWorld, World world, int posX, int posY, int posZ)
	{

	}

	public Vec3d getCenterPos()
	{
		return this.centerPos;
	}

	protected void initCenterPos()
	{
		int defaultCenterX = (int) (this.length / 2.0F);
		int defaultCenterZ = (int) (this.width / 2.0F);
		if (this.centerX == null) this.centerX = defaultCenterX + 0.5F;
		if (this.centerY == null) this.centerY = 0.0F;
		if (this.centerZ == null) this.centerZ = defaultCenterZ + 0.5F;
		this.centerPos = new Vec3d(this.centerX, this.centerY, this.centerZ);
		//System.out.println("SIZE STRUCTURE: "+width+", "+height+", "+length);
	}
}
