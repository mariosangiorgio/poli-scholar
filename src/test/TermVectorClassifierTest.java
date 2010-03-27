package test;

import net.sf.classifier4J.ClassifierException;
import net.sf.classifier4J.vector.HashMapTermVectorStorage;
import net.sf.classifier4J.vector.TermVectorStorage;
import net.sf.classifier4J.vector.VectorClassifier;

public class TermVectorClassifierTest {
	public static void main(String[] args) throws ClassifierException {
		TermVectorStorage storage = new HashMapTermVectorStorage();
		
	    VectorClassifier vectorClassifier = new VectorClassifier(storage);
	    
	    vectorClassifier.teachMatch("africa", "on the plains of africa the lions roar " +
	    									  "in swahili ngoma means to dance "+
	    									  "nelson mandela became president of south africa " +
	    									  "the saraha dessert is expanding");
	    vectorClassifier.teachMatch("asia", "panda bears eat bamboo "+
	    									"china's one child policy has resulted in a surplus of boys "+
	    									"tigers live in the jungle");
	    vectorClassifier.teachMatch("australia", "home of kangaroos "+
	    									"Autralian's for beer - Foster "+
	    									"Steve Irvin is a herpetologist");
	    
	    System.out.println(storage.getTermVector("africa"));
	    System.out.println(storage.getTermVector("asia"));
	    System.out.println(storage.getTermVector("australia"));

	    String input = "Nelson Mandela never eats kangaroos";
	    System.out.println("Africa: "+vectorClassifier.classify("africa", input));
	    System.out.println("Asia: "+vectorClassifier.classify("asia", input));
	    System.out.println("Australia: "+vectorClassifier.classify("australia", input));

	}

}
