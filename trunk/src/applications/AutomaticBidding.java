package applications;

import java.io.File;

import applications.analyzer.twoPhaseClassifier.TwoPhaseClassifier;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public class AutomaticBidding {
	public static void main(String[] args) throws Exception {
		LongOpt longOptions[] = new LongOpt[8];
		longOptions[0] = new LongOpt("extractAbstracts", LongOpt.NO_ARGUMENT, null, 'e');
		longOptions[1] = new LongOpt("getBids", LongOpt.NO_ARGUMENT, null, 'b');

		Getopt options = new Getopt("automatic-bidding", args, "", longOptions);
		boolean  extractAbstracts = false;
		boolean  getBids = false;
		
		while (options.getopt() != -1) {
			int option = options.getLongind();
			switch (option) {
			case 0:
				extractAbstracts = true;
				break;
			case 1:
				getBids = true;
				break;
			}
		}
		if(extractAbstracts == getBids){
			System.out.println("ERROR");
			System.out.println("To extract abstracts from the submissions use --extracAbstracts");
			System.out.println("To get the bids use --getBids");
		}
		if(extractAbstracts){
			String root = "papers/fulltext/submissions/";
			new ExtractAbstracts().convert(root);
			return;
		}
		if(getBids){
			TwoPhaseClassifier twoPhaseClassifier = null;

			String classifierFile = "classifier";
			boolean train = false;
			File file = new File(classifierFile);

			if (train || !file.exists()) {
				twoPhaseClassifier = TwoPhaseClassifier.train("papers/abstracts/model",
						classifierFile);
			} else {
				twoPhaseClassifier = TwoPhaseClassifier.load(classifierFile);
			}

			twoPhaseClassifier
					.classifySubmissions("papers/abstracts/submissions");
			int minimumNumberOfPapers = 20;
			int maximumNumberOfPapers = (int) Math.floor(.5d * twoPhaseClassifier
					.getTotalSubmissions());
			float topicCoverage = .75f;
			twoPhaseClassifier.classifyReviewers(
					"papers/abstracts/reviewers", topicCoverage,
					minimumNumberOfPapers, maximumNumberOfPapers);
			return;
		}
	}
}
