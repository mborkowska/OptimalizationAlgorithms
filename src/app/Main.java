package app;

import java.util.LinkedList;
import java.util.List;

import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;

import de.Candidate;
import de.DifferentialEvolution;
import pso.Particle;
import pso.ParticleSwarmOptimalization;

public class Main {
	static String formula;
	static Jep jep = new Jep();

	public static void main(String[] args) {

		// formula = "(x+2*y-7)^2+(2*x+y-5)^2";
		formula = "100(y-x^2)^2+(1-x)^2";
		try {
			jep.addVariable("x", 0);
			jep.addVariable("y", 0);
			jep.parse(formula);
		} catch (JepException e) {
			e.printStackTrace();
		}

		// DE

		double[] DEdimension1Bounds = new double[2];
		DEdimension1Bounds[0] = -50;
		DEdimension1Bounds[1] = 50;

		double[] DEdimension2Bounds = new double[2];
		DEdimension2Bounds[0] = -50;
		DEdimension2Bounds[1] = 50;

		List<double[]> DEdimensionList = new LinkedList<>();
		DEdimensionList.add(DEdimension1Bounds);
		DEdimensionList.add(DEdimension2Bounds);
		new DifferentialEvolution((Candidate candidate) -> fitFunctionGetFormula(candidate), DEdimensionList);

		// PSO
		/*
		 * double[] PSOdimension1Bounds = new double[4]; PSOdimension1Bounds[0] = -50;
		 * PSOdimension1Bounds[1] = 50; PSOdimension1Bounds[2] = -100;
		 * PSOdimension1Bounds[3] = 100;
		 * 
		 * double[] PSOdimension2Bounds = new double[4]; PSOdimension2Bounds[0] = -50;
		 * PSOdimension2Bounds[1] = 50; PSOdimension2Bounds[2] = -100;
		 * PSOdimension2Bounds[3] = 100;
		 * 
		 * List<double[]> PSOdimensionList = new LinkedList<>();
		 * PSOdimensionList.add(PSOdimension1Bounds);
		 * PSOdimensionList.add(PSOdimension2Bounds); new
		 * ParticleSwarmOptimalization((Particle particle) ->
		 * fitFunctionGetFormula(particle), PSOdimensionList);
		 */
	}

	public static double fitFunctionGetFormula(Solution solution) {
		double x;
		double y;
		Object f;
		if (solution instanceof Candidate) {
			Candidate candidate = (Candidate) solution;
			x = candidate.variables[0];
			y = candidate.variables[1];
		} else if (solution instanceof Particle) {
			Particle particle = (Particle) solution;
			x = particle.position[0];
			y = particle.position[1];
		} else {
			x = 0;
			y = 0;
			System.out.println("Sht went wrong");
		}

		try {
			jep.getVariableTable().getVariable("x").setValue(x);
			jep.getVariableTable().getVariable("y").setValue(y);
			f = jep.evaluate();
		} catch (JepException e) {
			System.out.println("An error occurred: " + e.getMessage());
			return 0;
		}
		double result = (double) f;
		return result;
	}

	public static double fitFunction(Solution solution) {
		double x;
		double y;
		Object f;
		if (solution instanceof Candidate) {
			Candidate candidate = (Candidate) solution;
			x = candidate.variables[0];
			y = candidate.variables[1];
		} else if (solution instanceof Particle) {
			Particle particle = (Particle) solution;
			x = particle.position[0];
			y = particle.position[1];
		} else {
			x = 0;
			y = 0;
			System.out.println("Sht went wrong");
		}
		//return Math.pow((1 - x), 2) + 100 * Math.pow((y - Math.pow(x, 2)), 2); "(x+2*y-7)^2+(2*x+y-5)^2";
		return Math.pow(x + 2 * y - 7, 2) + Math.pow(2 * x + y - 5, 2);
	}

	public static double fitFunctionMultiDim(Solution solution) {
		double value = 0;
		if (solution instanceof Candidate) {
			Candidate candidate = (Candidate) solution;
			for (int i = 0; i < candidate.variables.length - 1; i++) {
				// value = value + Math.pow(aCandidate.dataValue[i], 2.0) - 10.0 * Math.cos(2 *
				// Math.PI * aCandidate.dataValue[i]);
				value = value + Math.pow((1 - candidate.variables[i]), 2)
						+ 100 * Math.pow((candidate.variables[i + 1] - Math.pow(candidate.variables[i], 2)), 2);
			}
		}
		if (solution instanceof Particle) {
			Particle particle = (Particle) solution;
			for (int i = 0; i < particle.position.length - 1; i++) {
				// value = value + Math.pow(aCandidate.dataValue[i], 2.0) - 10.0 * Math.cos(2 *
				// Math.PI * aCandidate.dataValue[i]);
				value = value + Math.pow((1 - particle.position[i]), 2)
						+ 100 * Math.pow((particle.position[i + 1] - Math.pow(particle.position[i], 2)), 2);
			}
		}
		return value;
	}
}