package temporaryCode.validation;

import java.util.Collection;
import java.util.Vector;

public class Reviewer {
	private String name;
	private Collection<Integer> yes;
	private Collection<Integer> maybe;
	private Collection<Integer> conflict;
	private Collection<Integer> suggestedPapers;
	
	public Reviewer(String name){
		this.name = name;
		yes = new Vector<Integer>();
		maybe = new Vector<Integer>();
		conflict = new Vector<Integer>();
		suggestedPapers = new Vector<Integer>();
	}
	
	public String getName() {
		return name;
	}
	
	public Collection<Integer> getYes(){
		return yes;
	}
	
	public Collection<Integer> getMaybe(){
		return maybe;
	}
	
	public Collection<Integer> getConflict(){
		return conflict;
	}
	
	public void addBid(Integer paperNumber, KindOfBid kindOfBid){
		switch(kindOfBid){
		case yes:
			yes.add(paperNumber);
			break;
		case maybe:
			maybe.add(paperNumber);
			break;
		case conflict:
			conflict.add(paperNumber);
			break;

		}
	}
	
	public void addSuggestedPapers(Integer paperNumber){
		suggestedPapers.add(paperNumber);
	}
	
	public void printSummary() {
		if(suggestedPapers.size() == 0){
			System.out.println("No suggestion for this author");
			return;
		}
		int yesRetrieved = 0, maybeRetrieved = 0, conflictRetrieved = 0;
		for(Integer i : suggestedPapers){
			if(yes.contains(i)){
				yesRetrieved++;
			}
			if(maybe.contains(i)){
				maybeRetrieved++;
			}
			if(conflict.contains(i)){
				conflictRetrieved++;
			}
		}
		int relevantAndRetrieved = yesRetrieved + maybeRetrieved + conflictRetrieved;
		System.out.println("Suggested papers: "+suggestedPapers.size());
		System.out.println("Actually bidded papers: "+(yes.size()+maybe.size()+conflict.size()));
		System.out.println("Retrieved "+yesRetrieved+" out of "+yes.size()+" yes bids");
		System.out.println("Retrieved "+maybeRetrieved+" out of "+maybe.size()+" maybe bids");
		System.out.println("Retrieved "+conflictRetrieved+" out of "+conflict.size()+" conflict bids");
		System.out.println("Precision: "+(float)relevantAndRetrieved/suggestedPapers.size());
		System.out.println("Recall: "+(float)relevantAndRetrieved/(yes.size()+maybe.size()+conflict.size()));
		CSVWriter.addNewLine(name+","+
							 suggestedPapers.size()+","+
							 (yes.size()+maybe.size()+conflict.size())+","+
							 yesRetrieved+","+
							 yes.size()+","+
							 maybeRetrieved+","+
							 maybe.size()+","+
							 conflictRetrieved+","+
							 conflict.size());
	}

}
