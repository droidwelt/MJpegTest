package droidwelt.ru.mjpegtest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;

public class MainActivity extends AppCompatActivity {



    private static final String TAG = "MJPEGTEST-MainActivity";
    private static String URL = "http://94.72.4.191:80/mjpg/video.mjpg";
    private MjpegView mv;
    private static final int MENU_QUIT = 1;
    private static final int MENU_SETTING = 2;
    private static final int MENU_SPB1 = 11;
    private static final int MENU_SPB2 = 12;
    private static final int MENU_USA = 13;
    private static final int MENU_Greece = 14;
    private static final int MENU_Iceland = 15;


    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_QUIT, 0, "Quit");
        menu.add(0, MENU_SPB1, 1, "Saint-Peterburg 1");
        menu.add(0, MENU_SPB2, 1, "Saint-Peterburg 2");
        menu.add(0, MENU_USA, 1, "USA");
        menu.add(0, MENU_Greece, 1, "Greece");
        menu.add(0, MENU_Iceland, 1, "Iceland, Reykjavik");
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_QUIT:
                finish();
                return true;

            case MENU_SPB1:
                viewURL("http://94.72.4.191:80/mjpg/video.mjpg");
                return true;

            case MENU_SPB2:
                viewURL("http://178.162.34.110:82/cam_1.cgi");
                return true;

            case MENU_USA:
                viewURL("http://207.192.232.2:8000/mjpg/video.mjpg");
                return true;

            case MENU_Greece:
                viewURL("http://195.97.20.246:80/mjpg/video.mjpg");
                return true;

            case MENU_Iceland:
                viewURL("http://157.157.138.235:80/mjpg/video.mjpg");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void viewURL(String u) {
        URL = u;
        if (mv !=null)
            mv.stopPlayback();
        mv = new MjpegView(this);
        setContentView(mv);
        new DoRead().execute(u);
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        viewURL(URL);
    }

    public void onPause() {
        super.onPause();
        mv.stopPlayback();
    }


    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {

            HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if (res.getStatusLine().getStatusCode() == 401) {
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-ClientProtocolException", e);

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-IOException", e);
            }

            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            if (mv != null) {
                mv.setSource(result);
                mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
                mv.showFps(true);
            }
        }
    }


}
