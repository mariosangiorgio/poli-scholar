package applications;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import it.polimi.analyzer.BayesianDocumentClassifier;
import it.polimi.analyzer.DocumentClassifier;
import it.polimi.analyzer.DocumentClassifierType;
import it.polimi.data.hibernate.HibernateSessionManager;
import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.Classification;
import it.polimi.data.hibernate.entities.Journal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.hibernate.Query;
import org.hibernate.Session;


public class Classifier {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException {
		LongOpt longOptions[] = new LongOpt[8];
		longOptions[0] = new LongOpt("train", LongOpt.NO_ARGUMENT, null, 't');

		Getopt options = new Getopt("Classifier", args, "", longOptions);
		boolean train = false;
		while (options.getopt() != -1) {
			int option = options.getLongind();
			switch (option) {
			case 0:
				train = true;
				break;
			}
		}
		
		DocumentClassifier classifier;
		String classifierFile = "classifier";
		File file = new File(classifierFile);

		if (train || !file.exists()) {
			classifier = BayesianDocumentClassifier
					.getFromTrainingSet(DocumentClassifierType.NaiveBayesian,"plaintext/model");
			classifier.save(classifierFile);
		} else {
			classifier = BayesianDocumentClassifier.load(classifierFile);
		}

		Session session = HibernateSessionManager.getNewSession();
		session.beginTransaction();

		// Parameters
		int firstYear = 1970;
		int lastYear =  2009;
		String journalName = "IEEE Transactions on Software Engineering";

		Journal journal = (Journal) session.getNamedQuery("findJournalByName")
		.setParameter("journalName", journalName).uniqueResult();
		
		Query query = session.getNamedQuery("getJournalArticlesInInterval");
		query.setParameter("firstYear", firstYear);
		query.setParameter("lastYear", lastYear);
		query.setParameter("journal", journal);

		Iterator result = query.iterate(); // I used this rather than
											// query.list() to save memory
		
		while (result.hasNext()) {
			Article article = (Article) result.next();
			String label = classifier.classify(article.getArticleAbstract());

			System.out.println(article.getTitle());
			//System.out.println(article.getArticleAbstract());
			System.out.println(label);
			System.out.println();
			
			Classification classification;
			classification = (Classification) session.getNamedQuery(
					"getClassificationFromArticle").setParameter("article",
					article).uniqueResult();
			if (classification == null) {
				classification = new Classification(article, label);
			} else {
				classification.setClassification(label);
			}
			session.saveOrUpdate(classification);
			
			session.flush();
			session.evict(article);
			session.evict(classification);
			session.clear();
		}
		
		session.close();
		System.out.println("Done");
	}
}
