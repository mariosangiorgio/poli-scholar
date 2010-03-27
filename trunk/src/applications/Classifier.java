package applications;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import it.polimi.data.hibernate.HibernateSessionManager;
import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.Classification;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.hibernate.Query;
import org.hibernate.Session;

import applications.analyzer.BayesianDocumentClassifier;

public class Classifier {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException {
		
		Session session = HibernateSessionManager.getNewSession();
		session.beginTransaction();

		Query query = session.getNamedQuery("getArticlesByYear");
		query.setParameter("articleYear", 2009);
		
		Iterator result = query.iterate(); // I used this rather than query.list() to save memory
		BayesianDocumentClassifier classifier;
		String classifierFile = "classifier";
		File file = new File(classifierFile);
		
		LongOpt longOptions[] = new LongOpt[8];
		longOptions[0] = new LongOpt("train", LongOpt.NO_ARGUMENT, null, 't');
		Getopt options = new Getopt("Classifier", args, "", longOptions);
		boolean train = false;
		while(options.getopt() != -1){
			int option = options.getLongind();
			switch(option){
			case 0:
				train = true;
				break;
			}
		}
		
		
		if(train || !file.exists()){
			 classifier = BayesianDocumentClassifier.getFromTrainingSet();
			 classifier.save(classifierFile);
		}
		else{
			classifier = BayesianDocumentClassifier.load(classifierFile);
		}


		while(result.hasNext()){
			Article article = (Article) result.next();
			String label	= classifier.classify(article.getFullText());
			
			System.out.println(article.getTitle());
			//System.out.println(article.getArticleAbstract());
			System.out.println(label);
			
			Classification classification = new Classification(article, label);
			session.saveOrUpdate(classification);
			
			session.evict(article);
			session.evict(classification);
			session.clear();
		}
		session.close();
		System.out.println("Done");
	}
}
