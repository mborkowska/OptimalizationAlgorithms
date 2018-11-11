package de;

import java.util.List;
import java.util.Random;

import app.Solution;

public class Candidate extends Solution implements Cloneable {
	public double[] variables;

	public Candidate(List<double[]> dimensionList) {
		int dimensionNo = dimensionList.size();
		variables = new double[dimensionNo];
		Random generator = new Random();
		for (int i = 0; i < dimensionNo; i++) {
			double dimensionLowerBound = dimensionList.get(i)[0];
            double dimensionUpperBound = dimensionList.get(i)[1];      
            
            variables[i] = dimensionLowerBound + (dimensionUpperBound - dimensionLowerBound) * generator.nextDouble();
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	@Override
    public String toString(){
		String result = "(";
		for(int i = 0; i < variables.length; i++) {
			result += Double.toString(variables[i]);
            
            if((i + 1) != variables.length){
            	result += ",";
            }
		}
		result += ")";
		return result;
	}
}
