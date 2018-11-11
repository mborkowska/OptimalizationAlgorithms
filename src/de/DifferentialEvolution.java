package de;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class DifferentialEvolution {

	int populationSize = 500;
	double crossoverProbability = 0.5d;
	double diffferentialWeight = 0.7d;
	int iterationNo = 600;
	int dimensionNo;
	List<Candidate> population;
	List<double[]> dimensionList;
	Random generator = new Random();
	Function<Candidate, Double> getFitness;

	public DifferentialEvolution(Function<Candidate, Double> function, List<double[]> dimensionList) {
		getFitness = function;
		dimensionNo = dimensionList.size();
		population = new LinkedList<>();
		this.dimensionList = dimensionList;
		for(int i = 0; i < populationSize; i++) {
			Candidate candidate = new Candidate(dimensionList);
			population.add(candidate);
		}
		
		//Candidate bestCandidate = optimize();
		//System.out.println("DE: gBest: " + getFitness.apply(bestCandidate) + " at " + bestCandidate.toString());
	}

	public Candidate optimize() {
		for (int i = 0; i < iterationNo; i++) {
			int index = 0;
			while (index < populationSize) {

				Candidate newCandidate = null;
				Candidate originalCandidate = null;
				boolean boundsSatisfied;
				do {
					boundsSatisfied = true;
					int x = index;
					int a, b, c = -1;
					// pick three random candidates
					do {
						a = generator.nextInt(populationSize);
					} while (x == a);
					do {
						b = generator.nextInt(populationSize);
					} while (b == x || b == a);
					do {
						c = generator.nextInt(populationSize);
					} while (c == x || c == a || c == b);
					Candidate candidateA = population.get(a);
					Candidate candidateB = population.get(b);
					Candidate candidateC = population.get(c);
					Candidate candidateV = new Candidate(dimensionList);
					newCandidate = new Candidate(dimensionList);
					originalCandidate = population.get(x);

					// mutation
					for(int j = 0; j < dimensionNo; j++) {
						candidateV.variables[j] = (candidateA.variables[j] + diffferentialWeight * (candidateB.variables[j] - candidateC.variables[j]));
					}
					
					
					// crossover
					int R = generator.nextInt(dimensionList.size());
					for(int n = 0; n < dimensionList.size(); n++){
						
	                    double probability = generator.nextDouble();
	                    if (probability < crossoverProbability || n == R)  {
	                    	newCandidate.variables[n] = candidateV.variables[n];
	                    } else {
	                    	newCandidate.variables[n] = originalCandidate.variables[n];
	                    }
					}
					

					for(int n = 0; n < dimensionNo; n++){ 
	                    if(newCandidate.variables[n] < dimensionList.get(n)[0] || newCandidate.variables[n] > dimensionList.get(n)[1]){
	                    	boundsSatisfied = false;
	                    }
	                }
				} while (boundsSatisfied == false);

				// selection
				if (getFitness.apply(newCandidate) < getFitness.apply(originalCandidate)) {
					for(int n = 0; n < dimensionNo; n++){ 
						population.get(index).variables[n] = newCandidate.variables[n];
					}
				}
				index++;
			}
		}

		// find the best solution
		Candidate gBest = population.get(0);
		for (int i = 1; i < populationSize; i++) {
			if (getFitness.apply(population.get(i)) < getFitness.apply(gBest)) {
				try {
					gBest = (Candidate) population.get(i).clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		return gBest;
	}
}
