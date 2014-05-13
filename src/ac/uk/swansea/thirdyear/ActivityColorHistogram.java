/***********************************************************************************************/
/* This activity renders the image on screen with a breakdown of the RGB histogram             */
/*                                                                                             */
/* adapted from:http://www.stanford.edu/class/ee368/Android/ViewfinderEE368/ViewfinderEE368.java*/                                                                              
/************************************************************************************************/

package ac.uk.swansea.thirdyear;


import java.nio.ByteBuffer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ActivityColorHistogram extends Activity {
    /* Image View */

    private DrawOnTop mDrawOnTop;
    private Boolean is_showing= false;
    private Bitmap myBitmap;
    public String receivedImagePath;
    private LinearLayout imageView; 
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_color_histogram);
        
                
          receivedImagePath = getIntent().getExtras().getString("file_path");

    		// Load the image from file
			myBitmap = null;
        	//myBitmap = BitmapFactory.decodeFile(receivedImagePath); //this is whats causing a crash with some images
            myBitmap = MainImageDisplay.compressImage(receivedImagePath,300,300);
			//Toast.makeText(getApplicationContext(), "bitmap received " + receivedImagePath, Toast.LENGTH_LONG).show();
            // display_RGB
            imageView = (LinearLayout) this.findViewById(R.id.image_histogram);
            if(is_showing){ //if is already showing you hide the layout
                imageView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Inside if", Toast.LENGTH_LONG).show();

            }  else {         //else you draw it with your custom view - the view should be in your class 

            	//Toast.makeText(getApplicationContext(), "Inside else", Toast.LENGTH_LONG).show();
            	imageView.setVisibility(View.VISIBLE);
                mDrawOnTop = new DrawOnTop(ActivityColorHistogram.this.getApplicationContext(), myBitmap); 
                mDrawOnTop.mImageWidth = myBitmap.getWidth();
                mDrawOnTop.mImageHeight = myBitmap.getHeight();
                mDrawOnTop.mBitmap = Bitmap.createBitmap(mDrawOnTop.mImageWidth, mDrawOnTop.mImageHeight, Bitmap.Config.RGB_565);
                mDrawOnTop.mRGBData = new int[mDrawOnTop.mImageWidth * mDrawOnTop.mImageHeight];

                int bytes = myBitmap.getByteCount();
                ByteBuffer buffer = ByteBuffer.allocate(bytes);
                myBitmap.copyPixelsToBuffer(buffer);
                
                byte[] array = buffer.array();
                mDrawOnTop.mYUVData = array;
                imageView.addView(mDrawOnTop);
            }
     }

}
