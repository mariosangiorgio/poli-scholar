package applications;

import it.polimi.utils.AbstractNotFoundException;
import it.polimi.utils.PDFEncryptedException;
import it.polimi.utils.TextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExtractAbstracts {
	private TextStripper stripper = new TextStripper(true);
	
	public static void main(String[] args) throws IOException,
			PDFEncryptedException {
		//TODO: refactor in order to reveive the root folder as parameter
		String root = "papers/fulltext/submissions/";
		new ExtractAbstracts().convert(root);
	}

	public void convert(String root){
		System.out.println("Converting the content of: "+root);
		File sourceFolder = new File(root);
		File destinationFolder = new File(root.replace("papers/fulltext/","papers/abstracts/"));
		if (!destinationFolder.exists()) {
			destinationFolder.mkdirs();
		}

		for (String filename : sourceFolder.list()) {
			if (filename.startsWith(".")) {
				continue;
			}
			File file = new File(root +"/"+ filename);
			if (file.isDirectory()) {
				convert(root + filename);
				continue;
			}

			String paperAbstract;
			try {
				String outputFilename = root + "/"
						+ filename.replace(".pdf", ".txt");
				outputFilename = outputFilename.replace("papers/fulltext/","papers/abstracts/");
				if(new File(outputFilename).exists()){
					continue;
				}
				paperAbstract = stripper.getContent(file);
				FileWriter extrectedAbstract = new FileWriter(outputFilename);
				extrectedAbstract.write(paperAbstract);
				extrectedAbstract.close();
			} catch (AbstractNotFoundException e) {
				System.out.println("***** Abstract not found in " + filename);
			} catch(IOException e){
				System.out.println("***** IOException processing " + filename);
			} catch(PDFEncryptedException e){
				System.out.println("***** The document is encrypted: " + filename);
			}
		}
	}
}
