package it.polimi.crawler;

import it.polimi.data.hibernate.HibernateSessionManager;
import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.Author;
import it.polimi.data.hibernate.entities.Journal;

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpHost;
import org.hibernate.Query;
import org.hibernate.Session;

public class IEEECrawler extends JournalCrawler {
	private final Pattern issueIdentifierPattern = Pattern
			.compile("\\[\"[^\"]*\",\"(\\d*)\"\\]");
	private final Pattern articleListPattern = Pattern
			.compile("<a href=\"/xpls/abs_all\\.jsp\\?arnumber=(\\d*)\">AbstractPlus</a>");

	private final Pattern titlePattern = Pattern
			.compile("var artTitle='([^']*)';");
	private final Pattern authorsPattern = Pattern
			.compile("var author='([^']*)';");
	private final Pattern authorInfo = Pattern.compile("\\+([^,]*), ([^\\+]*)");
	private final Pattern abstractPattern = Pattern
			.compile("<a name=\"Abstract\"><h2>Abstract</h2></a>\\s*<p>([^<]*)</p>");
	private final Pattern articleIdentifierPattenr = Pattern
			.compile("var arNumber='(\\d*)';");
	
	private Session session = HibernateSessionManager.getNewSession();

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
		
		System.out.println("Downloading the "+paperAddress+" paper information");
		
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

			article = (Article) session.getNamedQuery("findArticleByTitle")
					.setParameter("title", title).uniqueResult();

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

			}
			for (Author author : article.getAuthors()) {
				author.addArticle(article);
				session.saveOrUpdate(author);
				session.evict(author);
			}

			Matcher abstractMatcher = abstractPattern.matcher(dataPage);
			if (abstractMatcher.find()) {
				article.setArticleAbstract(abstractMatcher.group(1));
			}
			else{
				throw new Exception("The paper doesn't have and abstract and has been skipped");
			}

			Matcher identifierMatcher = articleIdentifierPattenr
					.matcher(dataPage);
			if (identifierMatcher.find()) {
				String articleNumber = identifierMatcher.group(1), issueNumber = identifierMatcher
						.group(1);
				article.setFullTextPdf(downloader.getBinaryData(targetHost,
						"/stampPDF/getPDF.jsp?tp=&arnumber=" + articleNumber
								+ "&isnumber=" + issueNumber));
			}
		}
		return article;
	}

	@Override
	public Collection<String> getPapersOfAnIssue(String issueIdentifier) {
		Collection<String> papersPages = new Vector<String>();
		try {
			System.out.println("Getting the papers of the " + issueIdentifier
					+ " issue of IEEE Transactions on Software Engineering");

			// This cycle is to crawl also the sub-pages
			int currentPage = 1;
			int fetchedAddresses = -1;
			while (fetchedAddresses != 0) {
				fetchedAddresses = 0;
				String issuePage = "/xpl/tocresult.jsp?asf_iid="
						+ issueIdentifier + "&asf_pn=" + currentPage;
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
					+ issueIdentifier
					+ " issue of IEEE Transactions on Software Engineering: "+e);
		}
		return papersPages;
	}

	@Override
	public void getYearArticles(int year) {
		// Looking if the journal already appears in the database and creating
		// it if it doesn't
		if(session==null || !session.isOpen()){
			session = HibernateSessionManager.getNewSession();
		}
		session.beginTransaction();
		
		Journal journal = (Journal) session.getNamedQuery(
				"findJournalByName").setParameter("journalName",
				journalName).uniqueResult();

		if (journal == null) {
			journal = new Journal(journalName);
			session.saveOrUpdate(journal);
		}

		Collection<String> issuesIdentifiers = new Vector<String>();
		try {
			Pattern issues = Pattern
					.compile("\""
							+ year
							+ "\" : \\[(\\[\"[^\"]*\",\"\\d*\"\\](?:,\\[\"[^\"]*\",\"\\d*\"\\])*)\\s*]");
			System.out.println("Getting the " + year
					+ " issues of IEEE Transactions on Software Engineering");

			String yearIssues = downloader.getPage(targetHost,
					"/xpl/RecentIssue.jsp?punumber=" + journalIdentifier
							+ "&year=" + year);

			Matcher matcher = issues.matcher(yearIssues);
			while (matcher.find()) {
				Matcher issueIdentifierMatcher = issueIdentifierPattern
						.matcher(matcher.group(1));
				while (issueIdentifierMatcher.find()) {
					issuesIdentifiers.add(issueIdentifierMatcher.group(1));
				}
			}
		} catch (Exception e) {
			System.err.println("Something went wrong getting the " + year
					+ " issues of IEEE Transactions on Software Engineering: "+e);
		}

		for (String issueIdentifier : issuesIdentifiers) {
			Collection<String> paperAddresses = getPapersOfAnIssue(issueIdentifier);
			for (String paperAddress : paperAddresses) {
				Article article;
				try {
					article = getPaperData(paperAddress);
					article.setYear(year);
					article.setJournal(journal);

					session.saveOrUpdate(article);
					session.evict(article);
					session.flush();
					session.clear();

				} catch (Exception e) {
					System.err.println("Something went wrong downloading "
							+ paperAddress + " from the IEEE library: "+e);
				}
			}
		}
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void testConnection() throws Exception {
		downloader.getPage(targetHost, "/");
	}

}
