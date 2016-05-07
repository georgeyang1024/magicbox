package wpam.recognizer.math;

import android.util.Log;

/*
 * http://www.cs.princeton.edu/introcs/97data/FFT.java.html
 * Should be optimized. w_n may be looked up from a table etc.
 * 
 * Java DSP book
 */
public class FFT {
	
//	private static int length;
	
	private static double[] r_data = null;
	private static double[] i_data = null;
	private static boolean forward = true;
	
	/*	  
	 private void computeRootArray(int N) {
		Complex[] w = new Complex[N/2];
		
		for(int i=0; i<N/2; i++) {
			w[i] = Complex.nthRootOfUnity(i, N);
		}
	}
	*/
	
//	public static Complex[] forward(Complex[] x) {
//		int N = x.length;		
//		
//		if( N == 1 ) {
//			return new Complex[] { x[0] };
//		} else {			
//			Complex[] even = new Complex[N/2];
//			Complex[] odd = new Complex[N/2];
//			
//			for(int i=0; i<N/2; i++) {
//				even[i]=x[2*i];
//				odd[i]=x[2*i+1];
//			}
//			
//			Complex[] left = forward(even);
//			Complex[] right = forward(odd);
//			
//			Complex[] c = new Complex[N];
//			for(int n=0; n<N/2; n++) {
//				double nth = -2*n*Math.PI/N;
//				Complex wn = new Complex(Math.cos(nth), Math.sin(nth));
//				c[n] = Complex.add(left[n], Complex.multiply(wn, right[n]));
//				c[n+N/2] = Complex.substract(left[n], Complex.multiply(wn, right[n]));				
//			}
//			return c;			
//		}
//	}
	
//	public static int bitReverse(int index) {
//		// if length = 8 index goes from 0 to 7
//		// to write 8 we need log2(8)+1=3+1=4 bits.
//		// 8 = 1000, 7 = 111
//		// so to write 7 we need log2(8)=3 bits.
//		int numBits = (int)Tools.log2(8);		
//		int ret = 0;
//		
//		for (int i = 0; i < numBits; i++) {
//			ret = (ret<<1) + (index&1);
//			index = index>>1;			
//		}
//		return ret;
//	}	
	
	/**
	 * (转化)振幅谱线(600个数据变成了1024个数据)
	 * @param realPart
	 * @return
	 */
	public static double[] magnitudeSpectrum(double[] realPart) {
//		length = realPart.length;
//		int localN;
//		
//		int numBits = (int)Tools.log2(length);
//		
//		for(int m = 1; m <= numBits; m++) {
//			// localN = 2^m;
//			localN = 1<<m;
//		}
		double[] imaginaryPart = new double[realPart.length];
		
		for (int i = 0; i < imaginaryPart.length; i++) {
			imaginaryPart[i] = 0;
		}
		forwardFFT(realPart, imaginaryPart);
		
		for (int i = 0; i < realPart.length; i++) {
			realPart[i] = Math.sqrt( r_data[i]*r_data[i] + i_data[i]*i_data[i] );
//			Log.d("test", String.format("i=%s,value=%s",new String[]{i+"",realPart[i]+""}));
		}
		
		return realPart;
	}
	
//	 swap Zi with Zj
    private static void swapInt(int i, int j) {
        double tempr;
        int ti;
        int tj;
        ti = i - 1;
        tj = j - 1;
        tempr = r_data[tj];
        r_data[tj] = r_data[ti];
        r_data[ti] = tempr;
        tempr = i_data[tj];
        i_data[tj] = i_data[ti];
        i_data[ti] = tempr;
    }
	
    private static void bitReverse2() {
//        System.out.println("fft: bit reversal");
        /* bit reversal */
        int n = r_data.length;
        int j = 1;

        int k;

        for (int i = 1; i < n; i++) {

            if (i < j) swapInt(i, j);
            k = n / 2;
            while (k >= 1 && k < j) {

                j = j - k;
                k = k / 2;
            }
            j = j + k;
        }
    }
	
    /**
     * 离散傅氏变换的快速算法
     * @param in_r 输入的数据
     * @param in_i 待输出的数据
     */
    public static void forwardFFT(double in_r[], double in_i[]) {
        int id;

        int localN;
        double wtemp, Wjk_r, Wjk_i, Wj_r, Wj_i;
        double theta, tempr, tempi;
//        int ti, tj;

        int numBits = (int)Tools.log2(in_r.length);
        if (forward) {
            //centering(in_r);
        }


        // Truncate input data to a power of two
        int length = 1 << numBits; // length = 2**nu
        int n = length;
        int nby2;

        // Copy passed references to variables to be used within
        // fft routines & utilities
        r_data = in_r;
        i_data = in_i;

        bitReverse2();
        for (int m = 1; m <= numBits; m++) {
            // localN = 2^m;
            localN = 1 << m;

            nby2 = localN / 2;
            Wjk_r = 1;
            Wjk_i = 0;

            theta = Math.PI / nby2;

            // for recursive comptutation of sine and cosine
            Wj_r = Math.cos(theta);
            Wj_i = -Math.sin(theta);
            if (forward == false) {
                Wj_i = -Wj_i;
            }


            for (int j = 0; j < nby2; j++) {
                // This is the FFT innermost loop
                // Any optimizations that can be made here will yield
                // great rewards.
                for (int k = j; k < n; k += localN) {
                    id = k + nby2;
                    tempr = Wjk_r * r_data[id] - Wjk_i * i_data[id];
                    tempi = Wjk_r * i_data[id] + Wjk_i * r_data[id];

                    // Zid = Zi -C
                    r_data[id] = r_data[k] - tempr;
                    i_data[id] = i_data[k] - tempi;
                    r_data[k] += tempr;
                    i_data[k] += tempi;
                }

                // (eq 6.23) and (eq 6.24)

                wtemp = Wjk_r;

                Wjk_r = Wj_r * Wjk_r - Wj_i * Wjk_i;
                Wjk_i = Wj_r * Wjk_i + Wj_i * wtemp;
            }
        }
        // normalize output of fft.
//        if (forward)
        if(false)
            for (int i = 0; i < r_data.length; i++) {
                r_data[i] = r_data[i] / (double) n;
                i_data[i] = i_data[i] / (double) n;
            }
        in_r = r_data;
        in_r = i_data;
    }
	
//	public static Complex[] inverse(Complex[] c) {
//		int N = c.length;
//		Complex[] x = new Complex[N];
//		
//		for(int i=0; i<N; i++) {
//			x[i] = Complex.conjugate(c[i]);
//		}
//		
//		x = forward(x);
//		
//		for(int i=0; i<N; i++) {
//			x[i] = Complex.conjugate(x[i]);
//			x[i] = Complex.scale(x[i], 1.0/N);
//		}
//		
//		return x;		
//	}
}
