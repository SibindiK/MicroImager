/**********************************************************************************/
/* Method to check whether external media available and writable. This is
/* adapted from  http://developer.android.com/guide/topics/data/data-storage.html
/* #filesExternal                                                         
/***********************************************************************************/

package ac.uk.swansea.thirdyear;

import java.io.*;
import java.util.Random;

import android.graphics.Bitmap;
import android.os.Environment;

public class SaveToFile {

	public static boolean checkExternalMedia() {
	    boolean mExternalStorageAvailable = false;
	    boolean mExternalStorageWriteable = false;
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        // Can read and write the media
	       return mExternalStorageAvailable = mExternalStorageWriteable = true;
	    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        // Can only read the media
	        mExternalStorageAvailable = true;
	        return mExternalStorageWriteable = false;
	    } else {
	        // Can't read or write
	      return  mExternalStorageAvailable = mExternalStorageWriteable = false;
	    }
	}

    public static String saveBitmap(Bitmap finalBitmap) {
    
        
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/micro_images");    
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete (); 
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (root + "/micro_images/" + fname);
    }
}
