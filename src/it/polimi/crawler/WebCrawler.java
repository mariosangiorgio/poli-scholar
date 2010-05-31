package it.polimi.crawler;

import it.polimi.data.hibernate.HibernateSessionManager;
import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.Author;
import it.polimi.data.hibernate.entities.Journal;
import it.polimi.webClient.ContentDownloader;

import java.util.Collection;

import org.apache.http.HttpHost;
import org.hibernate.Session;

/**
 * Generic class for a Journal Crawler. Subclasses must provide the actual
 * implementation to access a data source.
 * 
 * @author Mario Sangiorgio
 */
public abstract class WebCrawler {
	protected Journal journal;
	protected String journalCode;

	protected ContentDownloader downloader;
	protected HttpHost targetHost;

	protected Session session;

	protected WebCrawler(String journalName, String journalIdentifier,
			HttpHost targetHost) {
		// Looking if the journal already appears in the database and creating
		// it if it doesn't
		if (session == null || !session.isOpen()) {
			session = HibernateSessionManager.getNewSession();
		}
		session.beginTransaction();

		journal = (Journal) session.getNamedQuery("findJournalByName")
				.setParameter("journalName", journalName).uniqueResult();

		if (journal == null) {
			journal = new Journal(journalName);
			session.saveOrUpdate(journal);
		}
		session.getTransaction().commit();
		session.close();

		this.journalCode = journalIdentifier;

		// Setup of connection related stuffs
		downloader = new ContentDownloader();
		this.targetHost = targetHost;
	}

	protected WebCrawler(String journalName, String journalIdentifier,
			HttpHost targetHost, String proxyHostname, int proxyPort) {
		this(journalName, journalIdentifier, targetHost);
		downloader.setupProxy(proxyHostname, proxyPort);
	}

	protected WebCrawler(String journalName, String journalIdentifier,
			HttpHost targetHost, String proxyHostname, int proxyPort,
			String username, String password) {
		this(journalName, journalIdentifier, targetHost);
		downloader.setupProxyWithCredentials(proxyHostname, proxyPort,
				username, password);
	}

	/**
	 * Method to test if the connection to the data source is working properly.
	 * 
	 * @throws Exception
	 *             that specifies the error encountered.
	 */
	public void testConnection() throws Exception {
		downloader.getPage(targetHost, "/");
	}

	/**
	 * Crawls the data source and fills the database with the collected data
	 * 
	 * @param journalIdentifier
	 *            a string that identifies the journal.
	 * @param year
	 *            the year of the publication.
	 */
	public void getYearArticles(int year) {
		if (session == null || !session.isOpen()) {
			session = HibernateSessionManager.getNewSession();
		}
		session.beginTransaction();

		Collection<String> issues = getIssuesList(year);
		for (String issue : issues) {
			Collection<String> paperAddresses = getPapersOfAnIssue(issue);
			for (String paperAddress : paperAddresses) {
				Article article;
				try {
					article = getPaperData(paperAddress);
					article.setYear(year);
					article.setJournal(journal);
					
					session.saveOrUpdate(article);
					
					for(Author author : article.getAuthors()){
						author.addArticle(article);
						session.saveOrUpdate(author);
					}
					
					session.flush();
					
					session.evict(article);
					for(Author author : article.getAuthors()){
						session.evict(author);
					}
					session.clear();

				} catch (Exception e) {
					System.err.println("Something went wrong downloading "
							+ paperAddress + ": " + e);
				}
			}
		}

		session.getTransaction().commit();
		session.close();
	}

	public abstract Collection<String> getIssuesList(int year);

	public abstract Collection<String> getPapersOfAnIssue(String issueAddress);

	public abstract Article getPaperData(String paperAddress) throws Exception;
}