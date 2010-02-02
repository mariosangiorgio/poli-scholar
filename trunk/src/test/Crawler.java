package test;

import it.polimi.crawler.IEEECrawler;
import it.polimi.crawler.JournalCrawler;
import it.polimi.webClient.DownloadException;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;

public class Crawler {
	
	public static void main(String[] args) throws ClientProtocolException, IOException, DownloadException, URISyntaxException {
		String proxyHostname= "proxy.polimi.it";
		int	   proxyPort	= 8080;
		
		JournalCrawler ieeeCrawler;
		
		if(args.length == 2){
			String username		= args[0];
			String password		= args[1];
			System.out.println("Using the provided username and password for the proxy");
			ieeeCrawler = new IEEECrawler("TSE", "32", proxyHostname, proxyPort, username, password);
		}
		else{
			System.out.println("No proxy login information provided");
			ieeeCrawler = new IEEECrawler("TSE", "32", proxyHostname, proxyPort);
		}
		
		try {
			ieeeCrawler.testConnection();
			//This is also useful to make the ieee website recognize the Politecnico subscription
		} catch (Exception e) {
			System.err.println("Connection problems");
			e.printStackTrace();
			return;
		}
		
		ieeeCrawler.getYearArticles(2010);
		
		//ieeeCrawler.getPaperData("http://ieeexplore.ieee.org/xpls/abs_all.jsp?isnumber=5401361&arnumber=5196681&count=11&index=3");
		
		/*
		for(String issue:ieeeCrawler.getYearIssuesList(32,2009)){
			for(String paper:ieeeCrawler.getPaperOfAnIssue(issue)){
				System.out.println(paper);
			}
		}
		*/
	}

}
