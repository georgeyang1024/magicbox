package cn.georgeyang.tetris.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class GameData  {
	int leve=0;
	boolean islose=false;
	
	public boolean getIslose() {
		return islose;
	}

	public void setIslose(boolean islose) {
		this.islose = islose;
	}

	public int getLeve() {
		return leve;
	}

	public void setLeve(int leve) {
		this.leve = leve;
	}

	private static GameData instance;

	public static GameData getInstance() 
	{
		if (instance == null) 
		{
			instance = new GameData();
			return instance;
		}
		return instance;
	}
}
