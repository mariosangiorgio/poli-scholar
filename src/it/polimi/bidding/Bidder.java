package it.polimi.bidding;

import it.polimi.utils.TextStripper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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
					
					String content = null;
					if (documentName.endsWith(".pdf")) {
						TextStripper textStripper = new TextStripper();
						content = textStripper.getContent(document);
						content = textStripper.cleanContent(content);
					}
					if (documentName.endsWith(".txt")) {
						FileReader reader = new FileReader(document);
						BufferedReader fileReader = new BufferedReader(reader);
						StringBuffer buffer = new StringBuffer();
						String temp;
						while((temp = fileReader.readLine()) != null){
							buffer.append(temp+"\n");
						}
						content = buffer.toString();
						reader.close();
						fileReader.close();
					}
					
					String reviewer = getReviewer(content);
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
