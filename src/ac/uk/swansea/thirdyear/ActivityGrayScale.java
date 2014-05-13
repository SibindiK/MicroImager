/********************************************************************************************/
/* This activity converts a colour image to grayscale                                        */
/* adapted from:                                                                             */
/* http://shaikhhamadali.blogspot.co.uk/2013/06/gray-scale-image-in-imageview-android_29.html*/
/*********************************************************************************************/


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
 
public class ActivityGrayScale extends Activity {
    private ImageView view;
    public String receivedImagePath = "";
    public String savedImagePath = ""; 
    private Bitmap grayScaledImage = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        receivedImagePath = getIntent().getExtras().getString("file_path");
        view = (ImageView) findViewById(R.id.filtered_view);
        //grayScaledImage = grayScaleImage(BitmapFactory.decodeFile(receivedImagePath));
        grayScaledImage = grayScaleImage(MainImageDisplay.compressImage(receivedImagePath,300,300));
        view.setImageBitmap(grayScaledImage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.save:
            boolean sdCardAvailable = SaveToFile.checkExternalMedia();
            if (!sdCardAvailable) {
                Toast.makeText(getApplicationContext(), "Cannot save image. SD Card not found ", Toast.LENGTH_LONG).show();
            } else {
            	savedImagePath = SaveToFile.saveBitmap(grayScaledImage);
                Toast.makeText(getApplicationContext(), "Saving " + savedImagePath, Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            }
            Intent saveImage = new Intent(ActivityGrayScale.this, MainImageDisplay.class);
	    	saveImage.putExtra("file_path",savedImagePath);
            startActivity(saveImage);
            finish();
            return true;
    	case R.id.discard:
            Toast.makeText(getApplicationContext(), "Image discarded", Toast.LENGTH_SHORT).show();
            Intent discardImage = new Intent(ActivityGrayScale.this, MainImageDisplay.class);
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
   
    public static Bitmap grayScaleImage(Bitmap bmp) {
 
    	final double RED = 0.212;
        final double GREEN = 0.715;
        final double BLUE = 0.0721;
       
        int A, red, green, blue;
        int pixel;
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap grayImage = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
 
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                pixel = bmp.getPixel(x, y);
                A = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                red = green = blue = (int)(RED * red + GREEN * green + BLUE * blue);
                grayImage.setPixel(x, y, Color.argb(A, red, green, blue));
            }
        }
        return grayImage;
    }
 }