// implements a sobel edge detector by calling on the convolution class.
package ac.uk.swansea.thirdyear;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class ActivityEdgeDetect extends Activity {
    Bitmap myBitmap;
    Bitmap edgeDetectedImage;
    ImageView myView;
    Boolean myImageFiltered;
    String receivedImagePath ="";
    String savedImagePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        
        receivedImagePath = getIntent().getExtras().getString("file_path");

    		// Load the image from file
			myBitmap = null;
        	//myBitmap = BitmapFactory.decodeFile(receivedImagePath);
            myBitmap = MainImageDisplay.compressImage(receivedImagePath,300,300);

    		// Display the image in the image viewer
        	myView = (ImageView)findViewById(R.id.filtered_view);
        	if (myView != null)
        	{
                edgeDetectedImage = edgeDetect(myBitmap);
        		myView.setImageBitmap(edgeDetectedImage);
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
                Toast.makeText(getApplicationContext(), "Cannot save image. SD Card not found ", Toast.LENGTH_LONG).show();
            } else {
            	savedImagePath = SaveToFile.saveBitmap(edgeDetectedImage);
                Toast.makeText(getApplicationContext(), "Saving " + savedImagePath, Toast.LENGTH_LONG).show();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            }
            Intent saveImage = new Intent(ActivityEdgeDetect.this, MainImageDisplay.class);
	    	saveImage.putExtra("file_path",savedImagePath);
            startActivity(saveImage);
            finish();
            return true;
    	case R.id.discard:
            Toast.makeText(getApplicationContext(), "Discard selected", Toast.LENGTH_LONG).show();
            Intent discardImage = new Intent(ActivityEdgeDetect.this, MainImageDisplay.class);
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


    public static Bitmap edgeDetect(Bitmap src) {
        double[][] XConfig = new double[][] {
            { -1 , 0 , 1 },
            { -2 , 0 , 2 },
            { -1 , 0 , 1 }
        };

        double[][] YConfig = new double[][] {
                { 1 , 2 , 1 },
                { 0 , 0 , 0 },
                { -1 , -2 , -1 }
            };

        ConvolutionMatrix XMatrix = new ConvolutionMatrix(3);
        ConvolutionMatrix YMatrix = new ConvolutionMatrix(3);
        XMatrix.applyConfig(XConfig); XMatrix.Factor = 0; XMatrix.Offset = 20;
        YMatrix.applyConfig(YConfig); YMatrix.Factor = 1; YMatrix.Offset = 127;
        return ConvolutionMatrix.computeConvolution3x3((ConvolutionMatrix.computeConvolution3x3(src, XMatrix)), YMatrix);
    }
    

    @Override
     protected Dialog onCreateDialog(int id) {
            ProgressDialog dataLoadProgress = new ProgressDialog(this);
            dataLoadProgress.setMessage("Loading...");
            dataLoadProgress.setIndeterminate(true);
            dataLoadProgress.setCancelable(false);
            dataLoadProgress.setProgressStyle(android.R.attr.progressBarStyleLarge);
            return dataLoadProgress;

        }
 }
