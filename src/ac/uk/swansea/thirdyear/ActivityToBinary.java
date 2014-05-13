/****************************************************************************************/
/* This thresholds a grayscale image and converts it to only black and white:            */
/* to Binary method adapted from:                                                        */
/*  http://stackoverflow.com/questions/20299264/android-convert-grayscale-to-binary-image*/
/*****************************************************************************************/

package ac.uk.swansea.thirdyear;

	import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
	 
	public class ActivityToBinary extends Activity {
	    private ImageView imViewAndroid;
	    public String receivedImagePath = "";
	    public String savedImagePath = ""; 
	    private Bitmap binaryImage = null;
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_filter);
	        receivedImagePath = getIntent().getExtras().getString("file_path");
	        imViewAndroid = (ImageView) findViewById(R.id.filtered_view);
	        binaryImage = toBinary(ActivityGrayScale.grayScaleImage(MainImageDisplay.compressImage(receivedImagePath,300,300)));
	        imViewAndroid.setImageBitmap(binaryImage);
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch (item.getItemId()) {
	    	case R.id.save:
	            boolean sdCardAvailable = SaveToFile.checkExternalMedia();
	            if (!sdCardAvailable) {
	                Toast.makeText(getApplicationContext(), "Cannot save image. SD Card not found ", Toast.LENGTH_LONG).show();
	            } else {
	            	savedImagePath = SaveToFile.saveBitmap(binaryImage);
	                Toast.makeText(getApplicationContext(), "Saving " + savedImagePath, Toast.LENGTH_SHORT).show();
	                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
	            }
	            Intent saveImage = new Intent(ActivityToBinary.this, MainImageDisplay.class);
		    	saveImage.putExtra("file_path",savedImagePath);
	            startActivity(saveImage);
	            finish();
	            return true;
	    	case R.id.discard:
	            Toast.makeText(getApplicationContext(), "Image discarded", Toast.LENGTH_SHORT).show();
	            Intent discardImage = new Intent(ActivityToBinary.this, MainImageDisplay.class);
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

//converts a grayscale image to binary	    
	    public Bitmap toBinary(Bitmap bmp) {
	        int width, height, threshold;
	        height = bmp.getHeight();
	        width = bmp.getWidth();
	        threshold = 127;
	        Bitmap myBinary = Bitmap.createBitmap(bmp);

	        for(int x = 0; x < width; ++x) {
	            for(int y = 0; y < height; ++y) {
	                int pixel = bmp.getPixel(x, y);
	                int red = Color.red(pixel);
	                if(red < threshold){
	                    myBinary.setPixel(x, y, 0xFF000000);
	                } else{
	                    myBinary.setPixel(x, y, 0xFFFFFFFF);
	                }
	            }
	        }
	        return myBinary;
	    }
	}	    