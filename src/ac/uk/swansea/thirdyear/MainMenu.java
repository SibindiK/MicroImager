/******************************************************************************/
/* This activity creates the application's home screen and main menu an image.*/
/* This project was developed by Kermit Sibindi as a third year project       */
/* at Swansea University in the College of Engineering.                       */
/* Code adapted from http://developer.android.com/training/index.html         */                                                                              
/******************************************************************************/

package ac.uk.swansea.thirdyear;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;

public class MainMenu extends Activity implements OnClickListener{
    
	int requestCode;
	private static int RESULT_LOAD_IMAGE = 1;
	public String imagePath ="";
	public Bitmap currentImage = null;
	private Boolean loadOk = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_menu);
		
		View aboutButton = findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);
		
		View cameraButton = findViewById(R.id.camera_button);
		cameraButton.setOnClickListener(this);
		
		View openGalleryButton = findViewById(R.id.gallery_button);
		openGalleryButton.setOnClickListener(this);
		
	}
	
	public void onClick(View thisView) {
		switch (thisView.getId()) {
		    case R.id.about_button:
		    	Intent showDescription = new Intent(this, About.class);
		    	startActivity(showDescription);
		    	break;
		    
		    case R.id.camera_button:
		    	Intent activateCamera = new Intent( android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    	startActivityForResult( activateCamera, requestCode);
		    	break;
		    	
		    case R.id.gallery_button:
	            Intent openImagesFolder = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(openImagesFolder, RESULT_LOAD_IMAGE);
			    break;
               	
		}
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
            loadOk = true;
			cursor.close();
			imagePath = picturePath;
            if (loadOk) {
	    	    Intent showImage = new Intent(this, MainImageDisplay.class);
		        showImage.putExtra("file_path",imagePath);
			    startActivity(showImage);
            }
		}
    
    }
}
