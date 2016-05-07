package wpam.recognizer;

import android.util.Log;

import wpam.recognizer.math.FFT;

public class DataBlock 
{
	private double[] block;

	/**
	 * 原始截取到的数据
	 * @param buffer
	 * @param blockSize
	 * @param bufferReadSize
	 */
	public DataBlock(short[] buffer, int blockSize, int bufferReadSize)
	{
		block = new double[blockSize];
		
		//600多个数据
		for (int i = 0; i < blockSize && i < bufferReadSize; i++) {
			block[i] = (double) buffer[i];
		}
	}
	
	public DataBlock()
	{
		
	}
	
	public void setBlock(double[] block) 
	{
		this.block = block;
	}
	
	public double[] getBlock() 
	{
		return block;
	}
	
	/**
	 * 数据转化
	 * @return
	 */
	public Spectrum FFT()
	{
		return new Spectrum(FFT.magnitudeSpectrum(block));
	}
}
