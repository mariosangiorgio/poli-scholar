package it.polimi.bidding;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;

public class AuthorPapers extends PaperCollection {
	private String authorName;
	private Collection<String> selectedTopics;

	public AuthorPapers(String authorName) {
		this.authorName = authorName;
		selectedTopics = new Vector<String>();
	}
	
	public void addSelectedTopic(String topic){
		selectedTopics.add(topic);
	}

	public String getAuthorName() {
		return authorName;
	}

	public Collection<String> getMostRelevantTopics(float topicCoverage) {
		if(selectedTopics.size() != 0){
			return selectedTopics;
		}
		Collection<String> mostRelevantTopics = new Vector<String>();

		Collection<String> availableCategories = getCategories();

		int totalPapers = 0;
		for (String category : availableCategories) {
			totalPapers += getNumberOfPapers(category);
		}

		int threshold = (int) (totalPapers * topicCoverage);
		int pickedPapers = 0;
		while (pickedPapers < threshold) {
			String selectedCategory = null;
			int max = 0;
			for (String category : availableCategories) {
				if (getNumberOfPapers(category) > max) {
					max = getNumberOfPapers(category);
					selectedCategory = category;
				}
			}
			mostRelevantTopics.add(selectedCategory);
			availableCategories.remove(selectedCategory);
			pickedPapers += max;
		}
		return mostRelevantTopics;
	}

	public Collection<Paper> pickTopPapers(String topic,
			Collection<Paper> submittedPapers, int papersToPick) {
		Collection<Paper> papersOfTheTopic = getPapers(topic);
		
		if(papersOfTheTopic.size() == 0){
			/*
			 * If the author specifed the interest for a paper that is not in his profile
			 * the distance will be computed with respect to all the papers of the profile
			 */
			papersOfTheTopic = getAllPapers();
		}
		
		TreeSet<PaperWithRank> rankedPapers = new TreeSet<PaperWithRank>(
				new Comparator<PaperWithRank>(){
					@Override
					public int compare(PaperWithRank o1, PaperWithRank o2) {
						if(o1.getRank()-o2.getRank() > 0){
							return 1;
						}
						if(o1.getRank()-o2.getRank() < 0){
							return -1;
						}
						return 0;
					}
				}
			);
		
		for (Paper paper : submittedPapers) {
			double rank = 0;
			// The rank is computed as the average of ranks
			for (Paper authorPaper : papersOfTheTopic) {
				rank += paper.getDistance(authorPaper);
			}
			rank = rank / papersOfTheTopic.size();
			rankedPapers.add(new PaperWithRank(paper, rank));
		}

		Collection<Paper> pickedPapers = new Vector<Paper>();
		for (int i = 0; i < papersToPick; i++) {
			if(rankedPapers.size() == 0){
				return pickedPapers;
			}
			// Polling last element to get the one with the highest rank value
			pickedPapers.add(rankedPapers.pollLast().getPaper());
		}

		return pickedPapers;
	}

	private Collection<Paper> getAllPapers() {
		Collection<Paper> papers = new Vector<Paper>();
		for(String topic : getCategories()){
			papers.addAll(getPapers(topic));
		}
		return papers;
	}
}