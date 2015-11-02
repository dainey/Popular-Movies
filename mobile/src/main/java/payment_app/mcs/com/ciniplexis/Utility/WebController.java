package payment_app.mcs.com.ciniplexis.Utility;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ogayle on 28/10/2015.
 */
public class WebController {

    private HttpURLConnection urlConnection;
    private URL url;
    public static final String URL_ENCODE = "application/x-www-url-form-urlencoded;charset =UTF-8";
    public static final String JSON_FORMAT = "application/json;charset =UTF-8";

    public WebController() {

    }


    public String getRequest(String url) {

        String response = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.addRequestProperty("Content-Type", JSON_FORMAT);


            int rc = urlConnection.getResponseCode();

            if (rc >= 200 && rc < 300) {
                InputStream iStream = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader bReader = new BufferedReader(new InputStreamReader(iStream, "UTF-8"));

                response = bReader.readLine();

            }


        } catch (MalformedURLException m) {

            Log.e("getRequest", m.getMessage());
        } catch (IOException io) {
            Log.e("getRequest", io.getMessage());
        }
        return response;
    }


    public void postRequest(String url) {

        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("POST");

            urlConnection.addRequestProperty("Content-Type", JSON_FORMAT);

            OutputStream outStream = urlConnection.getOutputStream();

            if (outStream == null)
                return;


        } catch (MalformedURLException m) {

        } catch (IOException io) {

        }

    }
}
