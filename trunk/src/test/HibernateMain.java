package test;

import org.hibernate.classic.Session;

import test.hibernate.entities.Author;
import test.hibernate.entities.HibernateUtil;

public class HibernateMain {

	public static void main(String[] args) {
		System.out.println("Testing Hibernate");
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		session.beginTransaction();
		Author author = new Author();
		author.setFullName("Mario");
		author.setAffiliation("Politecnico di Milano");
		session.save(author);
		System.out.println("Done");
		
		session.getTransaction().commit();
	}
}
