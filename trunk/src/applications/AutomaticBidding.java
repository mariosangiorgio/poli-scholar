package applications;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import it.polimi.data.io.NotADirectoryException;

import java.io.File;

public class AutomaticBidding {
	public static void main(String[] args) {
		// System.err.close();

		StringBuffer buffer = new StringBuffer();
		LongOpt longOptions[] = new LongOpt[8];
		longOptions[0] = new LongOpt("extractAbstracts", LongOpt.NO_ARGUMENT,
				null, 'e');
		longOptions[1] = new LongOpt("maxAbstractLength",
				LongOpt.REQUIRED_ARGUMENT, buffer, 'M');

		longOptions[2] = new LongOpt("analyze", LongOpt.NO_ARGUMENT, null, 'a');
		longOptions[3] = new LongOpt("train", LongOpt.NO_ARGUMENT, null, 't');
		longOptions[7] = new LongOpt("generateProfiles", LongOpt.NO_ARGUMENT,
				null, 'p');
		longOptions[4] = new LongOpt("loadProfiles", LongOpt.NO_ARGUMENT, null,
				'l');
		longOptions[5] = new LongOpt("groupSubmissions", LongOpt.NO_ARGUMENT,
				null, 'g');
		longOptions[6] = new LongOpt("getSuggestions", LongOpt.NO_ARGUMENT,
				null, 's');

		Getopt options = new Getopt("automatic-bidding", args, "", longOptions);
		boolean extractAbstract = false;
		boolean maxAbstractLengthSet = false;
		int maxAbstractLength = 0;
		boolean analyze = false;
		boolean train = false;
		boolean generateProfiles = false;
		boolean loadProfiles = false;
		boolean groupSubmissions = false;
		boolean getSuggestions = false;
		while (options.getopt() != -1) {
			int option = options.getLongind();
			switch (option) {
			case 0:
				extractAbstract = true;
				break;
			case 1:
				maxAbstractLengthSet = true;
				maxAbstractLength = Integer.parseInt(options.getOptarg());
				break;
			case 2:
				analyze = true;
				break;
			case 3:
				train = true;
				break;
			case 7:
				generateProfiles = true;
				break;
			case 4:
				loadProfiles = true;
				break;
			case 5:
				groupSubmissions = true;
				break;
			case 6:
				getSuggestions = true;
				break;
			}
		}
		if (extractAbstract == analyze) {
			System.out
					.println("You have to choose one options between extractAbstracts and analyze");
			printUsage();
		}

		if (extractAbstract) {
			String root = "papers/fulltext/submissions/";

			ExtractAbstracts extractor;
			if (maxAbstractLengthSet) {
				extractor = new ExtractAbstracts(maxAbstractLength);
			} else {
				extractor = new ExtractAbstracts();
			}
			extractor.convert(new File(root));
			return;
		}

		if (analyze) {
			if (generateProfiles && loadProfiles) {
				System.out
						.println("ERROR: you must choose to generate or to load the profiles");
				return;
			}
			if (getSuggestions && !(generateProfiles || loadProfiles)) {
				System.out
						.println("ERROR: to generate the suggestions you have to have authors' profiles");
				return;
			}
			try {
				MakeBids bidder = new MakeBids(train, generateProfiles,
						loadProfiles, groupSubmissions, getSuggestions);
				bidder.getBids();
			} catch (NotADirectoryException e) {
			}
			return;
		}
	}

	private static void printUsage() {
		System.out.println("To extract the abstracts use:");
		System.out.println("\t--extractAbstracts");
		System.out.println("\t--maxAbstractLength=N\t(Optional)");
		System.out.println();
		System.out.println("To perform the analysis use:");
		System.out.println("\t--analyze");
		System.out.println("\t--train\t\t\t(Optional)");
		System.out.println("\t--loadProfiles\t\t(Optional)");
		System.out.println("\t--groupSubmissions\t(Optional)");
		System.out.println("\t--getSuggestions\t(Optional)");
	}

}
