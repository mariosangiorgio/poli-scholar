package it.polimi.data.hibernate.entities;

import java.util.List;
import java.util.Vector;

import javax.persistence.*;

@Entity
@Table(name = "AUTHORS")
@NamedQuery(name = "findAuthorByName", query = "from Author author where author.name = :authorName and author.surname = :authorSurname")
public class Author {
	@Id @GeneratedValue
	@Column(name = "AuthorId")
	private Long identifier;
	
	private String name;
	private String surname;
	
	@ManyToMany(mappedBy = "authors")
	private List<Article> articles = new Vector<Article>();
	
	public Author(String name, String surname){
		this.name = name;
		this.surname = surname;
	}
	
	public Author(){
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getSurname() {
		return surname;
	}
}
