package it.polimi.analyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Scanner;
import java.util.Vector;

public class StopList {
	private static StopList stoplist;
	private Collection<String> words;
	
	private StopList(){
		words  = new Vector<String>();
		//This method loads the default stoplist
		FileInputStream input = null;
		try {
			input = new FileInputStream("resources/english.stoplist");
			InputStreamReader reader = new InputStreamReader(input);
			Scanner	scanner = new Scanner(reader);
			while(scanner.hasNext()){
				words.add(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static StopList getStoplist(){
		if(stoplist == null){
			stoplist = new StopList();
		}
		return stoplist;
	}
	
	public boolean contains(String word){
		return words.contains(word);
	}
}
