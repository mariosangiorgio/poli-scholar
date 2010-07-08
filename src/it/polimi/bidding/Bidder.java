package it.polimi.bidding;

import it.polimi.utils.TextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

public abstract class Bidder implements Serializable{
	private static final long serialVersionUID = 7609771369038960694L;
	
	public void save(String filename) throws IOException{
		FileOutputStream file = new FileOutputStream(filename);
		ObjectOutputStream outputStream = new ObjectOutputStream(file);
		
		outputStream.writeObject(this);
		
		outputStream.close();
		file.close();
	}
	
	static public Bidder load(String filename) throws IOException, ClassNotFoundException{
		Bidder bidder = null;
		
		FileInputStream file = new FileInputStream(filename);
		ObjectInputStream inputStream = new ObjectInputStream(file);
		
		bidder = (Bidder) inputStream.readObject();
		
		inputStream.close();
		file.close();
		
		return bidder;
	}

	abstract public void train(String pathToReviewers);

	abstract public String getReviewer(String documentContent);

	public Collection<Bidding> getReviewers(String pathToSubmissions) {
		Collection<Bidding> biddings = new Vector<Bidding>();
		
		File submissionsFolder = new File(pathToSubmissions);
		for (String documentName : submissionsFolder.list()) {
			File document = new File(pathToSubmissions + "/" + documentName);

			if (!documentName.startsWith(".") && document.isFile()) {
				try {
					System.out.println(documentName);
					String fullText = (new TextStripper()).getFullText(document);
					String reviewer = getReviewer(fullText);
					System.out.println("Selected reviewer:\t"+reviewer+"\n\n");
					
					biddings.add(new Bidding(documentName, reviewer));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return biddings;
	}
}
