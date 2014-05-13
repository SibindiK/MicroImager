/*********************************************************************************/
/* This activity displays the image and is the parent activity from which all the*/
/* processing menu options are called                                            */
/*                                                                               */
/*********************************************************************************/
package ac.uk.swansea.thirdyear;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class MainImageDisplay extends Activity {

	private Bitmap currentImage = null;
	private String receivedImagePath = "";
	public String imagePath = "";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gallery);
    	ActionBar actionBar = getActionBar();
    	actionBar.setDisplayUseLogoEnabled(false);
    	actionBar.setDisplayHomeAsUpEnabled(true);

        receivedImagePath = getIntent().getExtras().getString("file_path");
		ImageView imageView = (ImageView) findViewById(R.id.picture_view);
		imageView.setImageBitmap(compressImage(receivedImagePath,300,300));
	    currentImage = BitmapFactory.decodeFile(receivedImagePath); 
	    imagePath = receivedImagePath;
    }
    
//compresses the image to be processed
    public static Bitmap compressImage (String filePath, int width, int height){   

    	int newHeight=width;
        int newWidth=height;
            
        BitmapFactory.Options options = new BitmapFactory.Options();   
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = compressionRatio(options, newWidth, newHeight);
        options.inJustDecodeBounds = false;        
        return BitmapFactory.decodeFile(filePath, options); 
    }

    private static int compressionRatio(BitmapFactory.Options options, int newWidth, int newHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int newRatio = 1;

        if (height > newHeight || width > newWidth) {
            final int heightRatio = Math.round((float) height / (float) newHeight);
            final int widthRatio = Math.round((float) width / (float) newWidth);

            newRatio = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return newRatio;
    }

//calls the different menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case (android.R.id.home):
    		Intent toHome = new Intent(MainImageDisplay.this, MainMenu.class);
    	    toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	    startActivity(toHome);
    	    return true;
    	case R.id.grayscale:
            Toast.makeText(getApplicationContext(), "GrayScale " + imagePath, Toast.LENGTH_SHORT).show();
            Intent grayScaleImage = new Intent(MainImageDisplay.this, ActivityGrayScale.class);
	    	grayScaleImage.putExtra("file_path",imagePath);
            startActivity(grayScaleImage);
            return true;
       	case R.id.binary:
            Intent changeToBinary = new Intent(MainImageDisplay.this, ActivityToBinary.class);
        	changeToBinary.putExtra("file_path",imagePath);
            startActivity(changeToBinary);
    		return true;
    	case R.id.sharpen:
            Intent sharpenImage = new Intent(MainImageDisplay.this, ActivitySharpen.class);
	    	sharpenImage.putExtra("file_path",imagePath);
            startActivity(sharpenImage);
            return true;
    	case R.id.emboss:
            Intent embossImage = new Intent(MainImageDisplay.this, ActivityEmboss.class);
	    	embossImage.putExtra("file_path",imagePath);
            startActivity(embossImage);
            return true;
    	case R.id.smooth:
            Intent smoothenImage = new Intent(MainImageDisplay.this, ActivitySmooth.class);
	    	smoothenImage.putExtra("file_path",imagePath);
            startActivity(smoothenImage);
    		return true;
    	case R.id.color_histogram:
            Intent displayRGBHistogram = new Intent(MainImageDisplay.this, ActivityColorHistogram.class);
            displayRGBHistogram.putExtra("file_path",imagePath);
            Toast.makeText(getApplicationContext(), "Histogram selected "+ imagePath, Toast.LENGTH_SHORT).show();
            startActivity(displayRGBHistogram);
            finish();
            return true;
    	case R.id.histogram:
            Intent displayHistogram = new Intent(MainImageDisplay.this, ActivityHistogram.class);
	    	displayHistogram.putExtra("file_path",imagePath);
            startActivity(displayHistogram);
            finish();
    		return true;
   	case R.id.edge_detect:
            Intent detectEdges = new Intent(MainImageDisplay.this, ActivityEdgeDetect.class);
	    	detectEdges.putExtra("file_path",imagePath);
            startActivity(detectEdges);
    		return true;
   	case R.id.contrast:
        Intent enhanceContrast = new Intent(MainImageDisplay.this, ActivityEnhanceContrast.class);
    	enhanceContrast.putExtra("file_path",imagePath);
        startActivity(enhanceContrast);
		return true;
   	case R.id.equalize:
        Intent histogramEqual = new Intent(MainImageDisplay.this, ActivityEqualizeHistogram.class);
    	histogramEqual.putExtra("file_path",imagePath);
        startActivity(histogramEqual);
		return true;
   	case R.id.save:
            boolean sdCardAvailable = SaveToFile.checkExternalMedia();
            if (!sdCardAvailable) {
                Toast.makeText(getApplicationContext(), "Cannot save image. SD Card not found ", Toast.LENGTH_SHORT).show();
            } else {
            	imagePath = SaveToFile.saveBitmap(currentImage);
                Toast.makeText(getApplicationContext(), "Saving " + imagePath, Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            }
            Intent saveImage = new Intent(MainImageDisplay.this, MainMenu.class);
            startActivity(saveImage);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.open_gallery, menu);   
		return super.onCreateOptionsMenu(menu);
	}
    
}