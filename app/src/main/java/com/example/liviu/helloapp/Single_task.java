package com.example.liviu.helloapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Single_task extends ActionBarActivity {
    ProgressBar pb;
    SharedPreferences someData;
    public static String filename="MySharedString";
    public static String get_sigle_task_url="http://192.168.2.2:8081/mobile/get/project/sigle/task";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_task);

        someData = getSharedPreferences(MainActivity.filename,0);
        String id=someData.getString("id_task","Nu exista data aceasta");
        Log.e("ID:", id);

        pb=(ProgressBar)findViewById(R.id.loader);
        pb.setVisibility(View.INVISIBLE);

        if(isOnline()){
            requestData(get_sigle_task_url,id);
        }
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

    private void requestData(String uri,String id) {

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("id",id);


        MyTask task= new MyTask();
        task.execute(p);
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
            Log.e("RESPONSE",s.toString());

            try{
                JSONObject task_info = new JSONObject(s);
                Log.e("Task_info",task_info.toString());

                TextView task_title = (TextView) findViewById(R.id.textView3);
                task_title.setText(task_info.getString("task_name"));

                TextView task_points = (TextView) findViewById(R.id.textView5);
                task_points.setText(task_info.getString("points"));

                TextView task_volunters_numer = (TextView) findViewById(R.id.textView7);

                JSONArray i_volnteer = task_info.getJSONArray("i_volunteer");
                Integer volunteers_numer=i_volnteer.length();
                task_volunters_numer.setText(""+volunteers_numer);

                if(volunteers_numer>1){
                    TextView heroes_lable= (TextView) findViewById(R.id.textView8);
                    heroes_lable.setText("Heroes");
                }

                TextView task_status=(TextView) findViewById(R.id.textView9);
                if(task_info.getString("status").equals("2")){

                    Button done_button=(Button) findViewById(R.id.button);
                    done_button.setVisibility(View.INVISIBLE);

                    task_status.setText("Done");
                    task_status.setTextColor(Color.parseColor("#449d44"));

                }else
                if(task_info.getString("status").equals("1")){
                    task_status.setTextColor(Color.parseColor("#c9302c"));
                    task_status.setText("Open Task");
                }

                TextView added_on =(TextView) findViewById(R.id.textView10);
                added_on.setText("Added on: "+task_info.getString("added_on"));

                TextView deadline=(TextView) findViewById(R.id.textView11);
                if(task_info.isNull("deadline")){
                    FrameLayout deadline_layout = (FrameLayout) findViewById(R.id.frameLayout4);
                    deadline_layout.setVisibility(View.GONE);
                }else{
                    deadline.setText("Deadline: "+task_info.getString("deadline"));
                }

                TextView desc=(TextView) findViewById(R.id.textView4);
                if(task_info.isNull("task_desc")){
                    FrameLayout desc_layout = (FrameLayout) findViewById(R.id.frameLayout);
                    desc_layout.setVisibility(View.GONE);
                }else{
                    desc.setText(task_info.getString("task_desc"));
                }
            }catch (Exception e){
                Log.e("Error",e.toString());
                e.printStackTrace();
            }



            pb.setVisibility(View.INVISIBLE);
        }


        @Override
        protected void onProgressUpdate(String... values) {
           // updateDisplay(values[0]);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_task, menu);
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
}
