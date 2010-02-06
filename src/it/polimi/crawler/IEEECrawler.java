package it.polimi.crawler;

import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.Author;
import it.polimi.data.hibernate.entities.Journal;

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpHost;

public class IEEECrawler extends JournalCrawler {
	/*
	 *  Pattern to match the list of the issues in the index of the journal.
	 *  The capturing group captures the identifier of each issue.
	 */
	private final Pattern issuesListPattern = Pattern
			.compile("<option value=\"(\\d*)\"(?: selected)?>\\s*Volume [\\s\\w-,]*</option>");
	
	/*
	 *  Pattern to match the list of the articles in a issue of the journal.
	 *  The first capturing group captures the relative address of each article.
	 *  The second capturing grup just captures the identifier of each article rather than the whole address.
	 */
	private final Pattern articleListPattern= Pattern
			.compile("<a href=\"(/xpls/abs_all.jsp\\?isnumber=\\d*&arnumber=(\\d*)&count=\\d*&index=\\d*)\" class=\"bodyCopySpaced\">AbstractPlus</a>");
	private final Pattern articleAbstract	= Pattern.compile("Abstract</span><br>\\s*([^<]*)");
	private final Pattern fullTextPDF		= Pattern.compile("\\s<td width=\"\\d*%\" class=\"bodyCopyBlackLarge\">Full Text: <a href=\"([^\"]*)\" class=\"bodyCopy\">PDF</a>");
	
	// Patterns to extract data.
	
	/*
	 * This patterns gets the title and the author of and article.
	 * The first capturing group captures the title.
	 * The second capturing group captures the names of the authors.
	 * The third capturing group captures the affiliation of the authors.
	 */
	private final Pattern titleAndAuthors	= Pattern.compile("<p><span class=\"headNavBlueXLarge2\">\\s*([^<]*)</span></p>\\s*<p>\\s*<span class=\"bodyCopyBlackLargeSpaced\">((?:\\s*<a href=\"[^\"]*\" class=\"bodyCopy\">[^<]*</a>&nbsp;&nbsp;)*)\\s*<br>([^<]*)</span></p>");
	private final Pattern authorName		= Pattern.compile("\\s*<a href=\"[^\"]*\" class=\"bodyCopy\">\\s*([^<]*)</a>");
	
	public IEEECrawler(String journalName, String journalIdentifier, String proxyHostname, int proxyPort, String username,
			String password) {
		super(journalName, journalIdentifier, new HttpHost("ieeexplore.ieee.org"),
				proxyHostname, proxyPort, username, password);
	}

	public IEEECrawler(String journalName, String journalIdentifier, String proxyHostname, int proxyPort) {
		super(journalName, journalIdentifier, new HttpHost("ieeexplore.ieee.org"), proxyHostname, proxyPort);
	}

	public IEEECrawler(String journalName, String journalIdentifier) {
		super(journalName, journalIdentifier, new HttpHost("ieeexplore.ieee.org"));
	}

	@Override
	public void getYearArticles(int year) {
		//Looking if the journal already appears in the database and creating it if it doesn't
		session.beginTransaction();
		Journal journal = (Journal) session.getNamedQuery("findJournalByName").setParameter("journalName", journalName).uniqueResult();
		
		if(journal == null){
			journal = new Journal(journalName);
		}
		
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
					+ " issues of IEEE Transactions on Software Engineering");
			e.printStackTrace();
		}
		
		for(String issuePage : issuesPages){
			Collection<String> paperAddresses = getPaperOfAnIssue(issuePage);
			for(String paperAddress:paperAddresses){
				Article article = getPaperData(paperAddress);
				article.setYear(year);
				session.saveOrUpdate(article);
				journal.addArticle(article);
			}
		}
		session.saveOrUpdate(journal);
		session.getTransaction().commit();
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
					+ " issue of IEEE Transactions on Software Engineering");
			e.printStackTrace();
		}		return papersPages;
	}

	@Override
	public Article getPaperData(String paperAddress) {
		Article article = null;
		
		try {
			String dataPage = downloader.getPage(targetHost, paperAddress);
			
			Matcher matcher = titleAndAuthors.matcher(dataPage);
			if (matcher.find()) {
				String title = StringEscapeUtils.unescapeHtml(matcher.group(1));
				
				article = (Article) session.getNamedQuery("findArticleByTitle").setParameter("title", journalName).uniqueResult();
				
				if(article == null){
					article = new Article();
					
					//Title
					article.setTitle(title);
					
					//Author affiliation
					String affiliation = StringEscapeUtils.unescapeHtml(matcher.group(3));
					
					//Author names
					Matcher authorMatcher = authorName.matcher(matcher.group(2));
					while(authorMatcher.find()){
						String authorName = StringEscapeUtils.unescapeHtml(authorMatcher.group(1));
						//Getting the author if it is already in the database
						Author author = (Author) session.getNamedQuery("findAuthorByName").setParameter("authorName", authorName).uniqueResult();
						if(author == null){
							author = new Author(authorName,affiliation);
						}
						article.addAuthor(author);
					}
				}
			}
			
			for(Author author:article.getAuthors()){
				author.addArticle(article);
				session.saveOrUpdate(author);
			}
			
			Matcher abstractMatcher = articleAbstract.matcher(dataPage);
			if(abstractMatcher.find()){
				article.setArticleAbstract(abstractMatcher.group(1));
			}
			Matcher fullTextMatcher = fullTextPDF.matcher(dataPage);
			if(fullTextMatcher.find()){
				article.setFullTextPdf(downloader.getBinaryData(targetHost, fullTextMatcher.group(1)));
			}
			
		} catch (Exception e) {
			System.err.println("Something went wrong getting the " + paperAddress
					+ " paper of IEEE Transactions on Software Engineering");
			e.printStackTrace();
		}
		return article;
	}
	
	@Override
	public void testConnection() throws Exception {
			downloader.getPage(targetHost, "/");

	}
}