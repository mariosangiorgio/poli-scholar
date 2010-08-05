package it.polimi.bidding;


public class AuthorProfile extends PaperCollection{
	private String authorName;
	
	public AuthorProfile(String authorName){
		this.authorName = authorName;
	}
	
	public String getAuthorName(){
		return authorName;
	}
}
