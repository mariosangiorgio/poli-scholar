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

	private boolean stripAbstract = false;

	public TextStripper() {
	}

	public TextStripper(boolean stripAbstract) {
		this.stripAbstract = stripAbstract;
	}

	public String getContent(File document) throws AbstractNotFoundException,
			IOException, PDFEncryptedException {
		String content;
		content = getFullText(document);
		if (stripAbstract) {
			content = getAbstract(content);
		}
		Matcher matcher;
		// Removing dashes to rebuild hyphenated words
		matcher = dash.matcher(content);
		content = matcher.replaceAll("");
		// Dropping non alphabetic characters
		matcher = nonAlphabetic.matcher(content);
		content = matcher.replaceAll(" ");
		// Merging multiple whitespaces
		matcher = multipleWhitespaces.matcher(content);
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
			stripper.writeText(fullTextDocument, writer);
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
		Matcher startOfAbstractMatcher = Pattern.compile("abstract\\s*[\\.\\-ба:\n]?\\s*",
				Pattern.CASE_INSENSITIVE).matcher(fullText);
		Matcher endOfAbstractMatcher = Pattern.compile(
				"(keywords\\s*[\\-ба:\n]?\\s*)|"+
				"(i?.?\\s*introduction\\s*\n)|"+
				"(index terms\\s*[\\-ба:\n]?\\s*)",
				Pattern.CASE_INSENSITIVE).matcher(fullText);

		/*
		 * TODO: Manage when the keywords are mentioned not as a section name
		 * but it the title or in the body of the abstract
		 */
		if (startOfAbstractMatcher.find()) {
			String paperAbstract;
			if (endOfAbstractMatcher.find()) {
				try{
				paperAbstract = fullText.substring(
						startOfAbstractMatcher.end(), endOfAbstractMatcher
								.start() - 1);
				return paperAbstract;
				}
				catch(RuntimeException e){
				}
			}

		}
		throw new AbstractNotFoundException();
	}
}
