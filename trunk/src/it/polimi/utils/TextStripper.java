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
	private static final Pattern nonAlphabetic		 = Pattern.compile("[^a-zA-Z]");
	private static final Pattern dash				 = Pattern.compile("\\-\\s*");
	private static final Pattern multipleWhitespaces = Pattern.compile("\\s+");
	
	public static String getFullText(PDDocument fullTextDocument) throws IOException, PDFEncryptedException{
		if(!fullTextDocument.isEncrypted()){
			String fullText;
		
			StringWriter writer = new StringWriter();
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setSuppressDuplicateOverlappingText(false);
			stripper.setSpacingTolerance(0.5f);
			stripper.writeText(fullTextDocument, writer);
			writer.close();
			
			fullText = writer.toString();
			
			Matcher matcher;
			// Removing dashes to rebuild hyphenated words 
			matcher	 = dash.matcher(fullText);
			fullText = matcher.replaceAll("");
			// Dropping non alphabetic characters
			matcher	 = nonAlphabetic.matcher(fullText);
			fullText = matcher.replaceAll(" ");
			// Merging multiple whitespaces
			matcher	 = multipleWhitespaces.matcher(fullText);
			fullText = matcher.replaceAll(" ");
			return fullText;
		}else
			throw new PDFEncryptedException();
	}
	
	public static String getFullText(File document) throws IOException, PDFEncryptedException{
		PDDocument fullTextDocument = PDDocument.load(document);
		String fullText;
		try{
			fullText = getFullText(fullTextDocument);
		}
		finally
		{	
			if(fullTextDocument != null)
				fullTextDocument.close();
		}
		return fullText;		
	}
	
	public static String getFullText(InputStream document) throws IOException, PDFEncryptedException{
		PDDocument fullTextDocument = PDDocument.load(document);
		String fullText = getFullText(fullTextDocument);
		fullTextDocument.close();
		return fullText;		
	}
	
	public static String getAbstract(String fullText) throws AbstractNotFoundException{
		// TODO Implement
		return "";
	}

}
