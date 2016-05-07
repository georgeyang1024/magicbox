package wpam.recognizer.math;

public class DFT {
	
	public static final int RECTANGULAR = 0;
	public static final int HANN = 1;
	public static final int HAMMING = 2;
	public static final int BLACKMANN = 3;
	
	public static final double[] forwardMagnitude(double[] input) {
		int N = input.length;
		double[] mag = new double[N];
		double[] c = new double[N];
		double[] s = new double[N];
		double twoPi = 2*Math.PI;
		
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				c[i] += input[j]*Math.cos(i*j*twoPi/N);
				s[i] -= input[j]*Math.sin(i*j*twoPi/N);
			}
			c[i]/=N;
			s[i]/=N;
			
			mag[i]=Math.sqrt(c[i]*c[i]+s[i]*s[i]);
		}
		
		return mag;
	}
	
	public static final double[] window(double[] input, int type) {
		int N = input.length;
		double[] windowed = new double[N];
		
		switch(type) {
		case RECTANGULAR:
			return input;
		case HANN:
			for(int n=0; n<N; n++) {
				windowed[n] = 0.5*(1-Math.cos(2*Math.PI*n/(N-1))) * input[n];
			}
			break;
		case HAMMING:
			for (int n = 0; n < input.length; n++) {
				windowed[n] = (0.53836-0.46164*Math.cos(Tools.TWO_PI*n/(N-1))) * input[n];
			}
		case BLACKMANN:
			for(int n=0; n<N; n++) {
				windowed[n] = (0.42-0.5*Math.cos(2*Math.PI*n/(N-1))+0.08*Math.cos(4*Math.PI*n/(N-1)) ) * input[n];
			}
			break;
		}
		
		return windowed;
	}
}
