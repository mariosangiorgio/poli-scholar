package it.polimi.data.hibernate.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Classification {
	@Id
	@GeneratedValue
	private long identifier;
	
	@OneToOne
	Article article;
	String	classification;
	
	public Classification(Article article, String classification){
		this.article = article;
		this.classification = classification;
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
	
}
