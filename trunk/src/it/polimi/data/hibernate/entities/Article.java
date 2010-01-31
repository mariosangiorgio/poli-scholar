package it.polimi.data.hibernate.entities;

import java.util.List;
import java.util.Vector;

import javax.persistence.*;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "ARTICLES")
public class Article {
	@Id
	@GeneratedValue
	@Column(name = "ArticleId")
	private long identifier;
	
	private String title;
	
	@Type(type = "text")
	private String articleAbstract;
	
	private int year;

	@Lob
	private byte[] fullTextPdf;

	@ManyToMany
	@JoinTable(name = "Article_Author", joinColumns = { @JoinColumn(name = "ArticleId") }, inverseJoinColumns = { @JoinColumn(name = "AuthorId") })
	private List<Author> authors = new Vector<Author>();

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

	public void setFullTextPdf(byte[] fullTextPdf) {
		this.fullTextPdf = fullTextPdf;
	}

	public byte[] getFullTextPdf() {
		return fullTextPdf;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getYear() {
		return year;
	}

}
