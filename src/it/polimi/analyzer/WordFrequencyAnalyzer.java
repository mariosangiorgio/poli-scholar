package it.polimi.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tartarus.snowball.ext.porterStemmer;

public class WordFrequencyAnalyzer {
	private porterStemmer stemmer;
	private Pattern nonAlphabetic	= Pattern.compile("[^a-zA-Z]");
	
	public WordFrequencyAnalyzer() {
		stemmer = new porterStemmer();
	}

	public Map<String, Integer> getFrequencyCount(String text) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		String cleanedText, stemmedWord;
		Integer frequencyCounter;

		// Keeping just the alphabetic characters and putting all the characters lowercase
		Matcher matcher = nonAlphabetic.matcher(text);
		cleanedText = matcher.replaceAll(" ");
		cleanedText = cleanedText.toLowerCase();
		String[] words = cleanedText.split("\\s+");
		
		//Setting up the stoplist
		StopList stoplist = StopList.getStoplist();

		for (String currentWord : words) {
			if(stoplist.contains(currentWord)){
				continue;
			}

			stemmer.setCurrent(currentWord);
			stemmer.stem();
			stemmedWord = stemmer.getCurrent();

			frequencyCounter = result.get(stemmedWord);
			if (frequencyCounter == null) {
				result.put(stemmedWord, 1);
			} else {
				result.put(stemmedWord, frequencyCounter + 1);
			}
		}
		return result;
	}
}
