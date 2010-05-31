import java.io.IOException;

import org.apache.http.HttpHost;

import it.polimi.webClient.ContentDownloader;
import it.polimi.webClient.DownloadException;


public class TestClient {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws DownloadException 
	 */
	public static void main(String[] args) throws DownloadException, IOException {
		ContentDownloader downloader = new ContentDownloader();
		downloader.setupProxyWithCredentials("proxy.polimi.it", 8080, "S733085", "123365");
		HttpHost host = new HttpHost("ieeexplore.ieee.org");
		String page;
		//page = downloader.getPage(host, "/");
		//page = downloader.getPage(host, "/xpl/mostRecentIssue.jsp?punumber=6783");
		System.out.println("***************************************************");
		page = downloader.getPage(host, "/xpls/abs_all.jsp?arnumber=840991");
		System.out.println(page);
		System.out.println("***************************************************");
	}

}
