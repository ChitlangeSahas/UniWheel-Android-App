package com.chitlangesahas.unicycle;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

public class VESC_INTERFACE extends AppCompatActivity {
    private final Handler handler = new Handler();

    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("Auto-refresher", "CALLED");
                makeHTTPrequest();
                // Write code for your refresh logic
                doTheAutoRefresh();
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vesc);

        final SwipeRefreshLayout mySwipeRefreshLayout = findViewById(R.id.swiperefresh);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("SWIPE_REFRESH_VESC", "onRefresh called from SwipeRefreshLayout");
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        makeHTTPrequest();
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        doTheAutoRefresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void makeHTTPrequest() {
        RequestQueue queue = Volley.newRequestQueue(this); // this = context

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, getString(R.string.URL), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Update UI with response
                        updateActivityUI(response);
                        updateWidget(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);

    }


    void updateActivityUI(JSONObject response) {
        Log.d("Response", response.toString());
        try {
            // TODO add all the json responses.
            String RPM = response.getString("rpm");
            String INPUT_VOLTAGE = response.getString("inputVoltage") + "V";
            String AVG_INPUT_CURRENT = response.getString("avgInputCurrent");
            String DUTY_CYCLE = response.getString("dutyCycleNow");
            String TEMPERATURE_PCB = response.getString("temperaturePCB");

            Log.d("RESPONSE", RPM);
            TextView RPM_TEXTVIEW = findViewById(R.id.RPMText);
            RPM_TEXTVIEW.setText(RPM);

            TextView INPUT_VOLTAGE_TEXTVIEW = findViewById(R.id.voltageText);
            INPUT_VOLTAGE_TEXTVIEW.setText(INPUT_VOLTAGE);

            TextView AVG_INPUT_CURRENT_TEXTVIEW = findViewById(R.id.currentText);
            AVG_INPUT_CURRENT_TEXTVIEW.setText(AVG_INPUT_CURRENT);

            TextView DUTY_CYCLE_TEXTVIEW = findViewById(R.id.dutyText);
            DUTY_CYCLE_TEXTVIEW.setText(DUTY_CYCLE);

            TextView TEMPERATURE_TEXTVIEW = findViewById(R.id.tempText);
            TEMPERATURE_TEXTVIEW.setText(TEMPERATURE_PCB);


        } catch (JSONException e) { /* catch the exception */ }
    }


    void updateWidget(JSONObject response) {
        Log.d("Response", response.toString());
        try {
            // TODO add all the json responses.
            String RPM = response.getString("rpm");
            String INPUT_VOLTAGE = response.getString("inputVoltage") + "V";
            String AVG_INPUT_CURRENT = response.getString("avgInputCurrent");
            String DUTY_CYCLE = response.getString("dutyCycleNow");
            String TEMPERATURE_PCB = response.getString("temperaturePCB");

            Log.d("RESPONSE", RPM);
            TextView RPM_WIDGET_TEXTVIEW = findViewById(R.id.RPMText);
            RPM_WIDGET_TEXTVIEW.setText(RPM);

            TextView INPUT_VOLTAGE_TEXTVIEW = findViewById(R.id.voltageText);
            INPUT_VOLTAGE_TEXTVIEW.setText(INPUT_VOLTAGE);

            TextView AVG_INPUT_CURRENT_TEXTVIEW = findViewById(R.id.currentText);
            AVG_INPUT_CURRENT_TEXTVIEW.setText(AVG_INPUT_CURRENT);

            TextView DUTY_CYCLE_TEXTVIEW = findViewById(R.id.dutyText);
            DUTY_CYCLE_TEXTVIEW.setText(DUTY_CYCLE);

            TextView TEMPERATURE_TEXTVIEW = findViewById(R.id.tempText);
            TEMPERATURE_TEXTVIEW.setText(TEMPERATURE_PCB);


        } catch (JSONException e) { /* catch the exception */ }
    }

}
