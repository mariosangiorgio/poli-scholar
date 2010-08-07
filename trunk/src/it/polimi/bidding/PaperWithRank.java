package it.polimi.bidding;

public class PaperWithRank {
	private Paper	paper;
	private double	rank;
	
	public PaperWithRank(Paper paper, double rank) {
		this.paper = paper;
		this.rank = rank;
	}
	
	public Paper getPaper(){
		return paper;
	}
	
	public double getRank(){
		return rank;
	}
}
