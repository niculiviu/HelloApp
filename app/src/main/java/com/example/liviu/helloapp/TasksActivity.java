package com.example.liviu.helloapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.liviu.helloapp.R.layout.activity_dashboard;
import static com.example.liviu.helloapp.R.layout.lista_cu_projecte;


public class TasksActivity extends ActionBarActivity {
    TextView output;
    ProgressBar pb;

    SharedPreferences someData;
    List<Tasks> tasks;
    public List<Tasks> task_list=new ArrayList<Tasks>();
    List<String> myItems= new ArrayList<>();
    public static String filename="MySharedString";

    public static String get_tasks_url="http://192.168.2.2:8081/mobile/get/project/task";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_dashboard);


        someData = getSharedPreferences(MainActivity.filename,0);
        String id=someData.getString("id_project","Nu exista data aceasta");
        Log.e("ID:",id);

        TextView title=(TextView) findViewById(R.id.textView2);
        title.setText(someData.getString("project_name","undefined"));



        pb=(ProgressBar)findViewById(R.id.loader);
        pb.setVisibility(View.INVISIBLE);


        if(isOnline()){
            requestData(get_tasks_url,id);
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
    protected void goToSingleTaskScreen(){
        Intent intent=new Intent(this,Single_task.class);
        startActivityForResult(intent,0);

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

            try {

                JSONArray arr=new JSONArray(s);
                for(int i=0;i<arr.length();i++){
                    JSONObject obj=arr.getJSONObject(i);
                    task_list.add(new Tasks(obj.getString("task_name"), "" + obj.getJSONArray("i_volunteer").length(), obj.getString("points"), obj.getString("status"), obj.getString("_id")));
                }

                ArrayAdapter<Tasks> adapter = new taskListAdapter();
                ListView list=(ListView) findViewById(R.id.listView);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        SharedPreferences.Editor editor = someData.edit();
                        editor.putString("id_task",task_list.get(position).getId());
                        editor.commit();

                        goToSingleTaskScreen();
                        /*String project = projectsList.get(position).getProject_name();

                        Log.e("Pro_id:", projectsList.get(position).getId().toString());
                        Log.e("Pro_name:", projectsList.get(position).getProject_name());

                        SharedPreferences.Editor editor = someData.edit();
                        editor.putString("id_project", projectsList.get(position).getId());
                        editor.putString("project_name", projectsList.get(position).getProject_name());
                        editor.commit();

                        goToTasksScreen();
                        Toast.makeText(getApplicationContext(),
                                "Tasks for: " + project, Toast.LENGTH_LONG)
                                .show();*/

                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }



            pb.setVisibility(View.INVISIBLE);
        }


        @Override
        protected void onProgressUpdate(String... values) {
            updateDisplay(values[0]);
        }

    }




    public class taskListAdapter extends ArrayAdapter<Tasks>{
        public taskListAdapter(){
            super(TasksActivity.this,R.layout.lista_cu_taskuri,task_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View viewItem=convertView;
            if(viewItem==null){
                viewItem = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lista_cu_taskuri, parent, false);
            }
            Tasks currentTask=TasksActivity.this.task_list.get(position);

            TextView task_title=(TextView) viewItem.findViewById(R.id.task_name);
            task_title.setText(currentTask.getTask_name());

            TextView task_points=(TextView) viewItem.findViewById(R.id.task_points);
            task_points.setText(currentTask.getPoints()+" points");

            TextView task_vol_num=(TextView) viewItem.findViewById(R.id.task_volunteers_number);
            task_vol_num.setText(currentTask.getTask_volunteers_num()+" volunteers");

            TextView task_status=(TextView) viewItem.findViewById(R.id.task_status);
            if(currentTask.getStatus().equals("2")){
                task_status.setText("Done");
                task_status.setTextColor(Color.parseColor("#449d44"));
            }else
            if(currentTask.getStatus().equals("1")){
                task_status.setTextColor(Color.parseColor("#c9302c"));
                task_status.setText("Open Task");
            }


            return viewItem;
        }
    }

}

