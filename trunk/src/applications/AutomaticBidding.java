package applications;

import it.polimi.data.io.NotADirectoryException;

import java.io.File;

public class AutomaticBidding {
	public static void main(String[] args) {
		System.err.close();
		if(args.length < 1 || !(args[0].equals("extractAbstracts")||args[0].equals("getBids"))){
			System.out.println("ERROR: no supported operations specified");
			System.out.println("Use:");
			System.out.println(" 'extractAbstracts' to get the abstracts from the fulltext");
			System.out.println(" 'getBids' to get the sggestion");
		}
		
		if(args[0].equals("extractAbstracts")){
			String root = "papers/fulltext/submissions/";
			
			int charactersToKeep;
			ExtractAbstracts extractor;
			if(args.length == 2){
				charactersToKeep = Integer.parseInt(args[1]);
				extractor = new ExtractAbstracts(charactersToKeep);
			}
			else{
				extractor = new ExtractAbstracts();
			}
			extractor.convert(new File(root));
			return;
		}
		
		if(args[0].equals("getBids")){
			try {
				new MakeBids().getBids();
			} catch (NotADirectoryException e) {
			}
			return;
		}
	}

}
