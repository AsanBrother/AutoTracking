package com.graduation.autotracking2.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Point;

import android.util.Log;


public class MathUtils {
	public static final String TAG = "MathUtils";
	//最小二乘法
	public static Line leastSquaresTechnique(ArrayList <Double> mPointX,ArrayList <Double> mPointY) {
    	Point[] mPoint = new Point[2];
    	
    	double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
    	int number=0;
    	while (number < mPointX.size()) {
    	   sumx += mPointX.get(number);
    	   sumx2 += mPointX.get(number) * mPointX.get(number);
    	   sumy += mPointY.get(number);
    	   number++;
    		
    	}
    	// ��ƽ����
    	double xbar = sumx / number;
    	double ybar = sumy / number;
    	// ����ϵ��
    	double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
    	for (int i = 0; i < number; i++) {
    	xxbar += (mPointX.get(i) - xbar) * (mPointX.get(i)  - xbar);
    	yybar += (mPointY.get(i) - ybar) * (mPointY.get(i) - ybar);
    	xybar += (mPointX.get(i) - xbar) *(mPointY.get(i) - ybar);
    	}
    	double k = xybar / xxbar;
    	double b = ybar - k * xbar;
    	//��Androidƽ̨���޷�����ֵΪ���ͳ������ؾ���ĵ�,������ƵΪ1280*720,����Ӧ������ͬ�ĵ���
    	if(k<0&&b>0) {
    		if(b<720&&(-b/k)<1280) {
	    	mPoint[0] = new Point(0, b);
	    	mPoint[1] = new Point(-b/k,0);
    		}else if(b>720&&(-b/k)<1280){
    			mPoint[0] = new Point((720-b)/k, 720);
    			mPoint[1] = new Point(-b/k,0);
    		}else if(b>720&&(-b/k)>1280){
    			mPoint[0] = new Point((720-b)/k, 720);
    			mPoint[1] = new Point(1280,k*1280+b);
    		}else if(b<720&&(-b/k)>1280) {
    			mPoint[0] = new Point(0, b);
    			mPoint[1] = new Point(1280,k*1280+b);
    		}
    	}else if(k>0&&b<0) {
    		if(-b/k<1280&&(720-b)/1280<k) {
	    		mPoint[0] = new Point((720-b)/1280, 720);
	    		mPoint[1] = new Point(-b/k,0);
    		}else if(-b/k<1280&&(720-b)/1280>k) {
    			mPoint[0] = new Point(1280, 1280*k+b);
        		mPoint[1] = new Point(-b/k,0);
    		}
    	}else if(k>0&&b>0) {
    		if(b<720&&1280*k+b<720) {
	    		mPoint[0] = new Point(0, b);
	    		mPoint[1] = new Point(1280,1280*k+b);
    		}else if(b<720&&1280*k+b>720){
    			mPoint[0] = new Point(0, b);
	    		mPoint[1] = new Point((720-b)/k,720);
    		}
    	}
    	
    	Log.i(TAG,"y   = " + k + " * x + " + b);
    	Log.i(TAG,"x:  = " + mPoint[0] + " ;y: + " + mPoint[1]);
    	return new Line(mPoint[0],mPoint[1]);
    	
    }
	 //聚类算法
	public static Line[]  getTwoLines(List<Line> mLines,int number) {
    	
    	ArrayList<Double> mPointX = new ArrayList<Double>();
    	ArrayList<Double> mPointY= new ArrayList<Double>();
    	Line[] twoLines = new Line[2];   
    	int index=0;
    	for(;index<number;index++) {
    		mPointX.add(mLines.get(index).start.x);
    		mPointX.add(mLines.get(index).end.x);
    		mPointY.add(mLines.get(index).start.y);
    		mPointY.add(mLines.get(index).end.y);
//    		Log.i(TAG,mLines.get(index).start.x+":"+mLines.get(index).end.x
//    				+":::"+mLines.get(index).start.y+":"+mLines.get(index).end.y);
    	}
 //   	Log.i(TAG,mLines.get(number).start.x+":"+mLines.get(number).end.x);
    	twoLines[0] = MathUtils.leastSquaresTechnique(mPointX, mPointY);
    	mPointX.clear();
    	mPointY.clear();
   // 	Log.i(TAG,mPointX.size()+"KKKKK"+mPointY.size());
    	for( index=mLines.size()-1;index>number;index--) {
    		mPointX.add(mLines.get(index).start.x);
    		mPointX.add(mLines.get(index).end.x);
    		mPointY.add(mLines.get(index).start.y);
    		mPointY.add(mLines.get(index).end.y);
//    		Log.i(TAG,mLines.get(index).start.x+":"+mLines.get(index).end.x
//    				+":::"+mLines.get(index).start.y+":"+mLines.get(index).end.y);
    				
    	}
    	twoLines[1] = MathUtils.leastSquaresTechnique(mPointX, mPointY);
    	
    	return twoLines;
    	
    }
	
	public static double getAngle(List<Line> mLines) {
		double angle = 0.0;
		double angleTemp=0.0;
		double acos =0.0;
		double mark=1.0;
		int number=0;
		double length;
		for(int index=0;index<mLines.size();index+=1) {
			if((mLines.get(index).end.x-mLines.get(index).start.x)!=0&&mLines.get(index).end.y-mLines.get(index).start.y!=0) {
//				Log.i(TAG,mLines.get(index).start.x+":"+mLines.get(index).end.x
//	    				+":::"+mLines.get(index).start.y+":"+mLines.get(index).end.y);
				
				length = Math.sqrt(Math.pow(Math.abs(mLines.get(index).end.y-mLines.get(index).start.y),2)+
						Math.pow(Math.abs(mLines.get(index).end.x-mLines.get(index).start.x),2));
				
				acos = (mLines.get(index).end.x-mLines.get(index).start.x)/length;
				
				mark = (mLines.get(index).end.y-mLines.get(index).start.y)/(mLines.get(index).end.x-mLines.get(index).start.x);
				angleTemp = Math.acos(acos)*180/Math.PI;
				if(angleTemp<0) {
					angleTemp=-angleTemp;
				}
				if(mark>0) {					
					angle += angleTemp;
				}else {
					angle -= angleTemp;
				}
				
			//	Log.i(TAG,"angle"+angleTemp+"acos" +acos+"mark"+mark);
				number++;
			}
			
		}
		//Log.i(TAG,"number"+number+"angle" +angle);
		if(number!=0) {
			angle = angle/number;
		//	Log.i(TAG,"angle"+angle+"number" +number);
		}else {
			angle = 0.0;
		}
		return angle ;
	}
    
}
