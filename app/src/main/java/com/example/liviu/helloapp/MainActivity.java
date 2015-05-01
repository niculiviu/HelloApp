package com.example.liviu.helloapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class MainActivity extends ActionBarActivity {

    TextView output;
    ProgressBar pb;
    EditText email;
    EditText pass;

    Button register_button;
    Button login;

    public static String filename="MySharedString";
    public static String login_url="http://192.168.2.2:8081/login/user";
    SharedPreferences someData;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.logo);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Task Heroes");

        someData = getSharedPreferences(filename,0);

        output = (TextView) findViewById(R.id.textView);
        output.setMovementMethod(new ScrollingMovementMethod());

        pb=(ProgressBar)findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        email=(EditText) findViewById(R.id.email);
        pass=(EditText) findViewById(R.id.pass);

        login=(Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    System.out.println(email.toString() + " " + pass.toString());
                    requestData(login_url, email.getText().toString(), pass.getText().toString());
                }
            }
        });

        register_button = (Button) findViewById(R.id.register);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),RegisterActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_bar_do_task){
            /*if(isOnline()){
                requestData("http://services.hanselandpetal.com/restful.php");
            }else{
                Toast.makeText(this,"Network is not available",Toast.LENGTH_LONG).show();
            }*/

        }
        return false;
    }
    protected void goToLogin(){
        Intent intent=new Intent(this,Dashboard.class);
        startActivityForResult(intent,0);

    }

    private void requestData(String uri,String email,String pass) {

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("username", email);
        p.SetParam("pass", pass);

        MyTask task= new MyTask();
        task.execute(p);
    }

    protected void updateDisplay(String message){
        output.append(message+'\n');
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


    protected class MyTask extends AsyncTask<RequestPackage,String,String>{

        @Override
        protected void onPreExecute() {
            //updateDisplay("Starting task");
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content=HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String s) {
           //updateDisplay(s);
            if(s!=null){
                try{

                    JSONObject obj= new JSONObject(s);
                    SharedPreferences.Editor editor= someData.edit();
                    editor.putString("id",obj.getString("_id"));
                    editor.putString("first_name",obj.getString("first_name"));
                    editor.putString("last_name",obj.getString("last_name"));
                    editor.putString("email",obj.getString("email"));
                    editor.commit();
                }
                catch (Exception e){

                }finally {
                    goToLogin();
                }


            }
            pb.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            updateDisplay(values[0]);
        }
    }


}
