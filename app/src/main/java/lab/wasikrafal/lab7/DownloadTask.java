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

public class DownloadTask extends AsyncTask<String,Void,Boolean>
{
    private Response delegate;
    private List<String> response = null;

    public DownloadTask (Response del)
    {
        delegate = del;
    }

    protected Boolean doInBackground(String... Params)
    {
        JSONArray steps;
        response = new ArrayList<>();
        int responseCode = 404;
        try
        {
            URL service = new URL(Params[0]);
            HttpURLConnection connection=(HttpURLConnection) service.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String json=reader.readLine();
            JSONObject jObject=new JSONObject(json);
            responseCode = connection.getResponseCode();
            steps = jObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            Log.i("data", response.toString());
            Log.i("conRes", String.valueOf(connection.getResponseCode()));
            connection.disconnect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }

        for (int i=0; i<steps.length(); i++)
        {
            try
            {
                response.add(getPath(steps.getJSONObject(i)));
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