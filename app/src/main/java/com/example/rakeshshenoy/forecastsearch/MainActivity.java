package com.example.rakeshshenoy.forecastsearch;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private RadioGroup radioGroup;
    Spinner spinner;
    String error;
    EditText text1;
    TextView err;
    EditText text2;

    public void onLogoClicked(View view)
    {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.forecast.io")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        spinner = (Spinner) findViewById(R.id.state);
        ArrayAdapter aa = ArrayAdapter.createFromResource(this, R.array.statecodes, android.R.layout.simple_spinner_item);
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(this);
    }

    public void onAboutClicked(View view){
        Intent about = new Intent(this, AboutActivity.class);
        startActivity(about);
    }

    public void clear (View view1){
        radioGroup = (RadioGroup) findViewById(R.id.degree);
        radioGroup.check(R.id.fahrenheit);
        EditText text1 = (EditText) findViewById(R.id.street);
        EditText text2 = (EditText) findViewById(R.id.city);
        err = (TextView) findViewById(R.id.errorMsg);
        text1.setText("");
        text2.setText("");
        err.setText("");
        spinner.setSelection(0);
    }
    public void Search(View view){
        int flag = 1;
        text1 = (EditText) findViewById(R.id.street);
        text2 = (EditText) findViewById(R.id.city);
        String street = text1.getText().toString();
        String city = text2.getText().toString();
        err = (TextView) findViewById(R.id.errorMsg);
        err.setText("");
        error="";

        if(street.matches("")){
            error="Please Enter the Street Address.\n";
            flag = 0;
        }
        if(city.matches("")){
            error=error+"Please Enter the City.\n";
            flag = 0;
        }
        spinner=(Spinner) findViewById(R.id.state);
        String state = spinner.getSelectedItem().toString();
        if(state.matches("Select")){
            error=error+"Please Enter the state.";
            flag = 0;
        }

        if(flag == 0)
            err.setText(error);
        else
        {
            String str1 = text1.getText().toString();
            String str2 = text2.getText().toString();
            String str3 = spinner.getSelectedItem().toString();
            radioGroup = (RadioGroup) findViewById(R.id.degree);
            int id = radioGroup.getCheckedRadioButtonId();

            RadioButton selected = (RadioButton)findViewById(id);
            String str4 = selected.getText().toString();

            System.out.println(str1 + " " + str2 + " " + str3 + " " + str4);
            connectWithHttpGet(str1, str2, str3, str4);
        }
    }

    private void connectWithHttpGet(String str1, String str2, String str3, String str4) {
        class HttpGetAsyncTask extends AsyncTask<String, Void, String> {

            String one, two, three, four;
            @Override
            protected String doInBackground(String... params) {

                // As you can see, doInBackground has taken an Array of Strings as the argument
                //We need to specifically get the givenUsername and givenPassword
                one = params[0];
                two = params[1];
                three = params[2];
                four = params[3];
                System.out.println(one + " " + two + " " + three + " " + four);

                String newone = one.replaceAll(" ", "+");
                String newtwo = two.replaceAll(" ", "+");

                // Create an intermediate to connect with the Internet
                HttpClient httpClient = new DefaultHttpClient();

                // Sending a GET request to the web page that we want
                // Because of we are sending a GET request, we have to pass the values through the URL
                HttpGet httpGet = new HttpGet("http://shenoyfirstapp-env.elasticbeanstalk.com/index/index.php?address=" + newone + "&city=" + newtwo + "&state=" + three + "&units=" + four.toLowerCase());

                try {
                    // execute(); executes a request using the default context.
                    // Then we assign the execution result to HttpResponse
                    HttpResponse httpResponse = httpClient.execute(httpGet);

                    // Now we need a readable source to read the byte stream that comes as the httpResponse
                    InputStream inputStream = httpResponse.getEntity().getContent();

                    // We have a byte stream. Next step is to convert it to a Character stream
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    // Then we have to wraps the existing reader (InputStreamReader) and buffer the input
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    // InputStreamReader contains a buffer of bytes read from the source stream and converts these into characters as needed.
                    //The buffer size is 8K
                    //Therefore we need a mechanism to append the separately coming chunks in to one String element
                    // We have to use a class that can handle modifiable sequence of characters for use in creating String
                    StringBuilder stringBuilder = new StringBuilder();

                    String bufferedStrChunk = null;

                    // There may be so many buffered chunks. We have to go through each and every chunk of characters
                    //and assign a each chunk to bufferedStrChunk String variable
                    //and append that value one by one to the stringBuilder
                    while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                        stringBuilder.append(bufferedStrChunk);
                    }

                    return stringBuilder.toString();
                } catch (ClientProtocolException cpe) {
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                if(result.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Invalid Address", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("result", result);
                    extras.putString("city", two);
                    extras.putString("state", three);
                    extras.putString("degree", four);
                    resultIntent.putExtras(extras);
                    startActivity(resultIntent);
                }
            }
        }

        // Initialize the AsyncTask class
        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
        // Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
        // We are passing the connectWithHttpGet() method arguments to that
        httpGetAsyncTask.execute(str1, str2, str3, str4);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
