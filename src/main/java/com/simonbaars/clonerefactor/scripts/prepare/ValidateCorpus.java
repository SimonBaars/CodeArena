package com.simonbaars.clonerefactor.scripts.prepare;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.simonbaars.clonerefactor.util.FileUtils;
import com.simonbaars.clonerefactor.util.SavePaths;

public class ValidateCorpus {

	public static void main(String[] args) throws IOException {
		String file = FileUtils.getFileAsString(SavePaths.getApplicationDataFolder()+"projects.txt");
		FileOutputStream fos = new FileOutputStream(new File(SavePaths.getApplicationDataFolder()+"filtered_projects.txt"));
		for(String s : file.split("\n")) {
			int pom = doRequest("https://raw.githubusercontent.com"+s+"/master/pom.xml"), sourceFolder = doRequest("https://github.com"+s+"/tree/master/src/main/java");
			if(pom != 404 && sourceFolder != 404) {
				fos.write(s.getBytes());
				fos.write(System.lineSeparator().getBytes());
			}
		}
		fos.close();
	}
	
	public static int doRequest(String u) throws IOException {
		URL url = new URL(u);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		int res = connection.getResponseCode();
		connection.disconnect();
		return res;
	}

}
