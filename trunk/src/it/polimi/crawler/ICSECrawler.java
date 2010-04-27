package it.polimi.crawler;

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ICSECrawler extends ACMCrawler {

	public ICSECrawler(String journalName, String journalIdentifier,
			String proxyHostname, int proxyPort, String proxyUsername,
			String proxyPassword) {
		super(journalName, journalIdentifier, proxyHostname, proxyPort,
				proxyUsername, proxyPassword);
	}

	public ICSECrawler(String journalName, String journalIdentifier) {
		super(journalName, journalIdentifier);
	}

	public ICSECrawler(String journalName, String journalIdentifier,
			String proxyHostname, int proxyPort) {
		super(journalName, journalIdentifier, proxyHostname, proxyPort);
	}

	public Collection<String> getIssuesList(int year) {
		Vector<String> issueAddresses = new Vector<String>();
		String.format("Y", year);
		Pattern indexPattern = Pattern
				.compile("ICSE '"
						+ String.format("%02d", year % 100)
						+ "\\s*</td>\\s*<td class=\"small-text\"><strong><a href=\"([^\"]*)\">");

		try {
			System.out.println("Getting the " + year + " issues of "
					+ journal.getName());

			String yearIssues = downloader.getPage(targetHost, "/toc.cfm?id="
					+ journalCode);

			Matcher matcher = indexPattern.matcher(yearIssues);
			while (matcher.find()) {
				issueAddresses.add(matcher.group(1));
			}
		} catch (Exception e) {
			System.err.println("Something went wrong getting the " + year
					+ " issues of " + journal.getName() + ": " + e);
		}

		return issueAddresses;
	}
}
