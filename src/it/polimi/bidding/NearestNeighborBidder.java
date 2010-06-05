package it.polimi.bidding;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import weka.core.CosineDistance;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.DocumentLoader;
import weka.core.converters.TextDirectoryLoader;
import weka.core.neighboursearch.LinearNNSearch;
import weka.core.neighboursearch.NearestNeighbourSearch;
import weka.core.stemmers.SnowballStemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class NearestNeighborBidder extends Bidder {
	private NearestNeighbourSearch classifier;
	private int numberOfNeighbors;
	private StringToWordVector converter;
	private Instances dataSet;

	public NearestNeighborBidder(int numberOfNeighbors) {
		this.numberOfNeighbors = numberOfNeighbors;

		converter = new StringToWordVector();
		converter.setStemmer(new SnowballStemmer("porter"));
		converter.setUseStoplist(true);
		converter.setLowerCaseTokens(true);
		converter.setDoNotOperateOnPerClassBasis(true);
		converter.setOutputWordCounts(true);
		converter.setIDFTransform(true);
		int[] attributes = new int[1];
		attributes[0] = 0;
		converter.setAttributeIndicesArray(attributes);
	}

	@Override
	public String getReviewer(String documentContent) {
		String selectedReviewer = null;
		Instance document;
		HashMap<String,Integer> possibleReviewers = new HashMap<String, Integer>();

		double[] newInst = null;
		newInst = new double[3];
		newInst[0] = (double) dataSet.attribute(0).addStringValue(
				documentContent);
		/*
		 * No value is set for newInst at position 1, that is the class value
		 */
		document = new DenseInstance(1.0, newInst);
		document.setDataset(dataSet);

		try {
			converter.input(document);
			document = converter.output();

			Instances neighbors = classifier.kNearestNeighbours(document,
					numberOfNeighbors);
			Iterator<Instance> iterator = neighbors.iterator();

			while (iterator.hasNext()) {
				Instance instance = iterator.next();
				String filename = instance.stringValue(0);
				String reviewer = instance.classAttribute().value(
						(int) instance.classValue());
				System.out.println(filename
						+ ", "
						+ reviewer);
				if(possibleReviewers.containsKey(reviewer)){
					possibleReviewers.put(reviewer, possibleReviewers.get(reviewer) + 1);
				}
				else{
					possibleReviewers.put(reviewer, 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		int maxFound = 0;
		for(String reviewer : possibleReviewers.keySet()){
			int count = possibleReviewers.get(reviewer);
			if(count > maxFound){
				maxFound = count;
				selectedReviewer = reviewer;
			}
			System.out.println(reviewer+"\t"+count);
		}
		return selectedReviewer;
	}

	@Override
	public void train(String pathToReviewers) {
		TextDirectoryLoader loader = new DocumentLoader();
		loader.setOutputFilename(true);
		try {
			loader.setDirectory(new File(pathToReviewers));
			dataSet = loader.getDataSet();

			converter.setInputFormat(dataSet);
			Instances vectorModels = Filter.useFilter(dataSet, converter);

			classifier = new LinearNNSearch(vectorModels);
			classifier.setDistanceFunction(new CosineDistance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
