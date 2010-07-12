package applications.analyzer.twoPhaseClassifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Reviewer {
	private String name;
	private HashMap<String, Integer> classCount;

	public Reviewer(String name) {
		this.name = name;
		classCount = new HashMap<String, Integer>();
	}

	public void addArticleCategory(String cateogry) {
		int value = 1;
		if (classCount.containsKey(cateogry)) {
			value = classCount.get(cateogry);
			value++;
		}
		classCount.put(cateogry, value);
	}

	public String getName() {
		return name;
	}

	public Collection<Category> getTopCategories(int numberOfEntries) {
		List<Category> categories = new ArrayList<Category>();
		for (String category : classCount.keySet()) {
			categories.add(new Category(category, classCount.get(category)));
		}
		Collections.sort(categories, new CategoryComparator());

		Collection<Category> result = new Vector<Category>();
		if (numberOfEntries < categories.size()) {
			result.addAll(categories.subList(0, numberOfEntries));
		} else {
			result.addAll(categories);
		}
		return result;
	}
}

class CategoryComparator implements Comparator<Category> {

	@Override
	public int compare(Category o1, Category o2) {
		return o2.getCount() - o1.getCount();
	}

}
