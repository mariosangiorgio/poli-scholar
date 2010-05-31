package it.polimi.crawler;

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IEEEConferenceCrawler extends IEEECrawler {

	public IEEEConferenceCrawler(String journalName, String journalIdentifier,
			String proxyHostname, int proxyPort, String proxyUsername,
			String proxyPassword) {
		super(journalName, journalIdentifier, proxyHostname, proxyPort,
				proxyUsername, proxyPassword);
	}

	public IEEEConferenceCrawler(String journalName, String journalIdentifier,
			String proxyHostname, int proxyPort) {
		super(journalName, journalIdentifier, proxyHostname, proxyPort);
	}

	public IEEEConferenceCrawler(String journalName, String journalIdentifier) {
		super(journalName, journalIdentifier);
	}

	@Override
	public Collection<String> getIssuesList(int year) {
		Collection<String> editions = new Vector<String>();

		try {
			Pattern edition = Pattern
					.compile("<a href=\"(mostRecentIssue.jsp\\?punumber=\\d*)\">[^,]*,\\s*"
							+ year + ".");

			System.out.println("Getting the " + year + " edition of "
					+ journal.getName());

			String editionList = downloader.getPage(targetHost,
					"/xpl/conhome.jsp?punumber=" + journalCode);

			Matcher matcher = edition.matcher(editionList);
			while (matcher.find()) {
				editions.add("/xpl/"+matcher.group(1));
			}
		} catch (Exception e) {
			System.err.println("Something went wrong getting the " + year
					+ " issues of " + journal.getName() + ": " + e);
		}

		return editions;
	}
}
