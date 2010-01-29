package test;

import it.polimi.data.hibernate.HibernateUtil;
import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.Author;

import org.hibernate.classic.Session;


public class HibernateMain {

	public static void main(String[] args) {
		System.out.println("Testing Hibernate");
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		Author author = new Author("Mario", "Politecnico di Milano");
		Author author2 = new Author("Pippo", "Politecnico di Milano");
		
		Article article = new Article("Title","Bla bla");
		Article article2 = new Article("Nuovo","Bla bla bla");


		author.addArticle(article);
		author.addArticle(article2);
		author2.addArticle(article2);
		
		article.addAuthor(author);
		article2.addAuthor(author2);
		article2.addAuthor(author);
		
		session.save(author);
		session.save(article);
		session.save(author2);
		session.save(article2);
		session.getTransaction().commit();
		System.out.println("Done");
	}
}
