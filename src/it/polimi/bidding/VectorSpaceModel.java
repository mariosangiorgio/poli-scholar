package it.polimi.bidding;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.CosineDistance;
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
	private CosineDistance distance;

	public VectorSpaceModel() {
		initializeConverter();
	}

	private void initializeConverter() {
		distance = new CosineDistance();
		dataSet = getDataSetStructure();
		
		converter = new StringToWordVector();
		converter.setStemmer(new SnowballStemmer("porter"));
		converter.setUseStoplist(true);// TODO: add custom words
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
		// This creates a new attribute named text with each string as admissible value

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

	public double getDistance(String firstDocument, String secondDocument) {
		if(vectorModel == null){
			try {
				vectorModel = Filter.useFilter(dataSet, converter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Instance firstInstance, secondInstance;

		try {
			converter.input(createNewInstance(firstDocument));
			converter.input(createNewInstance(secondDocument));
		} catch (Exception e) {
			e.printStackTrace();
		}

		firstInstance = converter.output();
		secondInstance = converter.output();

		return distance.distance(firstInstance, secondInstance);
	}
}
