package com.example.liviu.helloapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import org.w3c.dom.Text;

import java.io.Console;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.example.liviu.helloapp.R.layout.*;


public class Dashboard extends ActionBarActivity {
    TextView output;
    ProgressBar pb;
    SharedPreferences someData;
    public List<Projects> pro_list=new ArrayList<Projects>();
    List<Projects> projectsList;
    List<String> myItems= new ArrayList<>();
    public static String filename="MySharedString";

    public static String get_projects_url="http://192.168.2.2:8081/mobile/get/projects";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_dashboard);


        someData = getSharedPreferences(MainActivity.filename,0);
        String id=someData.getString("id","Nu exista data aceasta");




        pb=(ProgressBar)findViewById(R.id.loader);
        pb.setVisibility(View.INVISIBLE);


        if(isOnline()){
            requestData(get_projects_url,id);
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
    protected void goToTasksScreen(){
        Intent intent=new Intent(this,TasksActivity.class);
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

            try{

                JSONArray arr = new JSONArray(s);
                final List<Projects> projectsList=new ArrayList<>();


                Log.e("LUNGIMEA VECTORULUI", String.valueOf(arr));
                for (int i=0;i<arr.length();i++){
                    JSONObject organization=arr.getJSONObject(i);
                    JSONArray arr2=new JSONArray();
                    arr2=organization.getJSONArray("organization_projects");
                    for(int j=0;j<arr2.length();j++){
                        JSONObject obj=arr2.getJSONObject(j);
                        pro_list.add(new Projects(obj.getString("project_name"),obj.getString("project_description"),obj.getString("_id"),organization.getString("organization_name")));

                        Projects project = new Projects();

                        project.setId(obj.getString("_id"));
                        project.setProject_name(obj.getString("project_name"));
                        project.setProject_description(obj.getString("project_description"));
                        project.setOrganization(organization.getString("organization_name"));
                        myItems.add(obj.getString("project_name"));
                        projectsList.add(project);
                    }




                }


                populateListView();

                ArrayAdapter<Projects> adapter = new projectListAdapter(pro_list);
                ListView listView=(ListView) findViewById(R.id.listView);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String project = projectsList.get(position).getProject_name();

                        Log.e("Pro_id:", projectsList.get(position).getId().toString());
                        Log.e("Pro_name:", projectsList.get(position).getProject_name());

                        SharedPreferences.Editor editor = someData.edit();
                        editor.putString("id_project", projectsList.get(position).getId());
                        editor.putString("project_name", projectsList.get(position).getProject_name());
                        editor.commit();

                        goToTasksScreen();
                        Toast.makeText(getApplicationContext(),
                                "Tasks for: " + project, Toast.LENGTH_LONG)
                                .show();

                    }
                });


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


    }

    public class projectListAdapter extends ArrayAdapter<Projects>{
        public projectListAdapter(List<Projects> pro_list){
            super(Dashboard.this,R.layout.lista_cu_projecte,pro_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

           View viewItem=convertView;
           if(viewItem==null){
               viewItem = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lista_cu_projecte, parent, false);
           }
            Projects currentPro=Dashboard.this.pro_list.get(position);

            TextView project_name=(TextView) viewItem.findViewById(R.id.project);
            project_name.setText(currentPro.getProject_name());

            TextView organization_name=(TextView) viewItem.findViewById(R.id.org);
            organization_name.setText(currentPro.getOrganization());

            return viewItem;
        }
    }
}
