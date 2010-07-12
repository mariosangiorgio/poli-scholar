package applications.analyzer.twoPhaseClassifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class Submissions {
	private HashMap<String,Collection<String>> submissions = new HashMap<String, Collection<String>>();
	
	public void addSubmissionToClass(String className,String submissionName){
		Collection<String> submissionList;
		if(submissions.containsKey(className)){
			submissionList = submissions.get(className);
		}
		else{
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
}
