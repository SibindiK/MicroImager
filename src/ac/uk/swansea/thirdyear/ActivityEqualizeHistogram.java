/********************************************************************************/
/* This activity equalises the image histogram                                  */
/*                                                                              */
/* adapted from http://www.stanford.edu/class/ee368/Android/                    */
/*********************************************************************************/

package ac.uk.swansea.thirdyear;

import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ActivityEqualizeHistogram extends Activity {
    private DrawOnView mDrawOnTop;
	private LinearLayout imView;
	public String receivedImagePath ="";
	public String savedImagePath = "";
	private Bitmap equalizedBitmap = null;
	private Bitmap myBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_histogram);
        receivedImagePath = getIntent().getExtras().getString("file_path");
		// Load the image from file
		myBitmap = null;
    	//myBitmap = BitmapFactory.decodeFile(receivedImagePath); //this is whats causing a crash with some images
        myBitmap = MainImageDisplay.compressImage(receivedImagePath,300,300);
		Toast.makeText(getApplicationContext(), "bitmap received " + receivedImagePath, Toast.LENGTH_LONG).show();
        // display_RGB
        imView = (LinearLayout) this.findViewById(R.id.image_histogram);
       	imView.setVisibility(View.VISIBLE);
        mDrawOnTop = new DrawOnView(ActivityEqualizeHistogram.this.getApplicationContext()); 
        mDrawOnTop.mImageWidth = myBitmap.getWidth();
        mDrawOnTop.mImageHeight = myBitmap.getHeight();
        mDrawOnTop.mBitmap = Bitmap.createBitmap(mDrawOnTop.mImageWidth, mDrawOnTop.mImageHeight, Bitmap.Config.RGB_565);
        mDrawOnTop.mRGBData = new int[mDrawOnTop.mImageWidth * mDrawOnTop.mImageHeight];

        int bytes = myBitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        myBitmap.copyPixelsToBuffer(buffer);
            
        byte[] array = buffer.array();
        mDrawOnTop.mYUVData = array;
		System.arraycopy(array, 0, mDrawOnTop.mYUVData, 0, array.length);
		mDrawOnTop.invalidate();
        imView.addView(mDrawOnTop);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.save:
            boolean sdCardAvailable = SaveToFile.checkExternalMedia();
            if (!sdCardAvailable) {
                Toast.makeText(getApplicationContext(), "Cannot save image. SD Card not found ", Toast.LENGTH_LONG).show();
            } else {
            	savedImagePath = SaveToFile.saveBitmap(mDrawOnTop.mBitmap);
                Toast.makeText(getApplicationContext(), "Saving " + savedImagePath, Toast.LENGTH_LONG).show();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            }
            Intent saveImage = new Intent(ActivityEqualizeHistogram.this, MainImageDisplay.class);
	    	saveImage.putExtra("file_path",savedImagePath);
            startActivity(saveImage);
            return true;
    	case R.id.discard:
            Toast.makeText(getApplicationContext(), "Discard selected", Toast.LENGTH_LONG).show();
            Intent discardImage = new Intent(ActivityEqualizeHistogram.this, MainImageDisplay.class);
	        discardImage.putExtra("file_path",receivedImagePath);
            startActivity(discardImage);
            return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    } 
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.filtered_view_menu, menu);   
		return super.onCreateOptionsMenu(menu);
	}
   
    public class DrawOnView extends View {
    	Bitmap mBitmap;
    	Paint mPaintBlack;
    	Paint mPaintYellow;
    	byte[] mYUVData;
    	int[] mRGBData;
    	int mImageWidth, mImageHeight;
    	int[] mGrayHistogram;
    	double[] mGrayCDF;
    	int mState;
    	
    	static final int STATE_ORIGINAL = 0;
    	static final int STATE_PROCESSED = 1;

        public DrawOnView(Context context) {
            super(context);
            
            mPaintBlack = new Paint();
            mPaintBlack.setStyle(Paint.Style.FILL);
            mPaintBlack.setColor(Color.BLACK);
            mPaintBlack.setTextSize(25);

            mBitmap = null;
            mYUVData = null;
            mRGBData = null;
            mGrayHistogram = new int[256];
            mGrayCDF = new double[256];
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (mBitmap != null)
            {
            	int canvasWidth = canvas.getWidth();
            	int canvasHeight = canvas.getHeight();
            	int newImageWidth = 640;
            	int newImageHeight = 480;
            	int marginWidth = (canvasWidth - newImageWidth)/2;
            	        	
            	// Convert from YUV to RGB
//            	if (mState == STATE_ORIGINAL)
            		decodeYUV420RGB(mRGBData, mYUVData, mImageWidth, mImageHeight);
//            	else
//            		decodeYUV420RGBContrastEnhance(mRGBData, mYUVData, mImageWidth, mImageHeight);
            	
            	// Draw bitmap
            	mBitmap.setPixels(mRGBData, 0, mImageWidth, 0, 0, mImageWidth, mImageHeight);
            	Rect src = new Rect(0, 0, mImageWidth, mImageHeight);
            	Rect dst = new Rect(marginWidth, 0, canvasWidth-marginWidth, canvasHeight);
             	canvas.drawBitmap(mBitmap, src, dst, mPaintBlack);
            	
            	// Draw black borders        	        	
            	canvas.drawRect(0, 0, marginWidth, canvasHeight, mPaintBlack);
            	canvas.drawRect(canvasWidth - marginWidth, 0, 
            			canvasWidth, canvasHeight, mPaintBlack);
            	
            } // end if statement
            super.onDraw(canvas);
            
        } // end onDraw method

        private void decodeYUV420RGB(int[] rgb, byte[] yuv420sp, int width, int height) {
        	// Convert YUV to RGB
        	final int frameSize = width * height;
        	for (int j = 0, yp = 0; j < height; j++) {
        		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
        		for (int i = 0; i < width; i++, yp++) {
        			int y = (0xff & ((int) yuv420sp[yp])) - 16;
        			if (y < 0) y = 0;
        			if ((i & 1) == 0) {
        				v = (0xff & yuv420sp[uvp++]) - 128;
        				u = (0xff & yuv420sp[uvp++]) - 128;
        			}
        			
        			int y1192 = 1192 * y;
        			int r = (y1192 + 1634 * v);
        			int g = (y1192 - 833 * v - 400 * u);
        			int b = (y1192 + 2066 * u);
        			
        			if (r < 0) r = 0; else if (r > 262143) r = 262143;
        			if (g < 0) g = 0; else if (g > 262143) g = 262143;
        			if (b < 0) b = 0; else if (b > 262143) b = 262143;
        			
        			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
        		}
        	}
        }
        
        private void decodeYUV420RGBContrastEnhance(int[] rgb, byte[] yuv420sp, int width, int height) {
        	// Compute histogram for Y
        	final int frameSize = width * height;
        	int clipLimit = frameSize / 10;
        	for (int bin = 0; bin < 256; bin++)
        		mGrayHistogram[bin] = 0;
        	for (int j = 0, yp = 0; j < height; j++) {
        		for (int i = 0; i < width; i++, yp++) {
        			int y = (0xff & ((int) yuv420sp[yp])) - 16;
        			if (y < 0) y = 0;
        			if (mGrayHistogram[y] < clipLimit)
        				mGrayHistogram[y]++;
        		}
        	}
        	double sumCDF = 0;
        	for (int bin = 0; bin < 256; bin++)
        	{
        		sumCDF += (double)mGrayHistogram[bin]/(double)frameSize;
        		mGrayCDF[bin] = sumCDF;
        	}
        	
        	// Convert YUV to RGB
        	for (int j = 0, yp = 0; j < height; j++) {
        		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
        		for (int i = 0; i < width; i++, yp++) {
        			int y = (0xff & ((int) yuv420sp[yp])) - 16;
        			if (y < 0) y = 0;
        			if ((i & 1) == 0) {
        				v = (0xff & yuv420sp[uvp++]) - 128;
        				u = (0xff & yuv420sp[uvp++]) - 128;
        			}
        			y = (int)(mGrayCDF[y]*255 + 0.5);
        			
        			int y1192 = 1192 * y;
        			int r = (y1192 + 1634 * v);
        			int g = (y1192 - 833 * v - 400 * u);
        			int b = (y1192 + 2066 * u);
        			
        			if (r < 0) r = 0; else if (r > 262143) r = 262143;
        			if (g < 0) g = 0; else if (g > 262143) g = 262143;
        			if (b < 0) b = 0; else if (b > 262143) b = 262143;
        			
        			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
        		}
        	}
        }
    }
}    

