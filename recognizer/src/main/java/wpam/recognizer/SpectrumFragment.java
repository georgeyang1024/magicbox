package wpam.recognizer;

public class SpectrumFragment 
{
	private int start;
	private int end;
	private Spectrum spectrum;
	
	//wyznaczony metod� i�yniersk� mno�nik okre�laj�cy ile razy warto�� musi by� wi�ksza od �redniej, �eby uzna� j� za wa�n� 
	private static double DISTINCT_FACTOR = 2;

	public SpectrumFragment(int start, int end, Spectrum spectrum) 
	{
		
	
		this.start = start;
		this.end = end;
		this.spectrum = spectrum;
	}
	
	public double getAverage()
	{
		double sum = 0;
		
		for(int i = start; i <= end; ++i)
			sum+=spectrum.get(i);
		
		return sum/((double)(end - start));
	}
	
	public boolean[] getDistincts()
	{
		double average = getAverage();		
		
		boolean[] ret = new boolean[spectrum.length()];
		
		for (boolean b : ret)
			b = false;
		
		for(int i = start; i <= end; ++i)
		{
			if(spectrum.get(i) > (average * DISTINCT_FACTOR))
			{
				ret[i] = true;
			}
		}
		
		return ret;
	}
	
	/**
	 * 数值最大的索引
	 * @return
	 */
	public int getMax()
	{
		int max = 0;
		double maxValue = 0;
		
		for(int i = start; i <= end; ++i)
			if(maxValue < spectrum.get(i))
			{
				maxValue = spectrum.get(i);
				max = i;
			}
		
		return max;
	}
}
