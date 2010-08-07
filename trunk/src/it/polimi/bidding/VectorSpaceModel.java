package it.polimi.bidding;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class VectorSpaceModel {
	private Instances dataSet;
	private Instances vectorModel;
	private StringToWordVector converter;

	public VectorSpaceModel() {
		initializeConverter();
	}

	private void initializeConverter() {
		// Getting stopwords
		URL stopwords = getClass().getResource(
				"/resources/english and computer science.stoplist");

		dataSet = getDataSetStructure();

		converter = new StringToWordVector();
		converter.setStemmer(new SnowballStemmer("porter"));
		converter.setStopwords(new File(stopwords.getFile()));
		converter.setLowerCaseTokens(true);
		converter.setDoNotOperateOnPerClassBasis(true);
		converter.setOutputWordCounts(true);
		converter.setIDFTransform(true);
		int[] attributes = new int[1];
		attributes[0] = 0;
		converter.setAttributeIndicesArray(attributes);

		try {
			converter.setInputFormat(dataSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Instances getDataSetStructure() {
		Instances structure;

		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		atts.add(new Attribute("text", (ArrayList<String>) null));
		// This creates a new attribute named text with each string as
		// admissible value

		structure = new Instances("Document content", atts, 0);

		return structure;
	}

	private Instance createNewInstance(String content) {
		double[] newInstance = new double[1];
		newInstance[0] = (double) dataSet.attribute(0).addStringValue(content);

		Instance document;
		document = new DenseInstance(1.0, newInstance);
		document.setDataset(dataSet);

		return document;
	}

	public void addNewInstance(String content) {
		dataSet.add(createNewInstance(content));
	}

	public double getCosine(String firstDocument, String secondDocument) {
		if (vectorModel == null) {
			try {
				vectorModel = Filter.useFilter(dataSet, converter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Instance firstInstance = null, secondInstance = null;

		try {
			converter.input(createNewInstance(firstDocument));
			firstInstance = converter.output();
			converter.input(createNewInstance(secondDocument));
			secondInstance = converter.output();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return computeCosine(firstInstance, secondInstance);
	}

	private double computeCosine(Instance firstInstance, Instance secondInstance) {
		double dotProduct = 0, normFirstInstance = 0, normSecondInstance = 0;

		// This cycle starts from to skip the filename and the class information
		for (int i = 0; i < firstInstance.numAttributes(); i++) {
			dotProduct += firstInstance.value(i) * secondInstance.value(i);
			normFirstInstance += firstInstance.value(i)
					* firstInstance.value(i);
			normSecondInstance += secondInstance.value(i)
					* secondInstance.value(i);
		}
		normFirstInstance = Math.sqrt(normFirstInstance);
		normSecondInstance = Math.sqrt(normSecondInstance);

		return dotProduct / (normFirstInstance * normSecondInstance);
	}
}
