package it.polimi.data.hibernate.entities;

import java.util.List;
import java.util.Vector;

import javax.persistence.*;

@Entity
@Table(name = "AUTHORS")
public class Author {
	@Id @GeneratedValue
	@Column(name = "AuthorId")
	private Long identifier;
	
	private String name;
	private String affiliation;
	
	@ManyToMany(mappedBy = "authors")
	private List<Article> articles = new Vector<Article>();
	
	public Author(String name,String affiliation){
		this.name = name;
		this.affiliation = affiliation;
	}
	
	public Author(){
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public String getAffiliation() {
		return affiliation;
	}

	@SuppressWarnings("unused")
	private void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public Long getIdentifier() {
		return identifier;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void addArticle(Article article) {
		articles.add(article);
	}
}
