/******************************************************************************/
/* This activity opens the phone's camera                                     */
/*                                                                            */
/* Code adapted from http://developer.android.com/training/index.html         */                                                                              
/******************************************************************************/


package ac.uk.swansea.thirdyear;

import java.io.IOException;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class StartCamera extends Activity implements SurfaceHolder.Callback {

	private SurfaceView cameraView;
	private SurfaceHolder cameraHolder;
	private Camera myCamera;
	private boolean isPreviewRunning;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incorporate_camera);
		cameraView = (SurfaceView) findViewById(R.id.camera_button);
		cameraHolder = cameraView.getHolder();
		cameraHolder.addCallback(this);
		cameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void cameraCreated(SurfaceHolder holder) {
		myCamera = Camera.open();
		if (myCamera != null) {
			Camera.Parameters params = myCamera.getParameters();
			myCamera.setParameters( params);
		}
		else {
			Toast.makeText(getApplicationContext(), "Camera Not Available", 
					Toast.LENGTH_LONG).show();
			finish();
		}	
	}
	
	public void surfaceChanged (SurfaceHolder holder, int format, int w, int h) {
		if (isPreviewRunning) {
			myCamera.stopPreview();
		}
		Camera.Parameters p = myCamera.getParameters();
		p.setPreviewSize(w, h);
		p.setPreviewFormat(PixelFormat.JPEG);
		myCamera.setParameters(p);
		try {
			myCamera.setPreviewDisplay(holder);
			myCamera.startPreview();
			isPreviewRunning = true;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}  
	public void surfaceDestroyed (SurfaceHolder holder) {
		myCamera.stopPreview();
		isPreviewRunning = false;
		myCamera.release();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
}
