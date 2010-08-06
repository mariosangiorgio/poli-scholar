package it.polimi.bidding;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;


public class PaperCollection {
	HashMap<String, Collection<Paper>> groupedPapers;
	
	public PaperCollection(){
		groupedPapers = new HashMap<String, Collection<Paper>>();
	}
	
	public void addSubmission(String category, Paper paper){
		Collection<Paper> papers;
		if(groupedPapers.containsKey(category)){
			papers = groupedPapers.get(category);
			papers.add(paper);
		}
		else{
			papers = new Vector<Paper>();
			papers.add(paper);
			groupedPapers.put(category, papers);
		}
	}
	
	public void addSubmissions(String category, Collection<Paper> selectedPapers) {
		Collection<Paper> papers;
		if(groupedPapers.containsKey(category)){
			papers = groupedPapers.get(category);
			papers.addAll(selectedPapers);
		}
		else{
			papers = new Vector<Paper>();
			papers.addAll(selectedPapers);
			groupedPapers.put(category, papers);
		}
	}
	
	public Collection<String> getCategories(){
		return groupedPapers.keySet();
	}
	
	public int getNumberOfPapers(String category){
		if(groupedPapers.containsKey(category)){
			return groupedPapers.get(category).size();
		}
		else{
			return 0;
		}
	}
	
	public Collection<Paper> getPapers(String category){
		Collection<Paper> papers = new Vector<Paper>();
		if(groupedPapers.containsKey(category)){
			papers.addAll(groupedPapers.get(category));
		}
		return papers;
	}
}
