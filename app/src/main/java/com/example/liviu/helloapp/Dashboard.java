package com.example.liviu.helloapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import static com.example.liviu.helloapp.R.layout.*;


public class Dashboard extends ActionBarActivity {
    TextView output;
    ProgressBar pb;
    SharedPreferences someData;
    List<Projects> projectList;
    List<String> myItems= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_dashboard);

        someData = getSharedPreferences(MainActivity.filename,0);
        String id=someData.getString("id","Nu exista data aceasta");


        output = (TextView) findViewById(R.id.json);
        output.setMovementMethod(new ScrollingMovementMethod());

        pb=(ProgressBar)findViewById(R.id.loader);
        pb.setVisibility(View.INVISIBLE);

        if(isOnline()){
            requestData("http://192.168.2.4:8081/mobile/get/projects",id);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestData(String uri,String id) {

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("id",id);


        MyTask task= new MyTask();
        task.execute(p);
    }

    protected void updateDisplay(String message){
        output.append(message+'\n');
    }

    protected void updateDisplayInt(int n){
        output.setText(n);

    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnectedOrConnecting())
        {
            return true;
        }else{
            return false;
        }
    }


    protected class MyTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {

            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content=HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String s) {

            try{

                JSONArray arr = new JSONArray(s);
                List<Projects> projectsList=new ArrayList<>();

                Log.e("LUNGIMEA VECTORULUI", String.valueOf(arr.length()));
                for (int i=0;i<arr.length();i++){
                    Projects project = new Projects();
                    JSONObject obj=arr.getJSONObject(i);
                    project.setProject_name(obj.getString("project_name"));
                    project.setProject_description(obj.getString("project_description"));
                    myItems.add(obj.getString("project_name"));
                    projectsList.add(project);
                }


                populateListView();


            }
            catch (Exception e){
                e.printStackTrace();
            }

            pb.setVisibility(View.INVISIBLE);
        }


        @Override
        protected void onProgressUpdate(String... values) {
            updateDisplay(values[0]);
        }

    }
    private void populateListView() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,lista_cu_projecte,myItems);
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
    }

}
