package pso;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class ParticleSwarmOptimalization {
	int swarmSize = 400;
	int iterationNo = 400;
	double C1 = 2.0;
	double C2 = 2.0;
	double W_UP = 1.0;
	double W_LO = 0.0;
	int t = 0;
	double w;
	Random generator = new Random();
	int dimensionNo;
	List<Particle> swarm;
	List<double[]> dimensionList;
	double gBest;
	double[] gBestPosition;
	Function<Particle, Double> getFitness;

	public ParticleSwarmOptimalization(Function<Particle, Double> function, List<double[]> dimensionList) {
		getFitness = function;
		dimensionNo = dimensionList.size();
		swarm = new LinkedList<>();
		this.dimensionList = dimensionList;
		gBestPosition = new double[dimensionNo];
		for (int i = 0; i < swarmSize; i++) {
			Particle particle = new Particle(dimensionList);
			swarm.add(particle);
		}
		//Particle best = optimize();
		//System.out.println("PSO: gBest: " + getFitness.apply(best) + " at " + best.toString());
	}

	public Particle optimize() {
		Particle bestParticle = null;
		while (t < iterationNo) {
			// calculate corresponding f(i,t) corresponding to location x(i,t)
			calculateAllFitness();

			// update pBest
			if (t == 0) {
				for (int i = 0; i < swarmSize; i++) {
					swarm.get(i).pBest = swarm.get(i).fitness;
					swarm.get(i).pBestPosition = swarm.get(i).position;
				}
			} else {
				for (int i = 0; i < swarmSize; i++) {
					if (swarm.get(i).fitness < swarm.get(i).pBest) { // looking for the function minimum
						swarm.get(i).pBest = swarm.get(i).fitness;
						swarm.get(i).pBestPosition = swarm.get(i).position;
					}
				}
			}
			// -------------------------------------------------------------
			int bestIndex = getBestParticle();
			// update gBest
			if (t == 0 || swarm.get(bestIndex).fitness < gBest) {
				gBest = swarm.get(bestIndex).fitness;
				gBestPosition = swarm.get(bestIndex).position;
				bestParticle = swarm.get(bestIndex);
			}
			// -------------------------------------------------------------
			w = W_UP - (((double) t) / iterationNo) * (W_UP - W_LO);

			for (int i = 0; i < swarmSize; i++) {
				double r1 = generator.nextDouble();
				double r2 = generator.nextDouble();
				Particle currentParticle = swarm.get(i);
				double[] p = currentParticle.position;
				double[] v = currentParticle.velocity;
				double[] pBestPos = currentParticle.pBestPosition;
				double[] gBestPos = gBestPosition;
				double[] newVel = new double[dimensionNo];
				double[] newPos = new double[dimensionNo];
				
				for (int j = 0; j < dimensionNo; j++) {
					newVel[j] = (w * v[j]) + (r1 * C1) * (pBestPos[j] - p[j]) + (r2 * C2) * (gBestPos[j] - p[j]);
					newPos[j] = p[j] + newVel[j];
				}
				currentParticle.position = newPos; 
				currentParticle.velocity = newVel;
			}
			t++;
		}
		return bestParticle;
	}

	private void calculateAllFitness() {
		for (int i = 0; i < swarmSize; i++) {
			swarm.get(i).fitness = getFitness.apply(swarm.get(i));
		}
	}

	private int getBestParticle() {
		int best = 0;
		for (int i = 1; i < swarmSize; i++) {
			if (swarm.get(i).fitness < swarm.get(best).fitness) {
				best = i;
			}
		}
		return best;
	}
}
