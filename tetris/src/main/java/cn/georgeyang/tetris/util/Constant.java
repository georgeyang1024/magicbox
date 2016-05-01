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

public class Constant  {

	
	private int shapeAll_count=0;
	private List<Integer> shapes_count=new ArrayList<Integer>();
	
	private int rect_width;
	private int rect_height;
	 
	//private static Constant instance = null;
	
	private int MovePositon_x;
	private int MovePositon_y;
	
	private int nextShapeStart_x;  //绘制下一个形状的坐标初始坐标
	private int nextShapeStart_y;
	
	private int startPosition_x;
	private int startPosition_y;
	
	private int width_count=10;
	private int height_count=18;
	private static int unit_interval=2;
	private int width_lenght=0;
	private int height_lenght=0;
	
	private int nextshapewidth=0;
	
	public int getNextshapewidth() {
		return nextshapewidth;
	}

	public void setNextshapewidth(int nextshapewidth) {
		this.nextshapewidth = nextshapewidth;
	}


	private boolean unit_Status[][]=new boolean[width_count][height_count];

	private int moveunit_count=4;
	private boolean moveunit_Status[][]=new boolean[moveunit_count][moveunit_count];
	private boolean voidUnit[][]=new boolean[moveunit_count][moveunit_count];
	
	
	
	public void resetMovePosition(){
		MovePositon_y=-3;
		MovePositon_x=width_count/2-3;	
		moveunit_Status=voidUnit;

	}
	
	public int getNextShapeStart_x() {
		return nextShapeStart_x;
	}

	public void setNextShapeStart_x(int nextShapeStart_x) {
		this.nextShapeStart_x = nextShapeStart_x;
	}

	public int getNextShapeStart_y() {
		return nextShapeStart_y;
	}

	public void setNextShapeStart_y(int nextShapeStart_y) {
		this.nextShapeStart_y = nextShapeStart_y;
	}
	
	public int getMovePositon_x() {
		return MovePositon_x;
	}

	public void setMovePositon_x(int movePositon_x) {
		MovePositon_x = movePositon_x;
	}

	public int getMovePositon_y() {
		return MovePositon_y;
	}

	public void setMovePositon_y(int movePositon_y) {
		MovePositon_y = movePositon_y;
	}


	private List<List<boolean[][]>> ListMove_Data=new ArrayList<List<boolean[][]>>();
	
	public List<List<boolean[][]>> getListMove_Data() {
		return ListMove_Data;
	}

	public void setListMove_Data(List<List<boolean[][]>> listMove_Data) {
		ListMove_Data = listMove_Data;
	}



	public int getMoveunit_count() {
		return moveunit_count;
	}

	public void setMoveunit_count(int moveunit_count) {
		this.moveunit_count = moveunit_count;
	}

	
	
	public static int getUnit_interval() {
		return unit_interval;
	}

	public static void setUnit_interval(int unit_interval) {
		Constant.unit_interval = unit_interval;
	}
	

	public int getStartPosition_x() {
		return startPosition_x;
	}

	public void setStartPosition_x(int startPosition_x) {
		this.startPosition_x = startPosition_x;
	}

	public int getStartPosition_y() {
		return startPosition_y;
	}

	public void setStartPosition_y(int startPosition_y) {
		this.startPosition_y = startPosition_y;
	}

	//初始化
	public Constant(Context context){
		initMove_Data();
		for(int i=0;i<width_count;i++){
			for(int j=0;j<height_count;j++){
				unit_Status[i][j]=false;
			}
		}
		for(int i=0;i<moveunit_count;i++){
			for(int j=0;j<moveunit_count;j++){
				moveunit_Status[i][j]=false;
				voidUnit[i][j]=false;			
			}
		}
		 WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		 Display display = manager.getDefaultDisplay();
		 int width =display.getWidth();
		 int height=display.getHeight();	 
		 width_lenght=(width/3*2)/width_count;
		 height_lenght=width_lenght;	
		
		 Rect frame = new Rect();
//		 ((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//		int contentTop = ((Activity)context). getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

		rect_width=width_lenght*width_count;
		rect_height=height_lenght*height_count;
				
		startPosition_x=10;
		//startPosition_y=height-height_lenght*height_count-160;
		startPosition_y=200;
		nextshapewidth=width_lenght/2;
		nextShapeStart_x=startPosition_x+rect_width+nextshapewidth;
		nextShapeStart_y=startPosition_y;
		
		resetMovePosition();
	}
	
