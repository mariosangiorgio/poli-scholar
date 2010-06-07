package it.polimi.bidding;

import it.polimi.utils.TextStripper;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

public abstract class Bidder {
	abstract public void train(String pathToReviewers);

	abstract public String getReviewer(String documentContent);

	public Collection<Bidding> getReviewers(String pathToSubmissions) {
		Collection<Bidding> biddings = new Vector<Bidding>();
		
		File submissionsFolder = new File(pathToSubmissions);
		for (String documentName : submissionsFolder.list()) {
			File document = new File(pathToSubmissions + "/" + documentName);

			if (!documentName.startsWith(".") && document.isFile()) {
				try {
					String fullText = TextStripper.getFullText(document);
					String reviewer = getReviewer(fullText);
					
					biddings.add(new Bidding(documentName, reviewer));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return biddings;
	}
}
