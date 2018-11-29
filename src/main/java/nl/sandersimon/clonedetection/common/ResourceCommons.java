package nl.sandersimon.clonedetection.common;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class ResourceCommons {
	
	private ResourceCommons() {}
	
	public static File getResource(String path) {
		if(path.length()>0 && path.charAt(0) == File.separatorChar)
			path = path.substring(1);
		return new File(SavePaths.getResourceFolder()+path);
	}
	
	public static void extractResources() {
		extractResources("");
	}
	
	public static void extractResources(String path) {
		try {
			File resource = getResource("");
			if(!resource.exists())
				extractResources(ResourceCommons.class, path);
		} catch (Exception e) {
			throw new RuntimeException("Resources could not be extracted!", e);
		}
	}
	
	public static void extractResources(@SuppressWarnings("rawtypes") Class protectionDomainProvider, String path) throws IOException {
		final File jarFile = new File(protectionDomainProvider.getProtectionDomain().getCodeSource().getLocation().getPath());
		if(jarFile.isFile() && jarFile.getName().endsWith(".jar"))
			extractJar(jarFile, path);
		else {
			URL resourceUrl = null;
			resourceUrl = protectionDomainProvider.getResource(File.separator+path);
			if(resourceUrl == null)
				resourceUrl = protectionDomainProvider.getClassLoader().getResource(File.separator+path);
			copyPathToResourcesFolder(Paths.get(URLDecoder.decode(new File(resourceUrl.getFile()).getAbsolutePath(), "UTF-8")), path);
		}
	}
	
	private static void extractJar(File p, String path) throws IOException {
		Map<String, String> env = new HashMap<>(); 
		URI uri = URI.create("jar:" + p.toPath().toUri());
		try (FileSystem fs = FileSystems.newFileSystem(uri, env))
		{
			Path jarRoot = fs.getPath(File.separator + path);
			copyPathToResourcesFolder(jarRoot, path);
		}
	}

	private static void copyPathToResourcesFolder(Path jarRoot, String path) throws IOException {
		Files.walkFileTree(jarRoot, new SimpleFileVisitor<Path>() {
		    @Override
		    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
		        // Make sure that we conserve the hierachy of files and folders inside the zip
		        Path relativePathInZip = jarRoot.relativize(filePath);
		        String replace = relativePathInZip.toString().replace(".."+File.separator, "");
		        if(replace.indexOf(path) != -1)
		        	replace = replace.substring(replace.indexOf(path)+path.length());
				File file = new File(SavePaths.getResourceFolder()+replace);
				Path targetPath = file.toPath();
		        Files.createDirectories(targetPath.getParent());
		        // And extract the file
		        Files.copy(filePath, targetPath);

		        return FileVisitResult.CONTINUE;
		    }
		});
	}
}