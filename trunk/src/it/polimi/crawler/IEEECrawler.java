package it.polimi.crawler;

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;

public class IEEECrawler extends JournalCrawler {
	private final Pattern issuesListPattern = Pattern
			.compile("<option value=\"(\\d*)\"(?: selected)?>\\s*Volume [\\s\\w-,]*</option>");
	private final Pattern articleListPattern = Pattern
			.compile("<a href=\"(/xpls/abs_all.jsp\\?isnumber=\\d*&arnumber=(\\d*)&count=\\d*&index=\\d*)\" class=\"bodyCopySpaced\">AbstractPlus</a>");

	public IEEECrawler(String proxyHostname, int proxyPort, String username,
			String password) {
		super(new HttpHost("ieeexplore.ieee.org"), proxyHostname, proxyPort,
				username, password);
	}

	public IEEECrawler(String proxyHostname, int proxyPort) {
		super(new HttpHost("ieeexplore.ieee.org"), proxyHostname, proxyPort);
	}

	public IEEECrawler() {
		super(new HttpHost("ieeexplore.ieee.org"));
	}

	@Override
	public Collection<String> getYearIssuesList(int journalIdentifier, int year) {
		Collection<String> issuesPages = new Vector<String>();
		try {
			System.out.println("Getting the " + year
					+ " issues of IEEE Transactions on Software Engineering");

			String yearIssues = downloader.getPage(targetHost,
					"/xpl/RecentIssue.jsp?punumber=" + journalIdentifier + "&year=" + year);

			Matcher matcher = issuesListPattern.matcher(yearIssues);
			while (matcher.find()) {
				String issueNumber = matcher.group(1);
				issuesPages.add("/xpl/tocresult.jsp?isnumber=" + issueNumber
						+ "&isYear=" + year);
			}
		} catch (Exception e) {
			System.err.println("Something went wrong getting the " + year
					+ " issue of IEEE Transactions on Software Engineering");
			e.printStackTrace();
		}
		return issuesPages;
	}

	@Override
	public Collection<String> getPaperOfAnIssue(String issueAddress) {
		Collection<String> papersPages = new Vector<String>();
		try {
			System.out.println("Getting the papers of the " + issueAddress+ " issue of IEEE Transactions on Software Engineering");

			String issueIndex = downloader.getPage(targetHost, issueAddress);

			Matcher matcher = articleListPattern.matcher(issueIndex);
			while (matcher.find()) {
				String paperURL = matcher.group(1);
				papersPages.add(paperURL);
			}
		} catch (Exception e) {
			System.err.println("Something went wrong getting the " + issueAddress
					+ " paper of IEEE Transactions on Software Engineering");
			e.printStackTrace();
		}		return papersPages;
	}

	@Override
	public void testConnection() throws Exception {
			downloader.getPage(targetHost, "/");

	}
	
	// TODO: Get the data of a paper
}
