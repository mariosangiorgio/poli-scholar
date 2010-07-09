import java.io.File;
import java.io.IOException;

import it.polimi.utils.AbstractNotFoundException;
import it.polimi.utils.PDFEncryptedException;
import it.polimi.utils.TextStripper;


public class Test {
	public static void main(String[] args) throws IOException, PDFEncryptedException{
		TextStripper stripper = new TextStripper(true);
		File rootFolder = new File("automaticBidding/submissions/");
		for(String filename : rootFolder.list()){
			System.out.println(filename);
			
			if(filename.startsWith(".")){
				continue;
			}
			
			File file = new File("automaticBidding/submissions/"+filename);
			
			String paperAbstract;
			try {
				paperAbstract = stripper.getContent(file);
				System.out.println("ABSTRACT");
				System.out.println(paperAbstract);
				System.out.println("\n\n\n");
			} catch (AbstractNotFoundException e) {
				System.err.println("Abstract not found in "+filename);
			}
		}
	}

}
