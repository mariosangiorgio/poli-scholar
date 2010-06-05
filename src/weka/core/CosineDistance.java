package weka.core;

import java.util.Enumeration;

import weka.core.neighboursearch.PerformanceStats;

public class CosineDistance implements DistanceFunction {

	@Override
	public double distance(Instance first, Instance second) {
		double dotProduct = 0,
			   normFirstInstance = 0,
			   normSecondInstance = 0;
		
		//This cycle starts from to skip the filename and the class information
		for(int i=2;i<first.numAttributes();i++){
			dotProduct		   += first.value(i)*second.value(i);
			normFirstInstance  += first.value(i)*first.value(i);
			normSecondInstance += second.value(i)*second.value(i);
		}
		normFirstInstance  = Math.sqrt(normFirstInstance);
		normSecondInstance = Math.sqrt(normSecondInstance);

		return 1 - dotProduct/(normFirstInstance*normSecondInstance);
	}

	@Override
	public double distance(Instance first, Instance second,
			PerformanceStats stats) throws Exception {
		return distance(first, second);
	}

	@Override
	public double distance(Instance first, Instance second, double cutOffValue) {
		return distance(first, second);
	}

	@Override
	public double distance(Instance first, Instance second, double cutOffValue,
			PerformanceStats stats) {
		return distance(first, second);
	}

	@Override
	public String getAttributeIndices() {
		return null;
	}

	@Override
	public Instances getInstances() {
		return null;
	}

	@Override
	public boolean getInvertSelection() {
		return false;
	}

	@Override
	public void postProcessDistances(double[] distances) {
	}

	@Override
	public void setAttributeIndices(String value) {
	}

	@Override
	public void setInstances(Instances insts) {
	}

	@Override
	public void setInvertSelection(boolean value) {
	}

	@Override
	public void update(Instance ins) {
	}

	@Override
	public String[] getOptions() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration listOptions() {
		return null;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
	}
}
