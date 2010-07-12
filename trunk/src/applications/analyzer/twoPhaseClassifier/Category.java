package applications.analyzer.twoPhaseClassifier;

public class Category {
	private String name;
	private int count;
	
	public Category(String name,int count){
		this.name = name;
		this.count = count;
	}
	
	public String getName(){
		return name;
	}
	
	public int getCount(){
		return count;
	}
}
