package wpam.recognizer;

import java.util.ArrayList;
import java.util.Collection;

public class StatelessRecognizer {

	private Spectrum spectrum;
	private Collection<Tone> tones;

	public StatelessRecognizer(Spectrum spectrum) 
	{
		this.spectrum = spectrum;
		
//		for (int i = 0; i < spectrum.length(); i++) {
//			Log.i("test", "spectrum=" + spectrum.get(i));
//		}
//		System.exit(0);
		
		tones = new ArrayList<Tone>();
		
		fillTones();
	}
	
	private void fillTones() {
		//低频(30-300赫兹)
		tones.add(new Tone(44, 77, '1'));
		tones.add(new Tone(45, 77, '1'));
		tones.add(new Tone(44, 78, '1'));
		tones.add(new Tone(45, 78, '1'));
		
		tones.add(new Tone(44, 85, '2'));
		tones.add(new Tone(45, 85, '2'));	
		tones.add(new Tone(44, 86, '2'));
		tones.add(new Tone(45, 86, '2'));	
		
		tones.add(new Tone(44, 95, '3'));	
		tones.add(new Tone(45, 95, '3'));	
		tones.add(new Tone(44, 94, '3'));	
		tones.add(new Tone(45, 94, '3'));	
		
		tones.add(new Tone(49, 77, '4'));	
		tones.add(new Tone(50, 77, '4'));	
		tones.add(new Tone(49, 78, '4'));	
		tones.add(new Tone(50, 78, '4'));	
		
		tones.add(new Tone(49, 85, '5'));	
		tones.add(new Tone(50, 85, '5'));
		tones.add(new Tone(49, 86, '5'));	
		tones.add(new Tone(50, 86, '5'));	
		
		tones.add(new Tone(49, 95, '6'));		
		tones.add(new Tone(50, 95, '6'));	
		tones.add(new Tone(49, 94, '6'));		
		tones.add(new Tone(50, 94, '6'));	
		
		tones.add(new Tone(54, 77, '7'));		
		tones.add(new Tone(55, 77, '7'));	
		tones.add(new Tone(54, 78, '7'));		
		tones.add(new Tone(55, 78, '7'));
		
		tones.add(new Tone(54, 85, '8'));		
		tones.add(new Tone(55, 85, '8'));	
		tones.add(new Tone(54, 86, '8'));		
		tones.add(new Tone(55, 86, '8'));	
		
		tones.add(new Tone(54, 95, '9'));		
		tones.add(new Tone(55, 95, '9'));	
		tones.add(new Tone(54, 94, '9'));		
		tones.add(new Tone(55, 94, '9'));	
		
		tones.add(new Tone(60, 77, '*'));	
		tones.add(new Tone(61, 77, '*'));	
		tones.add(new Tone(60, 78, '*'));	
		tones.add(new Tone(61, 78, '*'));	
		
		tones.add(new Tone(60, 85, '0'));	
		tones.add(new Tone(61, 85, '0'));	
		tones.add(new Tone(60, 86, '0'));	
		tones.add(new Tone(61, 86, '0'));	
		
		tones.add(new Tone(60, 95, '#'));	
		tones.add(new Tone(61, 95, '#'));	
		tones.add(new Tone(60, 94, '#'));	
		tones.add(new Tone(61, 94, '#'));
	}
	

	public char getRecognizedKey()
	{
		SpectrumFragment lowFragment= new SpectrumFragment(0, 75, spectrum);
		SpectrumFragment highFragment= new SpectrumFragment(75, 150, spectrum);
		
		int lowMax = lowFragment.getMax();
		int highMax = highFragment.getMax();
		
//		Log.i("test", "lowMax=" + lowMax);
//		Log.i("test", "highMax=" + highMax);
//		System.exit(0);
		
		SpectrumFragment allSpectrum = new SpectrumFragment(0, 150, spectrum);
		int max = allSpectrum.getMax();
		
		if(max != lowMax && max != highMax)
			return ' ';
		
		for (Tone t : tones) {
			if(t.match(lowMax, highMax))
				return t.getKey();
		}
		
		return ' ';
	}
}