	public boolean[][] getMoveunit_Status() {
		return moveunit_Status;
	}

	public void setMoveunit_Status(boolean[][] moveunit_Status) {
		this.moveunit_Status = moveunit_Status;
	}

	public int getRect_width() {
		return rect_width;
	}

	public void setRect_width(int rect_width) {
		this.rect_width = rect_width;
	}

	public int getRect_height() {
		return rect_height;
	}

	public void setRect_height(int rect_height) {
		this.rect_height = rect_height;
	}

	public int getWidth_count() {
		return width_count;
	}

	public void setWidth_count(int width_count) {
		this.width_count = width_count;
	}

	public int getHeight_count() {
		return height_count;
	}

	public void setHeight_count(int height_count) {
		this.height_count = height_count;
	}

	public int getWidth_lenght() {
		return width_lenght;
	}

	public void setWidth_lenght(int width_lenght) {
		this.width_lenght = width_lenght;
	}

	public int getHeight_lenght() {
		return height_lenght;
	}

	public void setHeight_lenght(int height_lenght) {
		this.height_lenght = height_lenght;
	}

	public boolean[][] getUnit_Status() {
		return unit_Status;
	}

	public void setUnit_Status(boolean[][] unit_Status) {
		this.unit_Status = unit_Status;
	}

/*	public static Constant getInstance(Context context) 
	{
		if (instance == null) 
		{
			instance = new Constant( context);
			return instance;
		}
			return instance;
	}*/

	
	private void initMove_Data(){
		List<boolean[][]> dataList0=new ArrayList<boolean[][]>();
		/*
		 *     MM
		 *     MM
		 * */
		boolean data[][]={
			{false,false,false,false},
			{false,false,false,false},
			{false,false,true,true},
			{false,false,true,true}
		};
		dataList0.add(data);
		
		List<boolean[][]> dataList1=new ArrayList<boolean[][]>();
		/*
		 *   MM  
		 *    MM 
		 * */
		boolean data10[][]={
			{false,false,false,false},
			{false,false,false,true},
			{false,false,true,true},
			{false,false,true,false}
		};
		/*   
		 *     M  
		 *    MM
		 *    M 
		 * */
		boolean data11[][]={
			{false,false,false,false},
			{false,false,false,false},
			{false,true,true,false},
			{false,false,true,true}
		};
		dataList1.add(data10);
		dataList1.add(data11);
		
		List<boolean[][]> dataList2=new ArrayList<boolean[][]>();
		/*
		 *   MM  
		 *    M
		 *    M
		 * */
		boolean data20[][]={
			{false,false,false,false},
			{false,false,false,false},
			{false,true,true,true},
			{false,true,false,false}
		};
		/*   
		 *      M  
		 *    MMM    
		 * */
		boolean data21[][]={
			{false,false,false,false},
			{false,false,true,true},
			{false,false,false,true},
			{false,false,false,true}
		};
		/*   
		 *    M
		 *    M  
		 *    MM   
		 * */
		boolean data22[][]={
			{false,false,false,false},
			{false,false,false,false},
			{false,false,false,true},
			{false,true,true,true}
		};
		/*   
		 *    MMM
		 *    M    
		 * */
		boolean data23[][]={
			{false,false,false,false},
			{false,false,true,false},
			{false,false,true,false},
			{false,false,true,true}
		};
		dataList2.add(data20);
		dataList2.add(data21);
		dataList2.add(data22);
		dataList2.add(data23);
		
		List<boolean[][]> dataList3=new ArrayList<boolean[][]>();
		/*
		 *    M  
		 *    MM
		 *    M
		 * */
		boolean data30[][]={
			{false,false,false,false},
			{false,false,false,false},
			{false,false,true,false},
			{false,true,true,true}
		};
		/*   
		 *     M  
		 *    MMM    
		 * */
		boolean data31[][]={
			{false,false,false,false},
			{false,false,false,true},
			{false,false,true,true},
			{false,false,false,true}
		};
		/*   
		 *    M
		 *   MM  
		 *    M   
		 * */
		boolean data32[][]={
			{false,false,false,false},
			{false,false,false,false},
			{false,true,true,true},
			{false,false,true,false}
		};
		/*   
		 *    MMM
		 *     M    
		 * */
		boolean data33[][]={
			{false,false,false,false},
			{false,false,true,false},
			{false,false,true,true},
			{false,false,true,false}

		};
		dataList3.add(data30);
		dataList3.add(data31);
		dataList3.add(data32);
		dataList3.add(data33);
		
		List<boolean[][]> dataList4=new ArrayList<boolean[][]>();
		/*
		 *   MMMM   
		 *   
		 * */
		boolean data40[][]={
			{false,false,false,true},
			{false,false,false,true},
			{false,false,false,true},
			{false,false,false,true}	
		};
		/*   
		 *    M
		 *    M  
		 *    M
		 *    M 
		 * */
		boolean data41[][]={
			{false,false,false,false},
			{false,false,false,false},
			{true,true,true,true},
			{false,false,false,false},
		};
		dataList4.add(data40);
		dataList4.add(data41);
		
		
		List<boolean[][]> dataList5=new ArrayList<boolean[][]>();
		/*
		 *    MM  
		 *    M
		 *    M
		 * */
		boolean data50[][]={
			{false,false,false,false},
			{false,false,false,false},
			{false,true,false,false},
			{false,true,true,true}
		};
		/*   
		 *    M  
		 *    MMM    
		 * */
		boolean data51[][]={
			{false,false,false,false},
			{false,false,false,true},
			{false,false,false,true},
			{false,false,true,true}
		};
		/*   
		 *    M
		 *    M  
		 *   MM   
		 * */
		boolean data52[][]={
			{false,false,false,false},
			{false,false,false,false},
			{false,true,true,true},
			{false,false,false,true}
		};
		/*   
		 *    MMM
		 *      M    
		 * */
		boolean data53[][]={
			{false,false,false,false},
			{false,false,true,true},
			{false,false,true,false},
			{false,false,true,false}
		};
		dataList5.add(data50);
		dataList5.add(data51);
		dataList5.add(data52);
		dataList5.add(data53);
		
		List<boolean[][]> dataList6=new ArrayList<boolean[][]>();
		/*
		 *     MM  
		 *    MM 
		 * */
		boolean data60[][]={
			{false,false,false,false},
			{false,false,true,false},
			{false,false,true,true},
			{false,false,false,true}
		};
		/*   
		 *    M  
		 *    MM
		 *     M 
		 * */
		boolean data61[][]={
			{false,false,false,false},
			{false,false,false,false},
			{false,false,true,true},
			{false,true,true,false}
		};
		dataList6.add(data60);
		dataList6.add(data61);
		
		
		ListMove_Data.add(dataList0);	
		ListMove_Data.add(dataList1);
		ListMove_Data.add(dataList2);
		ListMove_Data.add(dataList3);
		ListMove_Data.add(dataList4);
		ListMove_Data.add(dataList5);
		ListMove_Data.add(dataList6);
		
		shapeAll_count=ListMove_Data.size();
		for(int i=0;i<shapeAll_count;i++){
			int count=ListMove_Data.get(i).size();
			shapes_count.add(count);
		}
		
	}

	public int getShapeAll_count() {
		return shapeAll_count;
	}

	public void setShapeAll_count(int shapeAll_count) {
		this.shapeAll_count = shapeAll_count;
	}

	public List<Integer> getShapes_count() {
		return shapes_count;
	}

	public void setShapes_count(List<Integer> shapes_count) {
		this.shapes_count = shapes_count;
	}
	
}
