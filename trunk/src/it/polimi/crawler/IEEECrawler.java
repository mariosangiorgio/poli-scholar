package it.polimi.crawler;

import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.Author;

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpHost;
import org.hibernate.Query;

public class IEEECrawler extends JournalCrawler {
	private static final Pattern issueIdentifierPattern = Pattern
			.compile("\\[\"[^\"]*\",\"(\\d*)\"\\]");
	private static final Pattern articleListPattern = Pattern
			.compile("<a href=\"/xpls/abs_all\\.jsp\\?arnumber=(\\d*)\">AbstractPlus</a>");

	private static final Pattern titlePattern = Pattern
			.compile("var artTitle='([^']*)';");
	private static final Pattern authorsPattern = Pattern
			.compile("var author='([^']*)';");
	private static final Pattern authorInfo = Pattern
			.compile("\\+([^,]*), ([^\\+]*)");
	private static final Pattern abstractPattern = Pattern
			.compile("<a name=\"Abstract\"><h2>Abstract</h2></a>\\s*<p>([^<]*)</p>");
	private static final Pattern articleIdentifierPattenr = Pattern
			.compile("var arNumber='(\\d*)';");

	public IEEECrawler(String journalName, String journalIdentifier,
			String proxyHostname, int proxyPort) {
		super(journalName, journalIdentifier, new HttpHost(
				"ieeexplore.ieee.org"), proxyHostname, proxyPort);
	}

	public IEEECrawler(String journalName, String journalIdentifier,
			String proxyHostname, int proxyPort, String proxyUsername,
			String proxyPassword) {
		super(journalName, journalIdentifier, new HttpHost(
				"ieeexplore.ieee.org"), proxyHostname, proxyPort,
				proxyUsername, proxyPassword);
	}

	public IEEECrawler(String journalName, String journalIdentifier) {
		super(journalName, journalIdentifier, new HttpHost(
				"ieeexplore.ieee.org"));
	}

	@Override
	public Article getPaperData(String paperAddress) throws Exception {
		Article article = null;

		System.out.println("Downloading the " + paperAddress
				+ " paper information");

		String dataPage = downloader.getPage(targetHost, paperAddress);

		Matcher titleMatcher = titlePattern.matcher(dataPage);
		Matcher authorsMatcher = authorsPattern.matcher(dataPage);
		if (titleMatcher.find()) {
			String title = StringEscapeUtils
					.unescapeHtml(titleMatcher.group(1));
			authorsMatcher.find();
			String authors = StringEscapeUtils.unescapeHtml(authorsMatcher
					.group(1));

			String lowercaseTitle = title.toLowerCase();
			if (lowercaseTitle.toLowerCase().startsWith("editorial:")
					|| lowercaseTitle.contains("guest editor")
					|| lowercaseTitle.contains("ieee computer society")
					|| lowercaseTitle.contains("reviewers list")) {
				throw new Exception(
						"This paper is an editorial, a reviewer list or an index");
			}

			Query articleQuery = session.getNamedQuery("findArticleByTitleAndJournal");
			articleQuery.setParameter("title", title);
			articleQuery.setParameter("journal", journal);
			
			article = (Article) articleQuery.uniqueResult();

			if (article == null) {
				article = new Article();

				// Title
				article.setTitle(title);

				// Author names
				Matcher authorMatcher = authorInfo.matcher(authors);
				while (authorMatcher.find()) {
					String authorName = StringEscapeUtils
							.unescapeHtml(authorMatcher.group(2));
					String authorSurname = StringEscapeUtils
							.unescapeHtml(authorMatcher.group(1));
					// Getting the author if it is already in the database
					Query query = session.getNamedQuery("findAuthorByName");
					query.setParameter("authorName", authorName);
					query.setParameter("authorSurname", authorSurname);

					Author author = (Author) query.uniqueResult();
					if (author == null) {
						author = new Author(authorName, authorSurname);
					}
					article.addAuthor(author);
				}
				for (Author author : article.getAuthors()) {
					author.addArticle(article);
				}

				Matcher abstractMatcher = abstractPattern.matcher(dataPage);
				if (abstractMatcher.find()) {
					article.setArticleAbstract(abstractMatcher.group(1));
				} else {
					throw new Exception(
							"The paper doesn't have and abstract and has been skipped");
				}

				Matcher identifierMatcher = articleIdentifierPattenr
						.matcher(dataPage);
				if (identifierMatcher.find()) {
					String articleNumber = identifierMatcher.group(1), issueNumber = identifierMatcher
							.group(1);
					article.setFullTextPdf(downloader.getBinaryData(targetHost,
							"/stampPDF/getPDF.jsp?tp=&arnumber="
									+ articleNumber + "&isnumber="
									+ issueNumber));
				}
			} else {
				session.evict(article);
				session.clear();

				throw new Exception("Paper already downloaded");
			}
		}
		return article;
	}

	@Override
	public Collection<String> getPapersOfAnIssue(String issueAddress) {
		Collection<String> papersPages = new Vector<String>();
		try {
			System.out.println("Getting the papers of the " + issueAddress
					+ " issue of " + journal.getName());

			// This cycle crawls the sub-pages
			int currentPage = 1;
			int fetchedAddresses = -1;
			while (fetchedAddresses != 0) {
				fetchedAddresses = 0;
				String issuePage = issueAddress + "&asf_pn=" + currentPage;
				String issueIndex = downloader.getPage(targetHost, issuePage);

				Matcher matcher = articleListPattern.matcher(issueIndex);
				while (matcher.find()) {
					String paperIdentifier = matcher.group(1);
					papersPages.add("/xpls/abs_all.jsp?arnumber="
							+ paperIdentifier);
					fetchedAddresses++;
				}
				currentPage++;
			}
		} catch (Exception e) {
			System.err.println("Something went wrong getting the "
					+ issueAddress + " issue of " + journal.getName() + ": "
					+ e);
		}
		return papersPages;
	}

	@Override
	public Collection<String> getIssuesList(int year) {
		Collection<String> issuesAddresses = new Vector<String>();

		try {
			Pattern issues = Pattern
					.compile("\""
							+ year
							+ "\" : \\[(\\[\"[^\"]*\",\"\\d*\"\\](?:,\\[\"[^\"]*\",\"\\d*\"\\])*)\\s*]");

			System.out.println("Getting the " + year + " issues of "
					+ journal.getName());

			String yearIssues = downloader.getPage(targetHost,
					"/xpl/RecentIssue.jsp?punumber=" + journalCode + "&year="
							+ year);

			Matcher matcher = issues.matcher(yearIssues);
			while (matcher.find()) {
				Matcher issueIdentifierMatcher = issueIdentifierPattern
						.matcher(matcher.group(1));
				while (issueIdentifierMatcher.find()) {
					issuesAddresses.add("/xpl/tocresult.jsp?asf_iid="
							+ issueIdentifierMatcher.group(1));
				}
			}
		} catch (Exception e) {
			System.err.println("Something went wrong getting the " + year
					+ " issues of " + journal.getName() + ": " + e);
		}

		return issuesAddresses;
	}
}
