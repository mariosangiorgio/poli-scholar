package applications;

import it.polimi.bidding.Bidder;
import it.polimi.bidding.Bidding;
import it.polimi.bidding.NearestNeighborBidder;

public class AutomaticBidding {
	public static void main(String[] args) throws Exception {
		String pathToReviewers = "automaticBidding/reviewers";
		String pathToSubmissions = "automaticBidding/submissions";

		String filename = "automaticBidding/savedBidder";
		boolean load = true;

		// TODO: write a menu to select the bidder
		
		Bidder bidder;
		if (load) {
			bidder = Bidder.load(filename);
		} else {
			// Uncomment the bidder you want to use
			bidder = new NearestNeighborBidder(5);
			// bidder = new BayesianBidder();
			bidder.train(pathToReviewers);
			bidder.save(filename);
		}
		for (Bidding bidding : bidder.getReviewers(pathToSubmissions)) {
			System.out.println(bidding + "\n");
		}
	}
}
