package applications;

import it.polimi.data.hibernate.HibernateSessionManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.hibernate.Query;
import org.hibernate.Session;

public class Statistics {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Session session = HibernateSessionManager.getNewSession();
		session.beginTransaction();

		int firstYear = 1976;
		int lastYear = 2010;
		int step = 1;

		HashMap<String, Vector<Float>> table;
		table = new HashMap<String, Vector<Float>>();

		int i = 0;
		for (int year = firstYear; year <= lastYear; year += step) {
			System.out.println("From: "+year+" to: "+(year+step));
			Query query = session.getNamedQuery("getTotalNumerOfPapers");

			query.setParameter("firstYear", year);
			query.setParameter("lastYear", year + step - 1);

			Long total = (Long) query.uniqueResult();

			query = session.getNamedQuery("getClassificationStatistics");

			query.setParameter("firstYear", year);
			query.setParameter("lastYear", year + step - 1);

			List result = query.list();
			for (Object o : result) {
				Object[] record = (Object[]) o;
				String classification = (String) record[0];
				Long count = (Long) record[1];

				if (table.get(classification) == null) {
					table.put(classification, new Vector<Float>());
				}
				while (table.get(classification).size() < i) {
					table.get(classification).add(0f);
				}
				table.get(classification).add((float) count / total);
			}
			i++;
		}
		// Filling all the gaps
		for(String key : table.keySet()){
			while (table.get(key).size() < i) {
				table.get(key).add(0f);
			}
		}
		
		// Writing table to file
		try {
			FileWriter file = new FileWriter("table.csv");
			BufferedWriter buffer = new BufferedWriter(file);
			buffer.write("topic,");
			for (int year = firstYear; year <= lastYear; year += step) {
				buffer.write(year+"-"+(year+step-1)+",");
			}
			buffer.write("\n");
			for (String key : table.keySet()) {
				buffer.write(key + ",");
				i = 0;
				for (i = 0; i < (table.get(key).size() - 1); i++) {
					buffer.write(table.get(key).get(i) + ",");
				}
				buffer.write(table.get(key).get(i) + "\n");

			}
			buffer.close();
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		System.out.println("Done");
	}
}
