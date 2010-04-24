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

public class ACMCrawler extends JournalCrawler {
	private static final Pattern issueListPattern = Pattern
			.compile("toc.cfm\\?id=(\\d*)&[^>]*>Issue \\d* <small>\\(\\w* (\\d*)\\)");
	private static final Pattern articleListPattern = Pattern
			.compile("<A HREF=\"citation.cfm\\?id=(\\d*)[^>]*>\\s*abstract</A>");
	private static final Pattern titlePattern = Pattern
			.compile("<title>([^<]*)</title>");
	private static final Pattern bibTexPattern = Pattern
			.compile("'(popBibTex.cfm\\?id=[^']*)','BibTex'");
	private static final Pattern authorListPattern = Pattern
			.compile("author = \\{(.*)\\}");
	private static final Pattern authorPattern = Pattern
			.compile("([^,]*), (.*)");
	private static final Pattern abstractBeginsPattern = Pattern
			.compile("<A NAME=\"abstract\">ABSTRACT</A></span>\\s*<p class=\"abstract\">\\s*(<par>|<p>)?\\s*");
	private static final Pattern abstractEndsPattern = Pattern.compile("(</par>|</p>)");
	private static final Pattern fullTextLinkPattern = Pattern
			.compile("<A NAME=\"FullText\" title=\"Pdf\" HREF=\"([^\"]*)\" target=\"_blank\">");

	public ACMCrawler(String journalName, String journalIdentifier,
			String proxyHostname, int proxyPort) {
		super(journalName, journalIdentifier, new HttpHost("portal.acm.org"),
				proxyHostname, proxyPort);
	}

	public ACMCrawler(String journalName, String journalIdentifier,
			String proxyHostname, int proxyPort, String proxyUsername,
			String proxyPassword) {
		super(journalName, journalIdentifier, new HttpHost("portal.acm.org"),
				proxyHostname, proxyPort, proxyUsername, proxyPassword);
	}

	public ACMCrawler(String journalName, String journalIdentifier) {
		super(journalName, journalIdentifier, new HttpHost("portal.acm.org"));
	}

	@Override
	public Collection<String> getIssuesList(int year) {
		Vector<String> issueAddresses = new Vector<String>();

		try {
			System.out.println("Getting the " + year + " issues of "
					+ journal.getName());

			String yearIssues = downloader.getPage(targetHost, "/toc.cfm?id="
					+ journalCode);

			Matcher matcher = issueListPattern.matcher(yearIssues);
			while (matcher.find()) {
				if (matcher.group(2).equals(new Integer(year).toString())) {
					issueAddresses.add("/toc.cfm?id=" + matcher.group(1));
				}
			}
		} catch (Exception e) {
			System.err.println("Something went wrong getting the " + year
					+ " issues of " + journal.getName() + ": " + e);
		}

		return issueAddresses;
	}

	@Override
	public Article getPaperData(String paperAddress) throws Exception {
		Article article = null;

		System.out.println("Downloading the " + paperAddress
				+ " paper information");

		String dataPage = downloader.getPage(targetHost, paperAddress);

		Matcher titleMatcher = titlePattern.matcher(dataPage);
		// Downloading the BibTex to grab authors information
		Matcher bibTexMatcher = bibTexPattern.matcher(dataPage);
		bibTexMatcher.find();
		String bibTex = downloader.getPage(targetHost, bibTexMatcher.group(1));

		if (titleMatcher.find()) {
			String title = StringEscapeUtils
					.unescapeHtml(titleMatcher.group(1));
			
			article = (Article) session.getNamedQuery("findArticleByTitle")
					.setParameter("title", title).uniqueResult();

			if (article == null) {
				article = new Article();

				// Title
				article.setTitle(title);

				// Author names
				Matcher authorsMatcher = authorListPattern.matcher(bibTex);
				authorsMatcher.find();
				String authors = authorsMatcher.group(1);

				String[] authorsList = authors.split(" and ");
				for (String authorRecord : authorsList) {
					Matcher authorMatcher = authorPattern.matcher(authorRecord);
					authorMatcher.find();
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

				// Matching the abstract
				Matcher beginOfAbstract = abstractBeginsPattern
						.matcher(dataPage);
				Matcher endOfAbstract = abstractEndsPattern.matcher(dataPage);
				if (beginOfAbstract.find()) {
					int begin = beginOfAbstract.end();
					endOfAbstract.find(beginOfAbstract.end());
					int end = endOfAbstract.start();
					
					String rawAbstract = dataPage.substring(begin, end);
					
					// Removing HTML tags
					
					String cleanedAbstract = rawAbstract.replaceAll("<.*>", "");
					cleanedAbstract = cleanedAbstract.trim();
					
					article.setArticleAbstract(cleanedAbstract);
				} else {
					throw new Exception(
							"The paper doesn't have and abstract and has been skipped");
				}

				// Getting the full-text
				Matcher fullTextLinkMatcher = fullTextLinkPattern.matcher(dataPage);
				fullTextLinkMatcher.find();
				String fullTextLink = fullTextLinkMatcher.group(1);
				byte[] fullText = downloader.getBinaryData(targetHost, fullTextLink);
				
				article.setFullTextPdf(fullText);

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

			String issueIndex = downloader.getPage(targetHost, issueAddress);
			Matcher matcher = articleListPattern.matcher(issueIndex);

			while (matcher.find()) {
				String paperIdentifier = matcher.group(1);
				papersPages.add("/citation.cfm?id=" + paperIdentifier);
			}

		} catch (Exception e) {
			System.err.println("Something went wrong getting the "
					+ issueAddress + " issue of " + journal.getName() + ": "
					+ e);
		}
		return papersPages;
	}

}
