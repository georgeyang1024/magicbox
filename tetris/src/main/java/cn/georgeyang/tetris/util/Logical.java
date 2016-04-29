package cn.georgeyang.tetris.util;

import java.util.List;



import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class Logical  {
	boolean isLoadMoveDatafirsttime=true;
	//private int shapeall_index=0;
	//private int shape_index=0;
	private Score score=new Score();
	
	public ShapeArrayIndex shapeArrayIndex_Now=new ShapeArrayIndex();
	public ShapeArrayIndex ShapeArrayIndex_Next=new ShapeArrayIndex();
	
	private boolean isNewmoveStart=true;
	private List<List<boolean[][]>> ListMove_Data;
	
	private int width_count;
	private int height_count;
	
	private boolean unit_Status[][];	
	private int moveunit_count;
	private boolean moveunit_Status[][];
	
	public Constant constant;
	//private static Logical instance;
	
	
	public Logical(Context context){	
		constant=new Constant(context);
		moveunit_Status=constant.getMoveunit_Status();
		unit_Status=constant.getUnit_Status();
		moveunit_count=constant.getMoveunit_count();
		width_count=constant.getWidth_count();
		height_count=constant.getHeight_count();
		ListMove_Data=constant.getListMove_Data();
	}
	
	//删除行
	public void rowDelete(){
		int deleteCount=0;
		int deletescore=0;
		for(int i=0;i<height_count;i++){
			int count=0;
			for(int j=0;j<width_count;j++){
				if(unit_Status[j][i]==true){
					count++;
				}
			}
			//Log.d(count+"count",width_count+"width_count");
			if(count==width_count){
				//Log.d("delete","delete");
				deleteCount++;
				for(int m=i;m>0;m--){
					for(int n=0;n<width_count;n++){
						unit_Status[n][m]=unit_Status[n][m-1];
					}	
				}			
			}	
		}
		int scoreNum=score.getScoreNum();
		for(int k=0;k<deleteCount;k++){
			deletescore=3*deletescore+1;
		}
		score.setScoreNum(scoreNum+deletescore*10);	
		if(deleteCount>0){
			OnLoadscore.OnLoadscores(scoreNum+deletescore*10);
		}
	}
	
	
	//定时下降
	public void Down_ontime(){
		//isNewmoveStart_temp记录下 是否为新开始 （在loadMoveData()后isNewmoveStart会改变）
		boolean isNewmoveStart_temp=isNewmoveStart;		
		loadMoveData();
		
		int MovePositon_x=constant.getMovePositon_x();
		int MovePositon_y=constant.getMovePositon_y();
		
		if(isIllegal(MovePositon_x,MovePositon_y+1)==true){	
			//
			if(isNewmoveStart_temp==false){
				constant.setMovePositon_x(MovePositon_x);
				constant.setMovePositon_y(MovePositon_y+1);
			}
		}
		else{
			for(int i=0;i<constant.getMoveunit_count();i++){
				for(int j=0;j<constant.getMoveunit_count();j++){
					int i_x=i+MovePositon_x;
					int j_y=j+MovePositon_y;
					boolean movestatus=moveunit_Status[i][j];
					if(j_y<0){
						continue;
					}
					if(movestatus==true){		
						unit_Status[i_x][j_y]=true;
						isNewmoveStart=true;
					}			
				}
			}
			rowDelete();
			//判断是否结束
			if(isGameOver()==true){
				GameOver.OnGameOver(0);
			}
			constant.resetMovePosition();			
		}
	}
	
	//左键
	public void Left_press(){
		//loadMoveData();
		int MovePositon_x=constant.getMovePositon_x();
		int MovePositon_y=constant.getMovePositon_y();
		if(isIllegal(MovePositon_x-1,MovePositon_y)==true){
			constant.setMovePositon_x(MovePositon_x-1);
			constant.setMovePositon_y(MovePositon_y);
		}
	}
	
	//右键
	public void Right_press(){		
		//loadMoveData();
		int MovePositon_x=constant.getMovePositon_x();
		int MovePositon_y=constant.getMovePositon_y();
		if(isIllegal(MovePositon_x+1,MovePositon_y)==true){
			constant.setMovePositon_x(MovePositon_x+1);
			constant.setMovePositon_y(MovePositon_y);
		}
	}
	
	//变形
	public void Change_press(){		
		changeMoveData();
	}
	
	//下键
	public void Down_press(){
		
		int MovePositon_x=constant.getMovePositon_x();
		int MovePositon_y=constant.getMovePositon_y();
		int cols=downLogical(moveunit_Status,MovePositon_x,MovePositon_y);
		//未得到正确结果
		if(cols==-1){
			return;
		}
		
		for(int i=0;i<constant.getMoveunit_count();i++){
			for(int j=0;j<constant.getMoveunit_count();j++){
				int i_x=i+MovePositon_x;
				int j_y=j+MovePositon_y+cols;
				boolean movestatus=moveunit_Status[i][j];
				if(j_y<0){
					continue;
				}
				if(movestatus==true){		
					unit_Status[i_x][j_y]=true;						
				}		
			}
		}
		isNewmoveStart=true;
		rowDelete();
		if(isGameOver()==true){
			Log.d("gameoverQ","over");
		}
		constant.resetMovePosition();	
		loadMoveData();
	}
	
	private boolean isIllegal(int position_x,int position_y){		
		boolean illegal=true;		
		for(int i=0;i<constant.getMoveunit_count();i++){
			for(int j=0;j<constant.getMoveunit_count();j++){
				int i_x=i+position_x;
				int j_y=j+position_y;
				boolean movestatus=moveunit_Status[i][j];
				if(movestatus==true){		
					if(i_x<0||i_x>(constant.getWidth_count()-1)||
							j_y>(constant.getHeight_count()-1)){
						return false;
					}
					//if 纵坐标小于0 继续循环
					if(j_y<0){
						continue;
					}
					if(unit_Status[i_x][j_y]==true){
						return false;
					}
				}				
			}
		}		
		return illegal;		
	}
	
	//是否能变形
	private boolean isChange(boolean moveunit_Statusm[][],int position_x,int position_y){
		boolean illegal=true;		
		for(int i=0;i<constant.getMoveunit_count();i++){
			for(int j=0;j<constant.getMoveunit_count();j++){
				int i_x=i+position_x;
				int j_y=j+position_y;
				boolean movestatus=moveunit_Status[i][j];
				if(movestatus==true){
					if(i_x<0||i_x>(constant.getWidth_count()-1)||
							j_y<0||j_y>(constant.getHeight_count()-1)){
						continue;
					}
					if(unit_Status[i_x][j_y]==true){
						return false;
					}
				}				
			}
		}		
		return illegal;	
	}
	
	//变形时的 边界调整
	private void borderDetect_change(boolean moveunit_Status[][],int position_x,int position_y){
		int leftMax=0;
		int rightMax=constant.getMoveunit_count();
		int topMax=0;
		int bottomMax=0;
		for(int i=0;i<constant.getMoveunit_count();i++){
			for(int j=0;j<constant.getMoveunit_count();j++){
				int i_x=i+position_x;
				int j_y=j+position_y;
				boolean movestatus=moveunit_Status[i][j];
				if(movestatus==true){
					//左边界判断
					if(i_x<0){
						if(leftMax<=i){
							leftMax=i;
							//int x=constant.getMovePositon_x()-i_x;
							constant.setMovePositon_x(-i_x-1);
						}
					}
					//右边界判断
					else if(i_x>constant.getWidth_count()-1){
						if(rightMax>=i){
							rightMax=i;
							//int x=constant.getMovePositon_x()-i_x;
							constant.setMovePositon_x(position_x-(constant.getMoveunit_count()-i));
						}
					}								
				}
			}
		}			
	}
	
	//返回需要 停止的行
	private int downLogical(boolean moveunit_Status[][],int position_x,int position_y){
	
		for(int k=0;k<height_count;k++){			
			for(int i=0;i<constant.getMoveunit_count();i++){
				for(int j=0;j<constant.getMoveunit_count();j++){
					int i_x=i+position_x;
					int j_y=j+position_y+k;
					boolean movestatus=moveunit_Status[i][j];
					if(movestatus==true){		
						if(j_y>(constant.getHeight_count()-1)){						
							return k-1;
						}
						//if 纵坐标小于0 继续循环
						if(j_y<0){
							
							continue;
						}						
						if(unit_Status[i_x][j_y]==true){
							
							return k-1;
						}
					}		
				}
			}		
		}	
		return -1;
	}
	
	private boolean isGameOver(){
		boolean isover=false;
		for(int i=0;i<constant.getWidth_count();i++){
			isover= unit_Status[i][0];
			if(isover==true){
				
				return true;
			}
		}	
		return false;
		
	}
	
	
	private void changeMoveData(){	
		//int Shape_count=constant.getShapes_count().get(shapeall_index);
		int Shape_count=constant.getShapes_count().get(shapeArrayIndex_Now.getShapeall_index());
	/*	int index_temp=shape_index;
		if(shape_index==Shape_count-1){
			shape_index=0;
		}
		else if(shape_index<Shape_count-1){
			shape_index++;
		}*/
		
		int index_temp=shapeArrayIndex_Now.getShape_index();
		if(index_temp==Shape_count-1){
			shapeArrayIndex_Now.setShape_index(0);
		}
		else if(index_temp<Shape_count-1){		
			shapeArrayIndex_Now.setShape_index(index_temp+1);
		}	
		moveunit_Status=ListMove_Data.get(shapeArrayIndex_Now.getShapeall_index()).get(shapeArrayIndex_Now.getShape_index());
		
		//不能改变 则保持原来的索引
		if(isChange(moveunit_Status,constant.getMovePositon_x(),
				constant.getMovePositon_y())==false){
			moveunit_Status=ListMove_Data.get(shapeArrayIndex_Now.getShapeall_index()).get(index_temp);
		}
		else{
			
			borderDetect_change(moveunit_Status,constant.getMovePositon_x(),
					constant.getMovePositon_y());
		}
		
		constant.setMoveunit_Status(moveunit_Status);
	}
	
	
    private void loadMoveData(){
	 if(isNewmoveStart==true){
		isNewmoveStart=false;
		//如果是第一次 则自动产生形状
		if(isLoadMoveDatafirsttime==true){
			isLoadMoveDatafirsttime=false;
			//isNewmoveStart=false;
			//随机获取数据
			int shapeAll_count=constant.getShapeAll_count();

			shapeArrayIndex_Now.setShapeall_index((int) (Math.random()*shapeAll_count));
			int Shape_count=constant.getShapes_count().get(shapeArrayIndex_Now.getShapeall_index());
			shapeArrayIndex_Now.setShape_index((int) (Math.random()*Shape_count));
			
			moveunit_Status=ListMove_Data.get(shapeArrayIndex_Now.getShapeall_index()).get(shapeArrayIndex_Now.getShape_index());	
			/*shapeArrayIndex_Now.setShape_index(1);
			shapeArrayIndex_Now.setShapeall_index(4);		
			moveunit_Status=ListMove_Data.get(4).get(1);*/		
			constant.setMoveunit_Status(moveunit_Status) ;		
			beginMovePotion(moveunit_Status);
			
		}
		else{
			shapeArrayIndex_Now.setShape_index(ShapeArrayIndex_Next.getShape_index());
			shapeArrayIndex_Now.setShapeall_index(ShapeArrayIndex_Next.getShapeall_index());
			
			moveunit_Status=ListMove_Data.get(shapeArrayIndex_Now.getShapeall_index()).get(shapeArrayIndex_Now.getShape_index());		
			constant.setMoveunit_Status(moveunit_Status) ;		
			beginMovePotion(moveunit_Status);
		}
		//产生下一个形状
			//随机获取数据
			int shapeAll_count=constant.getShapeAll_count();
			ShapeArrayIndex_Next.setShapeall_index((int) (Math.random()*shapeAll_count));
			int Shape_count=constant.getShapes_count().get(ShapeArrayIndex_Next.getShapeall_index());
			ShapeArrayIndex_Next.setShape_index((int) (Math.random()*Shape_count));
			//moveunit_Status=ListMove_Data.get(ShapeArrayIndex_Next.getShapeall_index()).get(ShapeArrayIndex_Next.getShape_index());	
		
	  }
	}
	
	//开始时的移动位置坐标
	private void beginMovePotion(boolean moveunit_Status[][]){
		/*for(int i=0;i<constant.getMoveunit_count();i++){
			for(int j=0;j<constant.getMoveunit_count();j++){
				boolean movestatus=moveunit_Status[i][j];
				if(movestatus==true){		
					int movenum=constant.getMoveunit_count()-j;
					constant.setMovePositon_y(-movenum);	
					return;
				}		
			}
		}*/
	}
	
	
/*	
	public static Logical getInstance(Context context) 
	{
		if (instance == null) 
		{
			instance = new Logical(context);
			return instance;
		}
		return instance;
	}*/
	
	//加分回调
	private OnLoadscoreListener OnLoadscore;
	public void setLoadscoreLintener(OnLoadscoreListener listener){
		OnLoadscore = listener;
	}
	public interface OnLoadscoreListener{
		public void OnLoadscores(int score);
	}
	
	//结束回调
	private OnGameOverListener GameOver;
	public void setGameOverLintener(OnGameOverListener listener){
		GameOver = listener;
	}
	public interface OnGameOverListener{
		public void OnGameOver(int GameOverType);
	}
}

