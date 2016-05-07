package wpam.recognizer.math;

public class Complex {
	
	private double real;
	private double imag;
	
	public Complex(double real, double imag) {
		this.real = real;
		this.imag = imag;
	}
	
	public String toString() {
		if(imag<0) {
			return new String(real+" - i"+Math.abs(imag));
		} else {
			return new String(real+" + i"+imag);
		}
	}
	
	public static final Complex multiply(Complex c1, Complex c2) {
		double re = c1.real*c2.real - c1.imag*c2.imag;
		double im = c1.real*c2.imag + c1.imag*c2.real;
		return new Complex(re, im);
	}
	
	public static Complex scale(Complex c, double x) {
		return new Complex(c.real*x, c.imag*x);
	}
	
	public static final Complex add(Complex c1, Complex c2) {
		double re = c1.real + c2.real;
		double im = c1.imag + c2.imag;
		return new Complex(re, im);
	}
	
	public static final Complex substract(Complex c1, Complex c2) {
		double re = c1.real - c2.real;
		double im = c1.imag - c2.imag;
		return new Complex(re, im);
	}
	
	public static Complex conjugate(Complex c) {
		return new Complex(c.real, -c.imag);
	}
	
	public static double abs(Complex c) {
		return Math.sqrt(c.real*c.real+c.imag*c.imag);
	}
	
	public static double[] abs(Complex[] c) {
		int N = c.length;
		double[] mag = new double[N];
		for(int i=0; i<N; i++) {
			mag[i] = Math.sqrt(c[i].real*c[i].real+c[i].imag*c[i].imag);
		}
		return mag;
	}
	
	public static final Complex nthRootOfUnity(int n, int N) {
		double re = Math.cos(2*Math.PI*n/N);
		double im = Math.sin(2*Math.PI*n/N);
		return new Complex(re, im); 
	}
}
