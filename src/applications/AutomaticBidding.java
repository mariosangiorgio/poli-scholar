package applications;

import it.polimi.bidding.Bidder;
import it.polimi.bidding.Bidding;
import it.polimi.bidding.NearestNeighborBidder;

public class AutomaticBidding {
	public static void main(String[] args) {
		String pathToReviewers = "automaticBidding/reviewers";
		String pathToSubmissions = "automaticBidding/submissions";
		
		Bidder bidder = new NearestNeighborBidder(5);
		bidder.train(pathToReviewers);
		for(Bidding bidding : bidder.getReviewers(pathToSubmissions)){
			System.out.println(bidding+"\n");
		}
	}
}
