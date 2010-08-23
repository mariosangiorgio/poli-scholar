package it.polimi.bidding;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class OutputWriter {

	public void writeGroupedSubmission(String submissionGroupOutputFile,
			PaperCollection groupedSubmissions) {
		try {
			FileWriter outputFile = new FileWriter(submissionGroupOutputFile);
			for (String topic : groupedSubmissions.getCategories()) {
				Collection<Paper> papers = groupedSubmissions.getPapers(topic);
				if(papers.size() == 0){
					continue;
				}
				outputFile.write("*** " + topic + " ***\n");
				for (Paper paper : papers) {
					outputFile.write(paper.getContent()+"\n\n");
				}
				outputFile.write("\n");
			}
			outputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeAuthorProfiles(String profilesOutputDirectory,
			Collection<AuthorPapers> userProfiles,float topicCoverage) {
		new File(profilesOutputDirectory).mkdirs();
		for (AuthorPapers authorPapers : userProfiles) {
			try {
				FileWriter outputFile = new FileWriter(profilesOutputDirectory+"/"+authorPapers.getAuthorName()+".txt");
				outputFile.write("*** "+authorPapers.getAuthorName()+" ***\n");
				for(String topic : authorPapers.getMostRelevantTopics(topicCoverage)){
					outputFile.write(topic+"\n");
				}
				outputFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeAuthorSuggestions(String suggestionFolder,
			Collection<AuthorPapers> suggestions) {
		new File(suggestionFolder).mkdirs();
		for (AuthorPapers authorPapers : suggestions) {
			try {
				FileWriter outputFile = new FileWriter(suggestionFolder+"/"+authorPapers.getAuthorName()+".txt");
				outputFile.write("*** "+authorPapers.getAuthorName()+" ***\n");
				for(String topic : authorPapers.getCategories()){
					Collection<Paper> papers = authorPapers.getPapers(topic);
					if(papers.size() == 0){
						continue;
					}
					outputFile.write("** "+topic+" **\n");
					for(Paper paper : papers){
						outputFile.write(paper.getContent());
						outputFile.write("\n\n");
					}
					outputFile.write("\n\n\n");
				}
				outputFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
