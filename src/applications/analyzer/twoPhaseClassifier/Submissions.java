package applications.analyzer.twoPhaseClassifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Submissions {
	private HashMap<String, Collection<String>> submissions = new HashMap<String, Collection<String>>();

	public void addSubmissionToClass(String className, String submissionName) {
		Collection<String> submissionList;
		if (submissions.containsKey(className)) {
			submissionList = submissions.get(className);
		} else {
			submissionList = new Vector<String>();
		}
		submissionList.add(submissionName);
		submissions.put(className, submissionList);
	}

	public Set<String> getCategories() {
		return submissions.keySet();
	}

	public Collection<String> getPapersOfCategory(String category) {
		Collection<String> submissionList = new Vector<String>();
		submissionList.addAll(submissions.get(category));
		return submissionList;
	}

	public Collection<Integer> getSubmissionsOfCategory(String categoryName) {
		Pattern icsmFilename = Pattern.compile("\\w*\\d\\d\\d\\d_submission_(\\d*)");
		Vector<Integer> result = new Vector<Integer>();
		Collection<String> categoryPaper = submissions.get(categoryName);
		if (categoryPaper == null) {
			return result;
		}
		for (String paper : categoryPaper) {
			Matcher numberMatcher = icsmFilename.matcher(paper);
			if (numberMatcher.find()) {
				result.add(Integer.parseInt(numberMatcher.group(1)));
			}
		}
		return result;
	}
	
	public Collection<String> getSubmissionFilesByCategory(String categoryName){
		Collection<String> files = new Vector<String>();
		Collection<String> categoryPaper = submissions.get(categoryName);
		if (categoryPaper == null) {
			return files;
		}
		else{
			files.addAll(categoryPaper);
		}
		return files;
	}
	
	public int getTotalSubmissions() {
		int sum = 0;
		for (String category : submissions.keySet()) {
			sum += submissions.get(category).size();
		}
		return sum;
	}

	public int getSubmissionsCount(String name) {
		if (submissions.get(name) == null) {
			return 0;
		} else {
			return submissions.get(name).size();
		}
	}
}