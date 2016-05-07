package wpam.recognizer.math;

public class Tools {
	
	public static final double TWO_PI = 2*Math.PI;
	
	public static final double LOG_OF_2_BASE_10 = 1/Math.log10(2);
	
	public static double log2(double x) {
		return Math.log10(x)/Math.log10(2.0);
	}
	
	public static final double[] lowpass(double[] signal, int nPoints) {
		int N = signal.length;
		double[] ret = new double[N];
		
		for(int i=0; i<nPoints/2; i++) {
			ret[i] = signal[i];
		}
		for(int i=nPoints/2; i<N-nPoints/2; i++) {
			for(int j=0; j<nPoints; j++) {
				ret[i]=0;
				ret[i]+=signal[i-nPoints/2+j];
				ret[i]/=nPoints;
			}	
		}
		for(int i=N-nPoints/2; i<N; i++) {
			ret[i]=signal[i];
		}
		
		return ret;
	}
	
	public static final double[] addArrays(double[] x, double[] y) {
		double[] sum = new double[x.length];
		
		for(int i=0; i<x.length; i++) {
			sum[i] = x[i] + y[i];
		}
		
		return sum;
	}
	
	public static final Complex[] addArrays(Complex[] x, Complex[] y) {
		Complex[] sum = new Complex[x.length];
		
		for(int i=0; i<x.length; i++) {
			sum[i] = Complex.add(x[i], y[i]);
		}
		
		return sum;
	}
	
	public static final Complex[] substractArrays(Complex[] x, Complex[] y) {
		Complex[] sum = new Complex[x.length];
		
		for(int i=0; i<x.length; i++) {
			sum[i] = Complex.substract(x[i], y[i]);
		}
		
		return sum;
	}
	
	public static final double[] dotProduct(double[] x, double[] y) {
		double[] sum = new double[x.length];
		
		for(int i=0; i<x.length; i++) {
			sum[i] = x[i] * y[i];
		}
		
		return sum;
	}
	
	public static final Complex[] dotProduct(Complex[] x, Complex[] y) {
		Complex[] sum = new Complex[x.length];
		
		for(int i=0; i<x.length; i++) {
			sum[i] = Complex.multiply(x[i], y[i]);
		}
		
		return sum;
	}
	
	public static Complex[] makeComplex(double[] x) {
		int N = x.length;
		Complex[] c = new Complex[N];
		for(int i=0; i<N; i++) {
			c[i] = new Complex(x[i],0);
		}
		return c;
	}
	
	public static void printArray(double[] arr) {
		for (double d : arr) {
			System.out.format("%.4f ", d);
		}
		System.out.println();
	}
}
