/********************************************************************************/
/* This activity sharpens the image. Uses an embossing filter that was obtained */
/* from                                                                          */
/*                                                                               */
/*********************************************************************************/

package ac.uk.swansea.thirdyear;

	import android.app.Activity;
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

	public class ActivityEmboss extends Activity {
	    Bitmap myBitmap;
	    Bitmap embossImage;
	    ImageView myView;
	    Boolean myImageFiltered;
	    String receivedImagePath ="";
	    String savedImagePath ="";

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
	        		embossImage = toEmbossImage(myBitmap);
	        		myView.setImageBitmap(embossImage);
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
	            	savedImagePath = SaveToFile.saveBitmap(embossImage);
	                Toast.makeText(getApplicationContext(), "Saving " + savedImagePath, Toast.LENGTH_SHORT).show();
	                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
	            }
	            Intent saveImage = new Intent(ActivityEmboss.this, MainImageDisplay.class);
		    	saveImage.putExtra("file_path",savedImagePath);
	            startActivity(saveImage);
	            finish();
	            return true;
	    	case R.id.discard:
	            Toast.makeText(getApplicationContext(), "Image discarded", Toast.LENGTH_SHORT).show();
	            Intent discardImage = new Intent(ActivityEmboss.this, MainImageDisplay.class);
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


	    public static Bitmap toEmbossImage(Bitmap src) {
	        double[][] SharpConfig = new double[][] {
	            { -1 , 0 , -1 },
	            { 0 , 4 , 0 },
	            { -1 , 0 , -1 }
	        };
	        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
	        convMatrix.applyConfig(SharpConfig);
	        convMatrix.Factor = 1;
	        convMatrix.Offset = 127;
	        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	    }
	 }
