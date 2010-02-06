package applications;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import it.polimi.crawler.IEEECrawler;
import it.polimi.crawler.JournalCrawler;
import it.polimi.data.hibernate.HibernateUtil;
import it.polimi.webClient.DownloadException;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;

public class Crawler {
	
	public static void main(String[] args) throws ClientProtocolException, IOException, DownloadException, URISyntaxException {
		String proxyHostname= "proxy.polimi.it";
		int	   proxyPort	= 8080;
		String proxyUsername = null, proxyPassword = null;
		String journalName = null, journalIdentifier = null;
		int	from = -1, to = -1;
		StringBuffer buffer = new StringBuffer();
		boolean cleanup = false;
		
		LongOpt longOptions[] = new LongOpt[8];
		longOptions[0] = new LongOpt("journalName", LongOpt.REQUIRED_ARGUMENT, buffer, 'n');
		longOptions[1] = new LongOpt("journalIdentifier", LongOpt.REQUIRED_ARGUMENT, buffer, 'i');
		
		longOptions[2] = new LongOpt("username", LongOpt.REQUIRED_ARGUMENT, buffer, 'u');
		longOptions[3] = new LongOpt("password", LongOpt.REQUIRED_ARGUMENT, buffer, 'p');
		
		longOptions[4] = new LongOpt("from", LongOpt.REQUIRED_ARGUMENT, buffer, 'f');
		longOptions[5] = new LongOpt("to", LongOpt.REQUIRED_ARGUMENT, buffer, 't');
		
		longOptions[6] = new LongOpt("cleanup", LongOpt.NO_ARGUMENT, null, 'c');
		longOptions[7] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		
		Getopt options = new Getopt("Crawler", args, "", longOptions);
		while(options.getopt() != -1){
			int option = options.getLongind();
			switch (option) {
			case 0:
				journalName = options.getOptarg();
				break;
			case 1:
				journalIdentifier = options.getOptarg();
				break;
			case 2:
				proxyUsername = options.getOptarg();
				break;
			case 3:
				proxyPassword = options.getOptarg();
				break;
			case 4:
				from = Integer.parseInt(options.getOptarg());
				break;
			case 5:
				to = Integer.parseInt(options.getOptarg());
				break;
			case 6:
				cleanup = true;
				break;
			case 7:
				printUsage();
			default:
				break;
			}
		}
		
		if(cleanup){
			try {
				HibernateUtil.resetDatabase();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		
		if(journalIdentifier == null || journalName == null){
			System.out.println("Missing journal name or identifier");
			printUsage();
		}
		
		JournalCrawler ieeeCrawler;
		
		if(proxyUsername != null && proxyPassword != null){
			System.out.println("Using the provided username and password for the proxy");
			ieeeCrawler = new IEEECrawler(journalName, journalIdentifier, proxyHostname, proxyPort, proxyUsername, proxyPassword);
		}
		else{
			System.out.println("No proxy login information provided");
			ieeeCrawler = new IEEECrawler(journalName, journalIdentifier, proxyHostname, proxyPort);
		}
		
		try {
			ieeeCrawler.testConnection();
			//This is also useful to make the ieee website recognize the Politecnico subscription
		} catch (Exception e) {
			System.err.println("Connection problems");
			e.printStackTrace();
			return;
		}
		
		for(int year=from; year<= to; year++){
			ieeeCrawler.getYearArticles(year);	
		}
	}

	private static void printUsage() {
		// TODO Auto-generated method stub
		
	}

}
