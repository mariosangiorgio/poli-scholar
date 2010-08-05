package it.polimi.data.io;

import it.polimi.bidding.Paper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

public class PaperLoader {
	public static Collection<Paper> getFilesContent(File directory)
			throws NotADirectoryException {
		if (!directory.isDirectory()) {
			throw new NotADirectoryException();
		}
		Collection<Paper> contents = new Vector<Paper>();

		for (File f : directory.listFiles()) {
			if (f.isFile() && !f.isHidden() && f.getName().endsWith(".txt")) {
				try {
					StringBuffer content = new StringBuffer();
					String line = null;

					FileReader reader = new FileReader(f);
					BufferedReader bufferedReader = new BufferedReader(reader);
					while ((line = bufferedReader.readLine()) != null) {
						content.append(line + "\n");
					}
					
					Paper paper = new Paper(f.getName(), content.toString());
					contents.add(paper);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return contents;
	}
	
	public static Collection<File> getSubDirectories(File directory)
			throws NotADirectoryException{
		Collection<File> subDirectories = new Vector<File>();
		
		if(!directory.isDirectory()){
			throw new NotADirectoryException();
		}
		
		for(File f : directory.listFiles()){
			if(f.isDirectory() && ! f.isHidden()){
				subDirectories.add(f);
			}
		}
		return subDirectories;
	}
}
