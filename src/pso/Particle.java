package pso;

import java.util.List;
import java.util.Random;

import app.Solution;

public class Particle extends Solution{
	public double[] position;
	public double[] pBestPosition;
	public double[] velocity;
	public double pBest;
	public double fitness;
	
	public Particle(List<double[]> dimensionList) {
		int dimensionNo = dimensionList.size();
		position = new double[dimensionNo];
		velocity = new double[dimensionNo];
		pBestPosition = new double[dimensionNo];
		Random generator = new Random();
		for (int i = 0; i < dimensionNo; i++) {
			double positionLowerBound = dimensionList.get(i)[0];
            double positionUpperBound = dimensionList.get(i)[1]; 
            double velocityLowerBound = -100;
            double velocityUpperBound = 100;
            
            position[i] = positionLowerBound + (positionUpperBound - positionLowerBound) * generator.nextDouble();
            velocity[i] = velocityLowerBound + (velocityUpperBound - velocityLowerBound) * generator.nextDouble();
		}
	}
	@Override
    public String toString(){
		String result = "(";
		for(int i = 0; i < position.length; i++) {
			result += Double.toString(position[i]);
            
            if((i + 1) != position.length){
            	result += ",";
            }
		}
		result += ")";
		return result;
	}
}
