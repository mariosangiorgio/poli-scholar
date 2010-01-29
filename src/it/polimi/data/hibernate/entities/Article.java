package it.polimi.data.hibernate.entities;

import java.util.List;
import java.util.Vector;

import javax.persistence.*;

@Entity
@Table(name = "ARTICLES")
public class Article {
	@Id @GeneratedValue
	@Column(name = "ArticleId")
	private long identifier;
	
	private String title;
	private String articleAbstract;
	
	
	@ManyToMany
    @JoinTable(name = "Article_Author",
                    joinColumns = { @JoinColumn(name = "ArticleId")},
                    inverseJoinColumns = { @JoinColumn(name = "AuthorId") })
	private List<Author> authors = new Vector<Author>();
	
	public Article(String title, String articleAbstract) {
		this.setTitle(title);
		this.setArticleAbstract(articleAbstract);
	}

	@SuppressWarnings("unused")
	private void setIdentifier(long identifier) {
		this.identifier = identifier;
	}

	public long getIdentifier() {
		return identifier;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setArticleAbstract(String articleAbstract) {
		this.articleAbstract = articleAbstract;
	}

	public String getArticleAbstract() {
		return articleAbstract;
	}

	public void addAuthor(Author author) {
		authors.add(author);
	}

}
