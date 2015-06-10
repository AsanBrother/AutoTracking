package com.graduation.autotracking2;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import com.graduation.autotracking2.view.CannySurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import android.view.Window;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
	private String TAG = "MAINACTIVITY";
	private FrameLayout mFrameLayout;
	private CannySurfaceView mCannySurfaceView;
	
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

		@Override
		public void onManagerConnected(int status) {
			// TODO Auto-generated method stub
			switch (status) {
			case BaseLoaderCallback.SUCCESS:
				Log.i(TAG,"LOAD_SUCCESS");
				
					initView();				
   				break;

			default:
				super.onManagerConnected(status);
				Log.i(TAG,"LOAD_FAIL");
				break;
			}
		}
		
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, getApplicationContext(), mLoaderCallback);
		Log.i(TAG,"onResume sucess load OpenCV...");
	}

	private void initView() {
    	mFrameLayout = (FrameLayout)findViewById(R.id.canny_preview);
    	mCannySurfaceView = new CannySurfaceView(this);
    	mFrameLayout.addView(mCannySurfaceView);
    	  	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
