package it.polimi.data.hibernate.entities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "ARTICLES")
@NamedQueries( {
		@NamedQuery(name = "getArticlesByYear", query = "from Article article where article.year = :articleYear"),
		@NamedQuery(name = "findArticleByTitle", query = "from Article article where article.title = :title") })
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
	
	@ManyToOne
	private Journal journal;

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

	public String getFullText() {
		StringWriter writer = new StringWriter();
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(fullTextPdf);
			PDDocument document = PDDocument.load(stream);
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.writeText(document, writer);
			document.close();
			stream.close();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return writer.toString();
	}

	public void setJournal(Journal journal) {
		this.journal = journal;
	}

	public Journal getJournal() {
		return journal;
	}

}
