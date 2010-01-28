package test.hibernate.entities;

public class Author {
	private Long identifier;
	
	private String fullName;
	private String affiliation;
	
	public Author(){
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public String getAffiliation() {
		return affiliation;
	}

	@SuppressWarnings("unused")
	private void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public Long getIdentifier() {
		return identifier;
	}

}
