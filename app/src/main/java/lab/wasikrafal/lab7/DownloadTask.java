package lab.wasikrafal.lab7;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownloadTask extends AsyncTask<Void,Void,Boolean>
{
    private Response delegate;
    private List<String> response = null;
    private String key;
    private String url;

    public DownloadTask (Response del, String u, String k)
    {
        delegate = del;
        url = u;
        key = k;
    }

    protected Boolean doInBackground(Void... Params)
    {
        JSONArray legs;
        response = new ArrayList<>();
        int responseCode;
        try
        {
            URL service = new URL(url+key);
            HttpURLConnection connection=(HttpURLConnection) service.openConnection();
            connection.connect();
            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            StringBuffer sb = new StringBuffer();
            String json="";
            while( ( json = reader.readLine()) != null){
                sb.append(json);
            }
            json = sb.toString();

            JSONObject jObject=new JSONObject(json);
            responseCode = connection.getResponseCode();
            Log.i("data", jObject.toString());
            Log.i("conRes", String.valueOf(connection.getResponseCode()));
            legs = jObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");


            connection.disconnect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }

        for (int i=0; i<legs.length(); i++)
        {
            try
            {
                JSONArray step = legs.getJSONObject(i).getJSONArray("steps");
                for (int j=0; j<step.length();  j++)
                {
                    response.add(getPath(step.getJSONObject(j)));
                    //Log.i("obiekt " + j, getPath(legs.getJSONObject(j)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return responseCode == 200;
    }

    private String getPath(JSONObject step)
    {
        String point = "";
        try
        {
            point =  step.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return point;
    }

    protected void onPostExecute(Boolean result)
    {
        delegate.processReceiving(result, response);
    }
}