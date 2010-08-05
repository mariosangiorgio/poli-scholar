package it.polimi.analyzer;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.tartarus.snowball.ext.porterStemmer;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

public class Stemmer extends Pipe {
	private transient porterStemmer stemmer;

	private static final long serialVersionUID = -2270190864067551888L;

	public Stemmer() {
		stemmer = new porterStemmer();
	}

	private void readObject(ObjectInputStream aInputStream)
			throws ClassNotFoundException, IOException {
		aInputStream.defaultReadObject();
		stemmer = new porterStemmer();
	}

	public Instance pipe(Instance carrier) {
		TokenSequence tokenSequence = (TokenSequence) carrier.getData();
		TokenSequence stemmedTokenSequence = new TokenSequence();

		for (int i = 0; i < tokenSequence.size(); i++) {
			Token token = tokenSequence.get(i);
			stemmer.setCurrent(token.getText());
			stemmer.stem();
			stemmedTokenSequence.add(new Token(stemmer.getCurrent()));
		}
		carrier.setData(stemmedTokenSequence);
		return carrier;
	}

}
