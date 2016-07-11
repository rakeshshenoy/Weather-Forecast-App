package com.example.rakeshshenoy.forecastsearch;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MoreActivity extends AppCompatActivity {

    private String result, city, state, degree;
    private JSONObject jObj;
    private int dataids[] = {R.id.data0,R.id.data1,R.id.data2,R.id.data3,R.id.data4,R.id.data5,R.id.data6};
    private int rowids[] = {R.id.row0,R.id.row1,R.id.row2,R.id.row3,R.id.row4,R.id.row5,R.id.row6,R.id.row7,R.id.row8,R.id.row9,R.id.row10,R.id.row11,R.id.row12,R.id.row13,R.id.row14,R.id.row15,R.id.row16,R.id.row17,R.id.row18,R.id.row19,R.id.row20,R.id.row21,R.id.row22,R.id.row23,R.id.row25,R.id.row26,R.id.row27,R.id.row28,R.id.row29,R.id.row30,R.id.row31,R.id.row32,R.id.row33,R.id.row34,R.id.row35,R.id.row36,R.id.row37,R.id.row38,R.id.row39,R.id.row40, R.id.row41,R.id.row42,R.id.row43,R.id.row44,R.id.row45,R.id.row46,R.id.row47, R.id.row48};
    public void onNextDayClicked(View view)
    {
        TableLayout nextWeekTable = (TableLayout) findViewById(R.id.nextWeekTable);
        nextWeekTable.setVisibility(View.GONE);

        TableLayout nextDayTable = (TableLayout) findViewById(R.id.nextDayTable);
        nextDayTable.setVisibility(View.VISIBLE);

        Button nextDayButton = (Button) findViewById(R.id.nextDayButton);
        nextDayButton.setBackgroundColor(Color.parseColor("#bfefff"));

        Button nextWeekButton = (Button) findViewById(R.id.nextWeekButton);
        nextWeekButton.setBackgroundColor(Color.parseColor("#d3d3d3"));
    }

    public void onNextWeekClicked(View view)
    {
        TableLayout nextDayTable = (TableLayout) findViewById(R.id.nextDayTable);
        nextDayTable.setVisibility(View.GONE);

        TableLayout nextWeekTable = (TableLayout) findViewById(R.id.nextWeekTable);
        nextWeekTable.setVisibility(View.VISIBLE);

        Button nextDayButton = (Button) findViewById(R.id.nextDayButton);
        nextDayButton.setBackgroundColor(Color.parseColor("#d3d3d3"));

        Button nextWeekButton = (Button) findViewById(R.id.nextWeekButton);
        nextWeekButton.setBackgroundColor(Color.parseColor("#bfefff"));
    }

    public void onPlusClicked(View view) {
        TableRow plusRow = (TableRow) findViewById(R.id.row24);
        plusRow.setVisibility(View.GONE);

        for(int i = 24; i < 48; i++)
        {
            TableRow row = (TableRow) findViewById(rowids[i]);
            row.setVisibility(View.VISIBLE);
        }
    }

    public int summImg(String icon)
    {
        if(icon.matches("clear-day"))
            return(R.drawable.clear);
        else if(icon.matches("clear-night"))
            return(R.drawable.clear_night);
        else if(icon.matches("rain"))
            return(R.drawable.rain);
        else if(icon.matches("snow"))
            return(R.drawable.snow);
        else if(icon.matches("sleet"))
            return(R.drawable.sleet);
        else if(icon.matches("wind"))
            return(R.drawable.wind);
        else if(icon.matches("fog"))
            return(R.drawable.fog);
        else if(icon.matches("cloudy"))
            return(R.drawable.cloudy);
        else if(icon.matches("partly-cloudy-day"))
            return(R.drawable.cloud_day);
        else
            return(R.drawable.cloud_night);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_more);
        getSupportActionBar().setTitle("Details Activity");

        Button nextDayButton = (Button) findViewById(R.id.nextDayButton);
        nextDayButton.setBackgroundColor(Color.parseColor("#bfefff"));

        Button nextWeekButton = (Button) findViewById(R.id.nextWeekButton);
        nextWeekButton.setBackgroundColor(Color.parseColor("#d3d3d3"));

        TableLayout nextWeekTable = (TableLayout) findViewById(R.id.nextWeekTable);
        nextWeekTable.setVisibility(View.GONE);

        for(int i = 24; i < 48; i++)
        {
            TableRow tableRow = (TableRow) findViewById(rowids[i]);
            tableRow.setVisibility(View.GONE);
        }

        Bundle extras = getIntent().getExtras();
        result = extras.getString("result");
        city = extras.getString("city");
        state = extras.getString("state");
        degree = extras.getString("degree");

        try {
            jObj = new JSONObject(result);
            String hour = jObj.getString("hourly");
            JSONObject hourlyObj = new JSONObject(hour);
            JSONArray hourData = new JSONArray(hourlyObj.getString("data"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

            TextView headingTV = (TextView) findViewById(R.id.heading);
            headingTV.setText("More Details for " + city + ", " + state);

            TextView tempHeading = (TextView) findViewById(R.id.tempHeading);
            tempHeading.setText("Temp(" + degree + ")");

            for(int i = 0; i < 48; i++) {
                JSONObject hourObj = hourData.getJSONObject(i);

                TableRow tableRow = (TableRow) findViewById(rowids[i]);
                TextView time = (TextView) tableRow.getChildAt(0);
                Date timeVal = new Date(Long.parseLong(hourObj.getString("time")) * 1000);
                dateFormat.setTimeZone(TimeZone.getTimeZone(jObj.getString("timezone")));
                time.setText(dateFormat.format(timeVal).toString());

                String icon = hourObj.getString("icon");
                ImageView summary = (ImageView) tableRow.getChildAt(1);
                summary.setImageResource(summImg(icon));

                TextView temp = (TextView) tableRow.getChildAt(2);
                String tempVal = Integer.toString(Math.round(Float.parseFloat(hourObj.getString("temperature"))));
                temp.setText(tempVal);
            }

            String daily = jObj.getString("daily");
            JSONObject dailyObj = new JSONObject(daily);
            JSONArray dailyData = new JSONArray(dailyObj.getString("data"));

            for(int i = 0; i < 7; i++)
            {
                JSONObject dayObj = dailyData.getJSONObject(i);
                Date day = new Date(Long.parseLong(dayObj.getString("time")) * 1000);
                SimpleDateFormat format = new SimpleDateFormat("EEEE, MMM dd");

                String minMax = "Min:" + Math.round(Float.parseFloat(dayObj.getString("temperatureMin"))) + degree + " | Max:" + Math.round(Float.parseFloat(dayObj.getString("temperatureMax"))) + degree;

                TableRow tableRow = (TableRow) findViewById(dataids[i]);
                TextView data = (TextView) tableRow.getChildAt(0);
                data.setText(format.format(day).toString() + "\n\n" + minMax);

                ImageView img = (ImageView) tableRow.getChildAt(1);
                img.setImageResource(summImg(dayObj.getString("icon")));
            }
        }
        catch(JSONException e)
        {

        }
    }
}
