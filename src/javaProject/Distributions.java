package javaProject;
import java.util.Random;


public class Distributions {
	
	public static int poisson(double LAMBDA) {
	    double L = Math.exp(-LAMBDA);
	    double p = 1.0;
	    int k = 0;
	    Random random = new Random();
	    
	    do {
	        k++;
	        p *= random.nextDouble();
	    } while (p > L);

	    return (int) (k - 1);
	}
	
	public static int exponential(double SERVICE_RATE) {
		Random random = new Random();
        return (int)(-Math.log(1.0 - random.nextDouble()) / SERVICE_RATE);
    }
}
