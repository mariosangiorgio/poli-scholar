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
	
	abstract public void testConnection() throws Exception;
	
	abstract public Collection<String> getYearIssuesList(int journalIdentifier, int year);
	
	abstract public Collection<String> getPaperOfAnIssue(String issueAddress);
	
	//public void getPaperData(String paperAddress); //TODO: implement
}
