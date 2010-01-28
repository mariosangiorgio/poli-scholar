package it.polimi.webClient;


import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class PageDownloader {
	private DefaultHttpClient client;
	private static final int SLEEP_AFTER_REQUEST = 5*1000;
	
	public PageDownloader(){
		client = new DefaultHttpClient();
	}
	
	public void setupProxy(String proxyHostname, int proxyPort){
        client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxyHostname, proxyPort));
	}
	
	public void setupProxyWithCredentials(String proxyHostname, int proxyPort, String username, String password){
		setupProxy(proxyHostname, proxyPort);
		
		client.getCredentialsProvider().setCredentials(
                new AuthScope(proxyHostname, proxyPort),
                new UsernamePasswordCredentials(username, password));
	}
	
	public String getPage(HttpHost targetHost, String content) throws DownloadException, IOException{
		HttpGet request = new HttpGet(content);
        request.setHeader(HTTP.USER_AGENT,"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_1; en-us) AppleWebKit/532.3+ (KHTML, like Gecko) Version/4.0.3 Safari/531.9");
        
        HttpResponse response = client.execute(targetHost, request);
        
    	//Sleep to avoid ban
        try {
			Thread.sleep(SLEEP_AFTER_REQUEST);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        if(response.getStatusLine().getStatusCode() != 200){
        	throw new DownloadException(response.getStatusLine().getStatusCode());
        }
        
        HttpEntity entity = response.getEntity();
    	if (entity != null){
            return EntityUtils.toString(entity);
		}
    	else{
    		throw new DownloadException();
    	}
	}
	
	//TODO: Add a method to get pdf documents
}
