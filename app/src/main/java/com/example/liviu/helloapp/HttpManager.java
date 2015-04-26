package com.example.liviu.helloapp;

import android.net.http.AndroidHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by liviu on 26.04.2015.
 */
public class HttpManager {
    public static String getData(RequestPackage p){

        BufferedReader reader=null;
        String uri=p.getUri();

        if(p.getMethod().equals("GET")){
            uri += "?"+p.getEncodedParams();
        }

        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(p.getMethod());

            if(p.getMethod().equals("POST")){
                con.setDoInput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(p.getEncodedParams());
                writer.flush();
            }

            StringBuilder sb = new StringBuilder();
            reader=new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;

            while ((line=reader.readLine())!=null){
                sb.append(line+"\n");
            }

            return sb.toString();
        }catch(Exception e){
            e.printStackTrace();
            return null;

        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        /*AndroidHttpClient client=AndroidHttpClient.newInstance("AndroidAgent");
        HttpGet request = new HttpGet(uri);
        HttpResponse response;
        try {
            response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            client.close();
        }*/
    }
}
