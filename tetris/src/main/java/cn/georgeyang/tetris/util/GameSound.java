package cn.georgeyang.tetris.util;


import java.util.HashMap;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import cn.georgeyang.tetris.R;

public class GameSound {
	public MediaPlayer mMediaPlayer;
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;
	private Context context;
	
	@SuppressLint("UseSparseArrays")
	public GameSound(Context context){
		this.context=context;
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPoolMap.put(1, soundPool.load(context, R.raw.effect, 1));
		soundPoolMap.put(2, soundPool.load(context, R.raw.bird, 1));
		
		
		playSound(2, 0);
	}
	
	public void playSound(int sound, int loop)
	{
		AudioManager mgr = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = mgr
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolumeCurrent / streamVolumeMax;
		soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
	}

}
