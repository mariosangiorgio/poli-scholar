package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map.Entry;

import it.polimi.analyzer.WordFrequencyAnalyzer;
import it.polimi.data.hibernate.HibernateUtil;
import it.polimi.data.hibernate.entities.Article;

import org.hibernate.Query;
import org.hibernate.Session;
import org.tartarus.snowball.ext.porterStemmer;

public class Analyzer {

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		WordFrequencyAnalyzer analyzer = new WordFrequencyAnalyzer();

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Query query = session.getNamedQuery("getArticlesByYear");
		query.setParameter("articleYear", 2010);

		List result = query.list();

		for (Object o : result) {
			Article article = (Article) o;

			System.out.println("Frequency count for: "+article.getTitle());
			System.out.println("Abstract: "+article.getArticleAbstract());
			Map<String, Integer> frequencyCount = analyzer
					.getFrequencyCount(article.getArticleAbstract());
			
			// Sorting the result
			Set<Entry<String,Integer>> values = frequencyCount.entrySet();
			TreeSet<Entry<String, Integer>> sortedSet =
				new TreeSet<Entry<String,Integer>>(new Comparator<Entry<String,Integer>>() {

					@Override
					public int compare(Entry<String, Integer> o1,
							Entry<String, Integer> o2) {
						if(o2.getValue() == o1.getValue()){
							return 1;
						}
						else{
							return o2.getValue() - o1.getValue();
						}
					}
				});
			
			for(Entry<String, Integer> entry:values){
				sortedSet.add(entry);
			}
			
			for (Entry<String, Integer> entry : sortedSet) {
				System.out.println(entry.getKey() + "\t\t" + entry.getValue());
			}
			System.out.println("");
		}
	}

}
