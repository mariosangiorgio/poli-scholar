package applications;

import it.polimi.data.hibernate.HibernateSessionManager;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class Statistics {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Session session = HibernateSessionManager.getNewSession();
		session.beginTransaction();

		int firstYear = 1990;
		int lastYear = 2010;
		int step = 5;

		for (int year = firstYear; year <= lastYear; year += step) {
			Query query = session.getNamedQuery("getTotalNumerOfPapers");

			query.setParameter("firstYear", year);
			query.setParameter("lastYear", year + step - 1);

			Long total = (Long) query.uniqueResult();

			query = session.getNamedQuery("getClassificationStatistics");

			query.setParameter("firstYear", year);
			query.setParameter("lastYear", year + step - 1);

			System.out.println("From " + year + " to " + (year + step - 1));

			List result = query.list();
			for (Object o : result) {
				Object[] record = (Object[]) o;
				String classification = (String) record[0];
				Long count = (Long) record[1];
				System.out.println(classification + "," + count + ","
						+ ((float) count) / total);
			}
		}
		session.close();
	}
}
