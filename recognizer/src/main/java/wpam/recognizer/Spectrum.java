package wpam.recognizer;

import android.util.Log;

public class Spectrum {

	private double[] spectrum;
	private int length;
	
	/**
	 * 原始数据是??
	 * @param spectrum
	 */
	public Spectrum(double[] spectrum) 
	{
		this.spectrum = spectrum;
		this.length = spectrum.length;
	}
	
	/**
	 * 数据标准化,0-1
	 */
	public void normalize()
	{
		double maxValue = 0.0;
		
		for(int i=0;i<length; ++i)
			if(maxValue < spectrum[i])
				maxValue = spectrum[i];
		
		if(maxValue != 0)			
			for(int i=0;i<length; ++i) {
				spectrum[i] /= maxValue;
//				Log.d("test", String.format("i=%s,value=%s",new String[]{i+"",spectrum[i]+""}));
			}
//		System.exit(0);
	}
	
	public double get(int index)
	{
		return spectrum[index];
	}
	
	public int length() 
	{
		return length;
	}
}
