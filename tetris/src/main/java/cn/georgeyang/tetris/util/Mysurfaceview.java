package cn.georgeyang.tetris.util;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class Mysurfaceview extends SurfaceView implements SurfaceHolder.Callback{
	private boolean moveunit_Status[][];
	
	private Logical logical;
	
	private boolean unit_Status[][];
	
	private int line_width=5;
	 private Context context;
	 public boolean isrun=false;
	 private Thread myThread;
	 private SurfaceHolder holder;  

	 private Paint paint,paint_line;
	 private Constant constant;
	 private int startPosition_x,startPosition_y;
	 private int leve;
	 public void loadLogical(Logical logical,int leve){
		 this.logical=logical;
		 this.leve=leve;
		 
		 constant=logical.constant;
		 
		unit_Status=constant.getUnit_Status();		
		startPosition_x=constant.getStartPosition_x();
		startPosition_y=constant.getStartPosition_y();
		moveunit_Status=constant.getMoveunit_Status();
		
		 isrun=true;
		 new MyThread().start();
		 
	 }
	 
	public Mysurfaceview(Context context, AttributeSet attrs) {

		super(context, attrs);
		this.context = context;
		
		//constant=Constant.getInstance(context);
	
		//logical=Logical.getInstance(context);
		
		
		
		 holder=this.getHolder();
         holder.addCallback(this);
        
         paint=new Paint();
         paint.setAntiAlias(true);// 
         paint.setColor(Color.WHITE);
         paint.setStyle(Paint.Style.FILL);//设置填满 
         
         paint_line=new Paint();
         paint_line.setAntiAlias(true);// 
         paint_line.setColor(Color.RED);
         paint_line.setStrokeWidth(line_width);
         paint_line.setStyle(Paint.Style.FILL);//设置填满 
		// TODO Auto-generated constructor stub
	}
	
	public Mysurfaceview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void ondraw()
	{
		Canvas c=null;
		try {             
                c=holder.lockCanvas();
                c.drawColor(Color.BLACK);//              
                for(int i=0;i<constant.getWidth_count();i++){
                	 for(int j=0;j<constant.getHeight_count();j++){
                		 if(unit_Status[i][j]==true){
                		 c.drawRect(startPosition_x+i*constant.getWidth_lenght(), startPosition_y+(j)*constant.getHeight_lenght(),
                				 startPosition_x+(i+1)*constant.getWidth_lenght()-constant.getUnit_interval(), startPosition_y+(j+1)*constant.getHeight_lenght()-constant.getUnit_interval(), paint); 
                		 }
                		}
                }
                moveunit_Status=constant.getMoveunit_Status();
                for(int i=0;i<4;i++){
        			for(int j=0;j<4;j++){
        				boolean status=moveunit_Status[i][j];
        				//Log.d(status+"status","status");
        				if(status==true){		
        					int i_x=i+constant.getMovePositon_x();
        					int j_y=j+constant.getMovePositon_y();
        					if(i_x<0||i_x>(constant.getWidth_count()-1)||
        							j_y<0||j_y>(constant.getHeight_count()-1)){
        						continue;
        					}
        					c.drawRect(startPosition_x+i_x*constant.getWidth_lenght(), startPosition_y+(j_y)*constant.getHeight_lenght(),
                   				 startPosition_x+(i_x+1)*constant.getWidth_lenght()-constant.getUnit_interval(), startPosition_y+(j_y+1)*constant.getHeight_lenght()-constant.getUnit_interval(), paint);    		
        				}	
        			}
        		}
                //绘制下一个形状
                List<List<boolean[][]>> ListMove_Data=constant.getListMove_Data();          
                boolean nextshape_status[][]=ListMove_Data.get(logical.ShapeArrayIndex_Next.getShapeall_index()).get(logical.ShapeArrayIndex_Next.getShape_index());			
                int nextshapePosition_x=constant.getNextShapeStart_x();
                int nextshapePosition_y=constant.getNextShapeStart_y();
                
                for(int i=0;i<4;i++){
        			for(int j=0;j<4;j++){    				
        				boolean status=nextshape_status[i][j];

        				if(status==true){
        					
        					c.drawRect(nextshapePosition_x+i*constant.getNextshapewidth(), nextshapePosition_y+(j)*constant.getNextshapewidth(),
        							nextshapePosition_x+(i+1)*constant.getNextshapewidth()-constant.getUnit_interval(), nextshapePosition_y+(j+1)*constant.getNextshapewidth()-constant.getUnit_interval(), paint);    		
        				}	
        			}
        		}
                c.drawLine (startPosition_x,  startPosition_y,  startPosition_x,  startPosition_y+constant.getRect_height(),  paint_line);
                c.drawLine (startPosition_x,  startPosition_y+constant.getRect_height(), startPosition_x+constant.getRect_width(),  startPosition_y+constant.getRect_height(),  paint_line);
                c.drawLine (startPosition_x+constant.getRect_width(),  startPosition_y+constant.getRect_height(),  startPosition_x+constant.getRect_width(),  startPosition_y,  paint_line);      
                
		  } catch (Exception e) {
              e.printStackTrace();
		  }finally
		  {
              if(c!=null)
              {
                       holder.unlockCanvasAndPost(c);//
              }
		  }             
	}
		
	 @Override
     public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                     int arg3) {
             // TODO Auto-generated method stub
             
     }

     @Override
     public void surfaceCreated(SurfaceHolder arg0) {
             // TODO Auto-generated method stub
            // isrun=true;
            // ondraw();        
            // new MyThread().start();
     }

     @Override
     public void surfaceDestroyed(SurfaceHolder arg0) {
             // TODO Auto-generated method stub
             isrun = false;
     }

     
     
     class  MyThread extends Thread
     {
                 @Override
                 public void run() {
                         // TODO Auto-generated method stub
                         super.run();
                         int count = 0;
                         while(isrun)
                         {                        	 
                        	//logical.logical();                      	
                         	try {
                         		ondraw();
                         		logical.Down_ontime();
                         		sleep(1500-140*leve);								
 							} catch (InterruptedException e) {
 								// TODO Auto-generated catch block
 								e.printStackTrace();
 							}
                         }
                 }
             
     }
}
