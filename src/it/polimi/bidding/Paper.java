package it.polimi.bidding;

public class Paper {
	private String name;
	private String content;

	public Paper(String name, String content) {
		name = name.substring(0, name.length()-4);
		this.name = name;
		this.content = name + "\n\n" + content;
	}
	
	public String getName(){
		return name;
	}

	public String getContent() {
		return content;
	}
	
	public float getDistance(Paper paper){
		//TODO: Implement
		return 0f;
	}
}
