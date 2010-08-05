package temporaryCode.validation;

import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {
	private static FileWriter output;
	
	public static void addNewLine(String content){
		if(output == null){
			try {
				output = new FileWriter("output.csv");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			output.write(content+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(){
		if(output != null){
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
