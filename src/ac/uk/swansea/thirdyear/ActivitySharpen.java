/*********************************************************************************/
/* This activity sharpens the image. Uses a sharpening filter that was obtained  */
/* from                                                                          */
/* http://xjaphx.wordpress.com/2011/06/22/image-processing-sharpening-image/     */
/*********************************************************************************/

package ac.uk.swansea.thirdyear;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ActivitySharpen extends Activity {
    Bitmap myBitmap;
    Bitmap sharpImage;
    ImageView myView;
    Boolean myImageFiltered;
    String receivedImagePath ="";
    String savedImagePath ="";
    ProgressBar progress;
    Handler handler;
    Thread pThread;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        progress = (ProgressBar) findViewById(R.id.filter_progress); 
        
        /*       handler = new Handler() {
        	@Override
        	public void handleMessage(Message msg) {
        		myView.setImageBitmap(sharpImage);
        		pDialog.dismiss();
        	}
        };
 */           receivedImagePath = getIntent().getExtras().getString("file_path");
    		// Load the image from file
			myBitmap = null;
        	//myBitmap = BitmapFactory.decodeFile(receivedImagePath);
            myBitmap = MainImageDisplay.compressImage(receivedImagePath,300,300);
    		// Display the image in the image viewer
        	myView = (ImageView)findViewById(R.id.filtered_view);
        	if (myView != null)
        	{
        		Runnable runnable = new Runnable() {
        		    @Override
        		    public void run() {
        			    for (int i = 0; i <= 10; i++) {
        			    	final int value = i;
        			        sharpImage = sharpenImage(myBitmap,11);
        			        progress.post(new Runnable() {
        			        	@Override
           	        		    public void run() {
                                    progress.setProgress(value);
        			        	}
        			        });	
        			    }
        		    }    
        		};
        		new Thread(runnable).start();
    		    myView.setImageBitmap(sharpImage);
        	}
        		
        	if (myView == null) {
                Toast.makeText(getApplicationContext(), "Bitmap not loaded", Toast.LENGTH_LONG).show();
        	}
        	 
    }	


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.save:
            boolean sdCardAvailable = SaveToFile.checkExternalMedia();
            if (!sdCardAvailable) {
                Toast.makeText(getApplicationContext(), "Cannot save image. SD Card not found ", Toast.LENGTH_SHORT).show();
            } else {
            	savedImagePath = SaveToFile.saveBitmap(sharpImage);
                Toast.makeText(getApplicationContext(), "Saving " + savedImagePath, Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            }
            Intent saveImage = new Intent(ActivitySharpen.this, MainImageDisplay.class);
	    	saveImage.putExtra("file_path",savedImagePath);
            startActivity(saveImage);
            finish();
            return true;
    	case R.id.discard:
            Toast.makeText(getApplicationContext(), "Image Cancelled", Toast.LENGTH_SHORT).show();
            Intent discardImage = new Intent(ActivitySharpen.this, MainImageDisplay.class);
	        discardImage.putExtra("file_path",receivedImagePath);
            startActivity(discardImage);
            finish();
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

//applies the convolution. used as is from the source.    
    public static Bitmap sharpenImage(Bitmap src, double weight) {
        double[][] SharpConfig = new double[][] {
            { -1 , -1 ,-1  },
            { -1, 12, -1 },
            { -1 , -1  , -1  }
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(SharpConfig);
        convMatrix.Factor = weight - 8;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }
 }
