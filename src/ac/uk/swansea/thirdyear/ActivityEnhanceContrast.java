/**********************************************************************************/
/* This activity sharpens the image. Uses a method contrastImage that was adapted */
/* from:                                                                           */
/*http://xjaphx.wordpress.com/2011/06/21/image-processing-contrast-image-on-the-fly*/
/*                                                                                 */
/***********************************************************************************/

package ac.uk.swansea.thirdyear;

	import android.net.Uri;
	import android.os.Bundle;
	import android.os.Environment;
	import android.app.Activity;
	import android.content.Intent;
	import android.graphics.Bitmap;
	import android.graphics.Color;
	import android.view.Menu;
	import android.view.MenuInflater;
	import android.view.MenuItem;
	import android.widget.ImageView;
	import android.widget.Toast;
	 
	public class ActivityEnhanceContrast extends Activity {

		private ImageView imViewAndroid;
	    public String receivedImagePath = "";
	    public String savedImagePath = ""; 
	    private Bitmap contrastImage = null;
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        
	    	super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_filter);
	        receivedImagePath = getIntent().getExtras().getString("file_path");
	        imViewAndroid = (ImageView) findViewById(R.id.filtered_view);
	        contrastImage = contrastImage(MainImageDisplay.compressImage(receivedImagePath,300,300),20);
	        imViewAndroid.setImageBitmap(contrastImage);
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	
	    	switch (item.getItemId()) {
	    	case R.id.save:
	            boolean sdCardAvailable = SaveToFile.checkExternalMedia();
	            if (!sdCardAvailable) {
	                Toast.makeText(getApplicationContext(), "Cannot save image. SD Card not found ", Toast.LENGTH_LONG).show();
	            } else {
	            	savedImagePath = SaveToFile.saveBitmap(contrastImage);
	                Toast.makeText(getApplicationContext(), "Saving " + savedImagePath, Toast.LENGTH_SHORT).show();
	                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
	            }
	            Intent saveImage = new Intent(ActivityEnhanceContrast.this, MainImageDisplay.class);
		    	saveImage.putExtra("file_path",savedImagePath);
	            startActivity(saveImage);
	            finish();
	            return true;
	    	case R.id.discard:
	            Toast.makeText(getApplicationContext(), "Image Discarded", Toast.LENGTH_SHORT).show();
	            Intent discardImage = new Intent(ActivityEnhanceContrast.this, MainImageDisplay.class);
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
	   

//creates the contrast in the image. please see top of the page for source of method.	    
	    public static Bitmap contrastImage(Bitmap bmp, double level) {

	        int A, R, G, B;
	        int pix;
	        double value = Math.pow((100 + level) / 100, 2);

	        int width = bmp.getWidth();
	        int height = bmp.getHeight();
	        Bitmap imageOut = Bitmap.createBitmap(width, height, bmp.getConfig());
	     
	        for(int x = 0; x < width; ++x) {
	        	
	            for(int y = 0; y < height; ++y) {
	                pix = bmp.getPixel(x, y);
	                A = Color.alpha(pix);
	                
	                R = Color.red(pix);
	                R = (int)(((((R / 255.0) - 0.5) * value) + 0.5) * 255.0);
	                if(R < 0) { R = 0; }
	                else if(R > 255) { R = 255; }
	     
	                G = Color.green(pix);
	                G = (int)(((((G / 255.0) - 0.5) * value) + 0.5) * 255.0);
	                if(G < 0) { G = 0; }
	                else if(G > 255) { G = 255; }
	     
	                B = Color.blue(pix);
	                B = (int)(((((B / 255.0) - 0.5) * value) + 0.5) * 255.0);
	                if(B < 0) { B = 0; }
	                else if(B > 255) { B = 255; }
	     
	                imageOut.setPixel(x, y, Color.argb(A, R, G, B));
	            }
	        }
	     
	        return imageOut;
	    }
	}	    