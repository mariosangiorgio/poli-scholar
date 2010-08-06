package it.polimi.bidding;

public class PaperWithRank {
	private Paper paper;
	private float rank;
	
	public PaperWithRank(Paper paper, float rank) {
		this.paper = paper;
		this.rank = rank;
	}
	
	public Paper getPaper(){
		return paper;
	}
	
	public float getRank(){
		return rank;
	}
}
