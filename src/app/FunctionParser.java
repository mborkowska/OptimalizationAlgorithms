package app;

import javax.naming.spi.DirStateFactory.Result;

import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;

public class FunctionParser {
	public static void main(String[] args) {
		Jep jep = new Jep();
		Object result = 0;
		try {
			jep.addVariable("x", 1);
			jep.addVariable("y", 2);
			jep.parse("(x+y)^3");
			 result = jep.evaluate();
			 System.out.println("result = " + result);
		} catch (JepException e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
		double doubleResult = (double) result;
		System.out.println("doubleResult = " + doubleResult);
	}
}
	