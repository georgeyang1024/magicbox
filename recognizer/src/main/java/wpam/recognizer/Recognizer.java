package wpam.recognizer;

import java.util.ArrayList;

public class Recognizer 
{
	ArrayList<Character> history;
	char acctualVaue;
	
	public Recognizer() 
	{
		clear();
	}

	private void clear() 
	{
		history = new ArrayList<Character>();
		acctualVaue = ' ';
	}

	public char getRecognizedKey(char recognizedKey) 
	{
		history.add(recognizedKey);
		
		if(history.size() <= 4)
			return ' ';
		
		history.remove(0);
		
		int count = 0;
		
		for (Character c: history) 
		{
			if(c.equals(recognizedKey))
				count++;
		}
		
		if(count >= 3)
			acctualVaue = recognizedKey;
		
		return acctualVaue;
	}
}
