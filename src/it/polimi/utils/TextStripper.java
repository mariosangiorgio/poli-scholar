package it.polimi.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class TextStripper {
	private static final Pattern nonAlphabetic = Pattern.compile("[^a-zA-Z]");
	private static final Pattern dash = Pattern.compile("\\-\\s*");
	private static final Pattern multipleWhitespaces = Pattern.compile("\\s+");

	private static final Pattern beginningOfTheAbstract = Pattern.compile(
			"(abstract\\s*[\\.\\-研:\n]?\\s*)|"
					+ "(a ?b ?s ?t ?r ?a ?c ?t\\s*[\\.\\-研:\n]?\\s*)|"
					+ "(summary\\s*[\\.\\-研:\n]?\\s*)",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern endOfTheAbstract = Pattern.compile(
			"(key\\s?words\\s*[\\-研:\n]?\\s*)|"
					+ "(i?.?\\s*introduction\\s*\n)|"
					+ "(\\d?.?\\s*introduction\\s*\n)|"
					+ "(\\d?.?\\s*introduction and motivations\\s*\n)|"
					+ "(\\d?.?\\s*motivation\\s*\n)|"
					+ "(general terms\\s*\n)|"
					+ "(index terms\\s*[\\-研:\n]?\\s*)",
			Pattern.CASE_INSENSITIVE);

	private boolean assumeAbstractLength;
	private int assumedAbstractLength;

	private boolean stripAbstract = false;

	public TextStripper() {
	}

	public TextStripper(boolean stripAbstract) {
		this.stripAbstract = stripAbstract;
		this.assumeAbstractLength = false;
	}

	public TextStripper(boolean stripAbstract, int assumedAbstractLength) {
		this.stripAbstract = stripAbstract;
		this.assumeAbstractLength = true;
		this.assumedAbstractLength = assumedAbstractLength;
	}

	public String getContent(File document) throws AbstractNotFoundException,
			IOException, PDFEncryptedException {
		String content;
		content = getFullText(document);
		if (stripAbstract) {
			content = getAbstract(content);
		}
		Matcher matcher;
		// Merging multiple whitespaces
		matcher = multipleWhitespaces.matcher(content);
		content = matcher.replaceAll(" ");
		// Removing dashes to rebuild hyphenated words
		matcher = dash.matcher(content);
		content = matcher.replaceAll("");

		return content;
	}

	public String cleanContent(String content) {
		Matcher matcher;
		// Dropping non alphabetic characters
		matcher = nonAlphabetic.matcher(content);
		content = matcher.replaceAll(" ");
		return content;
	}

	private String getFullText(PDDocument fullTextDocument) throws IOException,
			PDFEncryptedException {
		if (!fullTextDocument.isEncrypted()) {
			String fullText;

			StringWriter writer = new StringWriter();
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setSuppressDuplicateOverlappingText(false);
			stripper.setSpacingTolerance(0.5f);
			try {
				stripper.writeText(fullTextDocument, writer);
			} catch (RuntimeException e) {
				throw new IOException();
			}
			writer.close();

			fullText = writer.toString();

			return fullText;
		} else
			throw new PDFEncryptedException();
	}

	private String getFullText(File document) throws IOException,
			PDFEncryptedException {
		PDDocument fullTextDocument = PDDocument.load(document);
		String fullText;
		try {
			fullText = getFullText(fullTextDocument);
		} finally {
			if (fullTextDocument != null)
				fullTextDocument.close();
		}
		return fullText;
	}

	public String getFullText(InputStream document) throws IOException,
			PDFEncryptedException {
		PDDocument fullTextDocument = PDDocument.load(document);
		String fullText = getFullText(fullTextDocument);
		fullTextDocument.close();
		return fullText;
	}

	public String getAbstract(String fullText) throws AbstractNotFoundException {
		Matcher startOfAbstractMatcher = beginningOfTheAbstract
				.matcher(fullText);
		Matcher endOfAbstractMatcher = endOfTheAbstract.matcher(fullText);
		
		if (startOfAbstractMatcher.find()) {
			String paperAbstract;
			try {
				if (endOfAbstractMatcher.find()) {
					paperAbstract = fullText.substring(startOfAbstractMatcher
							.end(), endOfAbstractMatcher.start() - 1);
					return paperAbstract;
				}
				if (assumeAbstractLength) {
					paperAbstract = fullText.substring(startOfAbstractMatcher
							.end(), startOfAbstractMatcher.end()
							+ assumedAbstractLength);
					return paperAbstract;

				}
			} catch (RuntimeException e) {
			}

		}
		throw new AbstractNotFoundException();
	}
}
