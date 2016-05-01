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

public class ShapeArrayIndex  {
	
	private int shapeall_index=0;
	private int shape_index=0;
	
	public void setShapeArrayIndex(int shapeall_index,int shape_index){
		this.shapeall_index=shapeall_index;
		this.shape_index=shape_index;
	}
	
	public int getShapeall_index() {
		return shapeall_index;
	}
	public void setShapeall_index(int shapeall_index) {
		this.shapeall_index = shapeall_index;
	}
	public int getShape_index() {
		return shape_index;
	}
	public void setShape_index(int shape_index) {
		this.shape_index = shape_index;
	}
	
}
