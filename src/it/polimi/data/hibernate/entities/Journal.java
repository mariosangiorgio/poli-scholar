package it.polimi.data.hibernate.entities;

import java.util.Collection;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "JOURNALS", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
@NamedQuery(name = "findJournalByName", query = "from Journal journal where journal.name = :journalName")
public class Journal {

	@Id
	@GeneratedValue
	@Column(name = "JournalId")
	private long identifier;

	private String name;
	
	@OneToMany
	private Collection<Article> articles = new Vector<Article>();
	
	public Journal() {
	}

	public Journal(String journalName) {
		this.name = journalName;
	}

	@SuppressWarnings("unused")
	private void setIdentifier(long identifier) {
		this.identifier = identifier;
	}

	public long getIdentifier() {
		return identifier;
	}
	
	public void setName(String journalName) {
		this.name = journalName;
	}

	public String getName() {
		return name;
	}
	
	public void addArticle(Article article){
		articles.add(article);
	}
}
