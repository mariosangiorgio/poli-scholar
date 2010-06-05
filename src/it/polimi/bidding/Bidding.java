package it.polimi.bidding;

public class Bidding {
	String document;
	String reviewer;
	
	public Bidding(String document, String reviewer){
		this.document = document;
		this.reviewer = reviewer;
	}
	
	public String toString(){
		return "Paper:\t\t"+document+"\nReviewer:\t"+reviewer;
	}
}
