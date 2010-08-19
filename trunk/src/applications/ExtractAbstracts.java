package applications;

import it.polimi.utils.AbstractNotFoundException;
import it.polimi.utils.PDFEncryptedException;
import it.polimi.utils.TextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExtractAbstracts {
	private TextStripper stripper;
	
	public ExtractAbstracts(int charactersToKeep) {
		stripper = new TextStripper(true,charactersToKeep);
	}
	
	public ExtractAbstracts() {
		stripper = new TextStripper(true);
	}

	public static void main(String[] args) throws IOException,
			PDFEncryptedException {
		String root = "papers/fulltext/submissions/";
		new ExtractAbstracts().convert(new File(root));
	}

	public void convert(File root){
		System.out.println("Converting the content of: "+root.getPath());
		File destinationFolder = new File(root.getPath().replace("papers/fulltext/","papers/abstracts/"));
		if (!destinationFolder.exists()) {
			destinationFolder.mkdirs();
		}
		
		for (File file : root.listFiles()) {
			if (file.isHidden()) {
				continue;
			}
			if (file.isDirectory()) {
				convert(file);
				continue;
			}

			String paperAbstract;
			try {
				String outputFilename = file.getPath().replace(".pdf", ".txt");
				outputFilename = outputFilename.replace("papers/fulltext/","papers/abstracts/");
				if(new File(outputFilename).exists()){
					continue;
				}
				paperAbstract = stripper.getContent(file);
				FileWriter extrectedAbstract = new FileWriter(outputFilename);
				extrectedAbstract.write(paperAbstract);
				extrectedAbstract.close();
			} catch (AbstractNotFoundException e) {
				System.out.println("***** Abstract not found in " + file.getName());
			} catch(IOException e){
				System.out.println("***** IOException processing " + file.getName());
			} catch(PDFEncryptedException e){
				System.out.println("***** The document is encrypted: " + file.getName());
			}
		}
	}
}
