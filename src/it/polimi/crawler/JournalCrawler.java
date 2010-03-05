package it.polimi.crawler;

import it.polimi.data.hibernate.entities.Article;
import it.polimi.webClient.ContentDownloader;

import java.util.Collection;

import org.apache.http.HttpHost;

/**
 * Generic class for a Journal Crawler.
 * Subclasses must provide the actual implementation
 * to access a data source.
 * 
 * @author Mario Sangiorgio
 */
public abstract class JournalCrawler {
	protected String			journalName;
	protected String			journalIdentifier;
	
	protected ContentDownloader downloader;
	protected HttpHost			targetHost;
	
	protected JournalCrawler(String journalName, String journalIdentifier, HttpHost targetHost){
		this.journalIdentifier = journalIdentifier;
		this.journalName	   = journalName;
		downloader = new ContentDownloader();
		this.targetHost = targetHost;
	}
	
	protected JournalCrawler(String journalName, String journalIdentifier, HttpHost targetHost,
							 String proxyHostname, int proxyPort){
		this(journalName, journalIdentifier, targetHost);
		downloader.setupProxy(proxyHostname, proxyPort);
	}
	
	protected JournalCrawler(String journalName, String journalIdentifier, HttpHost targetHost,
							 String proxyHostname, int proxyPort, String username, String password){
		this(journalName, journalIdentifier, targetHost);
		downloader.setupProxyWithCredentials(proxyHostname, proxyPort, username, password);
	}
	
	/**
	 * Method to test if the connection to the data source is working properly.
	 * 
	 * @throws Exception that specifies the error encountered.
	 */
	public abstract void testConnection() throws Exception;
	
	/**
	 * Crawls the data source and fills the database with the collected data
	 * 
	 * @param journalIdentifier a string that identifies the journal.
	 * @param year the year of the publication.
	 */
	public abstract void getYearArticles(int year);
	
	public abstract Collection<String> getPapersOfAnIssue(String issueAddress);
	
	public abstract Article getPaperData(String paperAddress) throws Exception;
}