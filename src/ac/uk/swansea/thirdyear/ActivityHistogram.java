/********************************************************************************/
/* This activity sharpens the image. Uses a sharpening filter that was obtained */
/* from                                                                          */
/*                                                                               */
/*********************************************************************************/

package ac.uk.swansea.thirdyear;

import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


	public class ActivityHistogram extends Activity{
	
	    Bitmap myBitmap;
     
	    LinearLayout view_color;
        String receivedImagePath ="";
	    boolean flag;

	    private int SIZE = 256;
	    // Red, Green, Blue
	    private int NUMBER_OF_COLOURS = 3;

	    public final int RED = 0;
	    public final int GREEN = 1;
	    public final int BLUE = 2;

	    private int[][] colourBins;
 	    private volatile boolean loaded = false;
	    private int maxY;

	    float offset = 1;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        // TODO Auto-generated method stub
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_histogram);

	        DisplayMetrics metrics = new DisplayMetrics();    
	        getWindowManager().getDefaultDisplay().getMetrics(metrics);    

	        if(metrics.densityDpi==metrics.DENSITY_LOW)
	            offset = 0.75f;
	        else if(metrics.densityDpi==metrics.DENSITY_MEDIUM)
	            offset = 1f;
	        else if(metrics.densityDpi==metrics.DENSITY_TV)
	            offset = 1.33f;
	        else if(metrics.densityDpi==metrics.DENSITY_HIGH)
	            offset = 1.5f;
	        else if(metrics.densityDpi==metrics.DENSITY_XHIGH)
	            offset = 2f;


	        colourBins = new int[NUMBER_OF_COLOURS][];

	        for (int i = 0; i < NUMBER_OF_COLOURS; i++) {
	            colourBins[i] = new int[SIZE];
	        }

	        receivedImagePath = getIntent().getExtras().getString("file_path");

	    		// Load the image from file
				myBitmap = null;
	            myBitmap = MainImageDisplay.compressImage(receivedImagePath,300,300);
				//Toast.makeText(getApplicationContext(), "bitmap received " + receivedImagePath, Toast.LENGTH_LONG).show();
                if(myBitmap!=null)
                {
                    try {
                        new MyAsync().execute();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }   
	
	        LinearLayout histogramView = (LinearLayout) findViewById(R.id.histo_layout);
	        histogramView.setVisibility(View.VISIBLE);
	        histogramView.addView(new MyHistogram(getApplicationContext(), myBitmap));
            }
        
	    

	    class MyAsync extends AsyncTask
	    {
	        @Override
	        protected void onPreExecute() {
	            // TODO Auto-generated method stub
	            super.onPreExecute();
	            showDialog(0);
	        }

	        @Override
	        protected Object doInBackground(Object... params) {
	            // TODO Auto-generated method stub

	            try {
	                load(myBitmap);
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }

	            return null;
	        }

	        @Override
	        protected void onPostExecute(Object result) {
	            // TODO Auto-generated method stub
	            super.onPostExecute(result);
		          ImageView view = (ImageView) findViewById(R.id.histo_source_image);
		          view.setVisibility(View.VISIBLE);
	              view.setImageBitmap(myBitmap);
	            dismissDialog(0);
	        }
	    }

	    public void load(Bitmap bi) throws IOException {

	        if (bi != null) {
	            // Reset all the bins
	            for (int i = 0; i < NUMBER_OF_COLOURS; i++) {
	                for (int j = 0; j < SIZE; j++) {
	                    colourBins[i][j] = 0;
	                }
	            }

	            for (int x = 0; x < bi.getWidth(); x++) {
	                for (int y = 0; y < bi.getHeight(); y++) {

	                    int pixel = bi.getPixel(x, y);

	                    colourBins[RED][Color.red(pixel)]++;
	                    colourBins[GREEN][Color.green(pixel)]++;
	                    colourBins[BLUE][Color.blue(pixel)]++;
	                }
	            }

	            maxY = 0;

	            for (int i = 0; i < NUMBER_OF_COLOURS; i++) {
	                for (int j = 0; j < SIZE; j++) {
	                    if (maxY < colourBins[i][j]) {
	                        maxY = colourBins[i][j];
	                    }
	                }
	            }
	            loaded = true;
	        } else {
	            loaded = false;
	        }
	    }

	    
	    class MyHistogram extends View {

	        public MyHistogram(Context context, Bitmap bi) {
	            super(context);

	        }

	        @Override
	        protected void onDraw(Canvas canvas) {
	            // TODO Auto-generated method stub

	            if (loaded) {
	                canvas.drawColor(Color.GRAY);

	                //Log.e("NIRAV", "Height : " + getHeight() + ", Width : "
	                //        + getWidth());

	                int xInterval = (int) ((double) getWidth() / ((double) SIZE + 1));

	                for (int i = 0; i < NUMBER_OF_COLOURS; i++) {

	                    Paint wallpaint;

	                    wallpaint = new Paint();
	                    wallpaint.setColor(Color.WHITE);
	                    wallpaint.setStyle(Style.FILL);

	                    Path wallpath = new Path();
	                    wallpath.reset();
	                    wallpath.moveTo(0, getHeight());
	                    for (int j = 0; j < SIZE - 1; j++) {
	                        int value = (int) (((double) colourBins[i][j] / (double) maxY) * (getHeight()+100));
	                             wallpath.lineTo(j * xInterval * offset, getHeight() - value);
	                    }
	                    wallpath.lineTo(SIZE * offset, getHeight());
	                    canvas.drawPath(wallpath, wallpaint);
	                }
	            }	            

                super.onDraw(canvas);
	        }
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