package it.polimi.data.hibernate.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@Entity
@NamedQueries( {
		@NamedQuery(name = "getClassificationStatistics", query = "select c.classification as label, count(*) as numberOfPapers "
				+ "from Classification c "
				+ "where c.year between :firstYear and :lastYear "
				+ "group by c.classification " + "order by c.classification"),
		@NamedQuery(name = "getTotalNumerOfPapers", query = "select count(*) "
				+ "from Classification c "
				+ "where c.year between :firstYear and :lastYear"),
		@NamedQuery(name = "getClassificationFromArticle", query = "from Classification c where c.article = :article") })
public class Classification {
	@Id
	@GeneratedValue
	private long identifier;

	@OneToOne
	Article article;

	String classification;
	
	/* 
	 * The year value is duplicated here to improve analysis performances.
	 */
	int year;

	public Classification() {
	}

	public Classification(Article article, String classification) {
		this.article = article;
		this.classification = classification;
		this.year = article.getYear();
	}

	public void setIdentifier(long identifier) {
		this.identifier = identifier;
	}

	public long getIdentifier() {
		return identifier;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}
	
	public int getYear(){
		return year;
	}
	public void setYear(int year){
		this.year = year;
	}

}
