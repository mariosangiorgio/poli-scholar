package applications;

import it.polimi.bidding.BayesianBidder;
import it.polimi.bidding.Bidder;
import it.polimi.bidding.Bidding;
import it.polimi.bidding.BiddingMethods;
import it.polimi.bidding.NearestNeighborBidder;

public class AutomaticBiddingOLD {
	public static void main(String[] args) throws Exception {
		String pathToReviewers = "papers/abstracts/reviewers";
		String pathToSubmissions = "papers/abstracts/submissions";

		String filename = "savedBidder";
		boolean load = false;
		
		BiddingMethods method = BiddingMethods.VectorSpaceModel;
		
		Bidder bidder;
		if (load) {
			bidder = Bidder.load(filename);
		} else {
			// Uncomment the bidder you want to use
			switch (method) {
			case NaiveBayesian:
				bidder = new BayesianBidder();
				break;
			case VectorSpaceModel:
				bidder = new NearestNeighborBidder(5);
				break;
			default:
				bidder = null;
				break;
			}
			bidder.train(pathToReviewers);
			bidder.save(filename);
		}
		for (Bidding bidding : bidder.getReviewers(pathToSubmissions)) {
			System.out.println(bidding + "\n");
		}
	}
}
