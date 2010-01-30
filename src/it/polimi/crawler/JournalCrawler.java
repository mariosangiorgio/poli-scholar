package it.polimi.crawler;

import it.polimi.webClient.PageDownloader;

import java.util.Collection;

import org.apache.http.HttpHost;

public abstract class JournalCrawler {
	protected PageDownloader downloader;
	protected HttpHost targetHost;
	
	protected JournalCrawler(HttpHost targetHost){
		downloader = new PageDownloader();
		this.targetHost = targetHost;
	}
	
	protected JournalCrawler(HttpHost targetHost, String proxyHostname, int proxyPort){
		this(targetHost);
		downloader.setupProxy(proxyHostname, proxyPort);
	}
	
	protected JournalCrawler(HttpHost targetHost, String proxyHostname, int proxyPort, String username, String password){
		this(targetHost);
		downloader.setupProxyWithCredentials(proxyHostname, proxyPort, username, password);
	}
	
	public abstract void testConnection() throws Exception;
	
	public abstract Collection<String> getYearIssuesList(int journalIdentifier, int year);
	
	public abstract Collection<String> getPaperOfAnIssue(String issueAddress);
	
	public abstract void getPaperData(String paperAddress);
}
