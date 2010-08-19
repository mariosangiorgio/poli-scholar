package it.polimi.bidding;

public class Paper {
	private String name;
	private String content;
	//TODO: choose the best dictionary size
	private final static int NUMBER_OF_WORDS_TO_KEEP = 20000;
	private static VectorSpaceModel vectorSpaceModel = new VectorSpaceModel(NUMBER_OF_WORDS_TO_KEEP);

	public Paper(String name, String content) {
		name = name.substring(0, name.length()-4);
		this.name = name;
		this.content = name + "\n\n" + content;
		//Adding the instance to the vector space model dataset
		vectorSpaceModel.addNewInstance(content);
	}
	
	public String getName(){
		return name;
	}

	public String getContent() {
		return content;
	}
	
	public double getDistance(Paper paper){
		return vectorSpaceModel.getCosine(content,paper.content);
	}
}
