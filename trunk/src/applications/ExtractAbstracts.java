package applications;
import it.polimi.utils.AbstractNotFoundException;
import it.polimi.utils.PDFEncryptedException;
import it.polimi.utils.TextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExtractAbstracts {
	public static void main(String[] args) throws IOException,
			PDFEncryptedException {
		TextStripper stripper = new TextStripper(true);

		String root = "automaticBidding/submissions/";
		File sourceFolder = new File(root);
		File destinationFolder = new File("plaintext/" + root);
		if(!destinationFolder.exists()){
			destinationFolder.mkdirs();
		}

		for (String filename : sourceFolder.list()) {
			System.out.println(filename);

			if (filename.startsWith(".")) {
				continue;
			}

			File file = new File(root + filename);

			String paperAbstract;
			try {
				paperAbstract = stripper.getContent(file);
				FileWriter extrectedAbstract = new FileWriter("plaintext/"
						+ root + filename.replace(".pdf", ".txt"));
				extrectedAbstract.write(paperAbstract);
				extrectedAbstract.close();
			} catch (AbstractNotFoundException e) {
				System.err.println("***** Abstract not found in " + filename);
			}
		}
	}
}
