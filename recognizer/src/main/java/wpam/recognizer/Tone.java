package wpam.recognizer;

public class Tone {
	
	private int lowFrequency;
	private int highFrequency;
	
	private char key;
	
	private static int FREQUENCY_DELTA = 2;
	
	/**
	 *  
	 * @param lowFrequency  低频 30-300
	 * @param highFrequency 高频
	 * @param key
	 */
	public Tone(int lowFrequency, int highFrequency, char key) 
	{
		this.lowFrequency = lowFrequency;
		this.highFrequency = highFrequency;
		this.key = key;
	}

	public int getLowFrequency()
	{
		return lowFrequency;
	}

	public int getHighFrequency()
	{
		return highFrequency;
	}

	public char getKey()
	{
		return key;
	}

	public boolean isDistrinct(boolean[] distincts) 
	{
		if(match(lowFrequency, distincts) && match(highFrequency,distincts))
			return true;
		
		return false;
	}

	
	private boolean match(int frequency, boolean[] distincts) 
	{
		for(int i = frequency - FREQUENCY_DELTA; i <= frequency + FREQUENCY_DELTA; ++i)
			if(distincts[i])
				return true;
		
		return false;
	}

	public boolean match(int lowFrequency, int highFrequency) 
	{
		if(matchFrequency(lowFrequency, this.lowFrequency) && matchFrequency(highFrequency, this.highFrequency))
			return true;
		
		return false;
	}
	
	private boolean matchFrequency(int frequency, int frequencyPattern)
	{
		if((frequency - frequencyPattern) * (frequency - frequencyPattern) < FREQUENCY_DELTA * FREQUENCY_DELTA)
			return true;
		
		return false;
	}

}
