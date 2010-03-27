package applications;

import it.polimi.analyzer.WordFrequencyAnalyzer;
import it.polimi.data.hibernate.HibernateSessionManager;
import it.polimi.data.hibernate.entities.Article;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.Session;

public class Analyzer {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		WordFrequencyAnalyzer analyzer = new WordFrequencyAnalyzer();

		Session session = HibernateSessionManager.getNewSession();
		session.beginTransaction();

		Query query = session.getNamedQuery("getArticlesByYear");
		query.setParameter("articleYear", 2010);
		
		Iterator result = query.iterate(); // I used this rather than query.list() to save memory

		while(result.hasNext()){
			Article article = (Article) result.next();
			
			String text;
			//text = article.getArticleAbstract();
			text = article.getFullText();

			System.out.println("Frequency count for: "+article.getTitle());
			System.out.println("Length: "+text.length());
			//System.out.println("Text: "+text);
			Map<String, Integer> frequencyCount = analyzer.getFrequencyCount(text);
			
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
			
			//Clearing session to avoid memory leakages
			session.evict(article);
			session.clear();
		}
		session.close();
		System.out.println("Done");
	}

}
