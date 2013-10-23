package com.example.wheelshare;

import android.os.AsyncTask;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HTTPConnection extends AsyncTask<String, Void, String>
{

    private HttpClient httpClient;
    private HttpPost httpPost;
    private String[] paramKeys;
    public final static String SERVER_URL = "http://wheelshare-tdbr.appspot.com/";

    public HTTPConnection(String urlEnding, String... paramKeys)
    {
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost(SERVER_URL + urlEnding);
        this.paramKeys = paramKeys;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            if (paramKeys.length != params.length)
                throw new Exception("length of parameters does not match the length of keys.");
            List<NameValuePair> passParams = new ArrayList<NameValuePair>();
            for (int i = 0; i < params.length; i++)
            {
                passParams.add(new BasicNameValuePair(paramKeys[i], params[i]));
            }

            httpPost.setEntity(new UrlEncodedFormEntity(passParams));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null)
                return EntityUtils.toString(entity);
            else
                return null;
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        } catch (ClientProtocolException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
