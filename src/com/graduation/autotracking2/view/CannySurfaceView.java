package com.graduation.autotracking2.view;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import com.graduation.autotracking2.utils.Line;
import com.graduation.autotracking2.utils.MathUtils;
import com.graduation.autotracking2.utils.SortedPoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CannySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable{
	private static final String TAG = "CannySurfaceView::SurfaceView";


    private SurfaceHolder       mHolder;
    private VideoCapture        mCamera;

    private Mat rgbMat;
    private Mat mShow;
    
    private Mat lines ;
    private List<Line> mLines = new ArrayList<Line>();
	
	private Line[] twoLines = new Line[2];     

	public CannySurfaceView(Context context) {
		super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
	}

	public boolean openCamera() {
        Log.i(TAG, "openCamera");     
        synchronized (this) {
            releaseCamera();
            mCamera = new VideoCapture(Highgui.CV_CAP_ANDROID);
            if (!mCamera.isOpened()) {
                mCamera.release();
                mCamera = null;
                Log.e(TAG, "Failed to open native camera");
                return false;
            }
        }
        return true;
    }
	
	public void releaseCamera() {
        Log.i(TAG, "releaseCamera");
        synchronized (this) {
//          if (mCamera != null) {
//                  mCamera.release();
//                  mCamera = null;
//      }
        }
    }
	public void setupCamera(int width, int height) {
        Log.i(TAG, "setupCamera("+width+", "+height+")");
        if (mCamera != null && mCamera.isOpened()) {
            List<Size> sizes = mCamera.getSupportedPreviewSizes();
            int mFrameWidth = width;
            int mFrameHeight = height;

            // selecting optimal camera preview size
            {
                double minDiff = Double.MAX_VALUE;
                for (Size size : sizes) {
                    if (Math.abs(size.height - height) < minDiff) {
                        mFrameWidth = (int) size.width;
                        mFrameHeight = (int) size.height;
                        minDiff = Math.abs(size.height - height);
                    }
                }
            }

            mCamera.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, mFrameWidth);
            mCamera.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, mFrameHeight);
        }


    }
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

        rgbMat = new Mat();
        mShow = new Mat();
        lines = new Mat();
        openCamera();
        (new Thread(this)).start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		setupCamera(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
		
	}

	
	protected Bitmap processFrame(VideoCapture capture) {
 
/*******因为在AndroidMainfext.xml中将屏幕旋转所以坐标系同样需要变换****
		
	      （0,0）**************>Y
		*
		*
		*
		*
		*
		*
		*
		X
//***************************************************************/		
		
		
        int threshold = 150;
        int minLineSize = 50;
        int lineGap = 35;
        double minYgap = 30;
        int number=0;
        
        double average_x1=0;
        double average_y1=0;
        double average_x2=0;
        double average_y2=0;
        String mMsg ="";
        //将之前的信息清空
        
        mLines.clear();
        
        //************************//
        
         Mat mErodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10,25));
    	 Mat mDilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10,25));
    	
    	
			
			
         capture.retrieve(rgbMat, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA); 
        
         Log.i(TAG,rgbMat.cols()+"??"+rgbMat.rows());
        
         Imgproc.cvtColor(rgbMat, rgbMat, Imgproc.COLOR_BGRA2GRAY, 4);
   	 
    	 Imgproc.GaussianBlur(rgbMat, rgbMat, new Size(5,5), 2.2,2);
    	 
    	 Imgproc.dilate(rgbMat, rgbMat, mDilateElement);
    	 Imgproc.erode(rgbMat, rgbMat, mErodeElement);
   	 
    	 Imgproc.Canny(rgbMat, rgbMat,50, 50);

    	 Imgproc.cvtColor(rgbMat, mShow, Imgproc.COLOR_GRAY2BGRA, 4); 
    	
    	 Imgproc.HoughLinesP(rgbMat, lines, 1, Math.PI/180, threshold,minLineSize,lineGap);
         //Log.i(TAG,lines.cols()+"???????");
    	
        
         for (int x = 0; x < lines.cols(); x+=1) 
         {
               double[] vec = lines.get(0, x);
               
               if(vec!=null) {
	               double x1 = vec[0], 
	                      y1 = vec[1],
	                      x2 = vec[2],
	                      y2 = vec[3];
	               Point start = new Point(x1, y1);
	               Point end = new Point(x2, y2);
	              
	               if(!(Math.abs(x2-x1)<minYgap) ){
	
	            	   Line mSingleline = new Line(start, end) ;
	                   mLines.add(mSingleline);
	            	
	            	   number++;
	            	   average_x1+=x1;
	            	   average_y1+=y1;
	            	   average_x2+=x2;
	            	   average_y2+=y2;
	            	   
	            	   Core.line(mShow, start, end, new Scalar(255,0,0,255), 3);
               }
            	   
               }
         }
         
         if(number!=0) {
        	 
        	 average_x1/=number;
        	 average_y1/=number;
        	 average_x2/=number;
        	 average_y2/=number;
        	 
        	 double angle = MathUtils.getAngle(mLines);
        	 
        	// Log.i(TAG,"angle"+angle);
        	 
        	 if(Math.abs(angle)<10) {
        	 
	        	 Line midLine = new Line(new Point(average_x1,average_y1),new Point(average_x2,average_y2));
	        	 mLines.add(midLine);
	        	 
	        	 
	        	 
	        	 Collections.sort(mLines, new SortedPoint());
	        	 
	        	 twoLines = MathUtils.getTwoLines(mLines,mLines.lastIndexOf(midLine));
	        	
	        	 if(twoLines[0].start!=null&&twoLines[0].end!=null&&twoLines[1].start!=null&&twoLines[1].end!=null) {
		        	 Point pStart = new Point((twoLines[0].start.x+twoLines[1].start.x)/2, (twoLines[0].start.y+twoLines[1].start.y)/2);
		        	 Point pEnd = new Point((twoLines[0].end.x+twoLines[1].end.x)/2, (twoLines[0].end.y+twoLines[1].end.y)/2);
		        	 
		        	 midLine = new Line(pStart, pEnd);
		        	 //Log.i(TAG,number+"::::::"+average_x1+":"+average_y1+";"+average_x2+":"+average_y2);
		        	 Core.line(mShow, twoLines[0].start, twoLines[0].end, new Scalar(255,255,0,100), 15);
		        	 Core.line(mShow, twoLines[1].start, twoLines[1].end, new Scalar(255,255,0,100), 15);
		        	 
		        	 Core.line(mShow, midLine.start, midLine.end, new Scalar(255,0,255,100), 15);
		        	 double distance = (midLine.start.y+midLine.end.y)/2-360;
		        	 if(distance<0) {
		        		 mMsg = "在道路中心线左侧"+(int)(-distance)+"个单位,需要向右侧调整";
		        	 }else {
		        		 mMsg = "在道路中心线右侧"+(int)distance+"个单位,需要向左侧调整";
		        	 }
		        	
		        	// Core.putText(mShow,mMsg, new Point(0, 360), 2, 2, new Scalar(255,255,255,0),1);
	        	 }else {
	        		 mMsg = "道路消失";
	        		// Core.putText(mShow,mMsg, new Point(0, 360), 2, 2, new Scalar(255,255,255,0),1);
	        	 }
        	 }else {
        		
        		 if(angle>0) {
        			 mMsg = "偏向左侧"+(int)angle+"度，需要向右侧偏转";
        		 }else {
        			 mMsg = "偏向右侧"+(int)(-angle)+"度，需要向左侧偏转";
        		 }
        		 //Core.putText(mShow,mMsg, new Point(0, 360), 3, 2, new Scalar(255,255,0,0),2);
        	 }
        	 sendDataToPC(mMsg);
         }
    	
        Bitmap bmp = Bitmap.createBitmap(rgbMat.cols(), rgbMat.rows(), Bitmap.Config.ARGB_8888);
        
        try {
                Utils.matToBitmap(mShow, bmp);
                
            return bmp;
        } catch(Exception e) {
                Log.e("org.opencv.samples.tutorial2", "Utils.matToBitmap() throws an exception: " + e.getMessage());
            bmp.recycle();
            return null;
        }
     }
	
	
	@Override
	public void run() {
		 while (true) {
            Bitmap bmp = null;
         
            if (mCamera == null)
                break;

            if (!mCamera.grab()) {
                Log.e(TAG, "mCamera.grab() failed");
                break;
            }
            long startTime=System.currentTimeMillis();   //获取开始时间  
            bmp = processFrame(mCamera);  
            
	        if (bmp != null) {
	            Canvas canvas = mHolder.lockCanvas();
	            if (canvas != null) {
	                canvas.drawBitmap(bmp, (canvas.getWidth() - bmp.getWidth()) , (canvas.getHeight() - bmp.getHeight()) , null);
	                
	                mHolder.unlockCanvasAndPost(canvas);
	                
	            }
	            bmp.recycle();
	            long endTime=System.currentTimeMillis(); //获取结束时间  
	            Log.i("程序运行时间： ","程序运行时间： "+(endTime-startTime)+"ms");  
	        }
	        //Log.i(TAG, "Finishing processing thread");
	    }
		if (rgbMat != null)
			rgbMat.release();
        if (mShow != null)
        	mShow.release();
      

        rgbMat = null;
         mShow = null;

		
	}
	
	private void sendDataToPC (String data) {
		DatagramSocket ds = null;
		data="$"+data+"@";
		try {
			ds = new DatagramSocket(10000);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	
		byte[] buf = data.getBytes();
		DatagramPacket dp;
		try {
			dp = new DatagramPacket(buf,buf.length,InetAddress.getByName("172.25.10.1"),15000);
			
			ds.send(dp);
			
			ds.close();
		} catch (Exception e) {

			e.printStackTrace();
		}		
	}

}
