package com.techneaux.techneauxmobileapp;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by CMD Drake on 11/10/2015.
 */
public class API_Communications implements Runnable{

    public static String url;
    public static JSONObject json;
    public static String result;
    public API_Communications(String setUrl, JSONObject setJson) {
        url = setUrl;
        json = setJson;
    }


    public void run() {
        postData();
    }
    public static void postData(){
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);


        try {

            JSONArray postjson= new JSONArray();
            postjson.put(json);

            // Post the data:
            httppost.setHeader("json",json.toString());
            httppost.getParams().setParameter("jsonpost",postjson);

            // Execute HTTP Post Request
            System.out.print(json);
            HttpResponse response = httpclient.execute(httppost);

            // for JSON:
            if(response != null)
            {
                InputStream is = response.getEntity().getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                result = sb.toString();
                Log.d("API","Result: " + result);


            }



        }catch (ClientProtocolException e) {
            Log.d("API","ClientProtocolException");
            // TODO Auto-generated catch block
        } catch (IOException e) {
            Log.d("API",e.getLocalizedMessage());
            // TODO Auto-generated catch block
        }


    }

}

