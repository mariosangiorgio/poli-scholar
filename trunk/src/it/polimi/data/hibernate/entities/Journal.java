package it.polimi.data.hibernate.entities;

import java.util.Collection;

public class Journal {
	private long identifier;
	private Collection<Issue> issues;

	@SuppressWarnings("unused")
	private void setIdentifier(long identifier) {
		this.identifier = identifier;
	}

	public long getIdentifier() {
		return identifier;
	}

}
