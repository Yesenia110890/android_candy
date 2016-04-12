package com.reception.candy.candyreception.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.reception.candy.candyreception.R;
import com.reception.candy.candyreception.util.URLS;
import com.reception.candy.candyreception.util.Util;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReservacionesActivity extends AppCompatActivity {

    @Bind(R.id.pb_Reserv)
    RelativeLayout pbReserv;

    TextView tvDisponibilidad;
    LinearLayout llyArticulos;
    LinearLayout llyDatePicker;
    DatePicker eventDate;
    private String eventDateReserved;
    private String owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservaciones);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initPropierties();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {

            case R.id.action_inicio:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.action_promociones:
                finish();
                startActivity(new Intent(this, PromocionesActivity.class));
                break;

            case R.id.action_nosotros:
                finish();
                startActivity(new Intent(this, NosotrosActivity.class));
                break;

            case R.id.action_contacto:
                finish();
                startActivity(new Intent(this, ContactoActivity.class));
                break;

            case R.id.action_salir:
                Intent i = new Intent(getBaseContext(), ReservacionesActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initPropierties(){
        ButterKnife.bind(this);

        pbReserv.setVisibility(View.GONE);
    }

    public void Cancela(View v) {
        finish();
        startActivity(new Intent(ReservacionesActivity.this, MainActivity.class));

    }

    public void guardaEvento(){
        saveEvent();
    }


    public void ConsultaDisponibilidad(View v){
        createEvent();
    }

    private void createEvent(){
        String disponible = "Disponible";
        tvDisponibilidad = (TextView) findViewById(R.id.edt_disponibilidad);
        llyArticulos = (LinearLayout) findViewById(R.id.lly_articulos);
        eventDate = (DatePicker) findViewById(R.id.event_date);
        llyDatePicker = (LinearLayout) findViewById(R.id.lly_date_picker);
        eventDateReserved = eventDate.getYear() + "." +
                (eventDate.getMonth() + 1) + "." + eventDate.getDayOfMonth();

        tvDisponibilidad.setText(disponible);

        if (tvDisponibilidad.getText() == "Disponible") {
            alertDisponible();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(ReservacionesActivity.this, MainActivity.class));
        super.onBackPressed();
    }

    /* Web Service save the event.*/
    public void saveEvent() {

        JSONObject jsonEvent = new JSONObject();

        try {
            jsonEvent.put("event_date", eventDateReserved);
            jsonEvent.put("owner", owner);

            Log.e("Event : ", jsonEvent.toString());
        } catch (JSONException e) {
            Log.e("ERROOOOOOOR", "FALLAAAAAAA 1");
            e.printStackTrace();
        }

        new LoadEvent(URLS.SAVE_EVENT, jsonEvent).execute();
    }

    private class LoadEvent extends AsyncTask<Void, Void, String> {


        private String saveUrl;
        private HttpEntity<String> entity;
        private RestTemplate restTemplate;
        private String result;
        private JSONObject cjsonObject;

        public LoadEvent(String url, JSONObject jsonObject) {
            saveUrl = url;
            cjsonObject = jsonObject;
            Log.e(saveUrl, "");
        }

        @Override
        protected void onPreExecute() {

            pbReserv.setVisibility(View.VISIBLE);

            entity = new HttpEntity<>(cjsonObject.toString(), Util.getRequestHeaders());
            restTemplate = new RestTemplate(Util.clientHttpRequestFactory());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                ResponseEntity<String> response = restTemplate.exchange(saveUrl, HttpMethod.POST,
                        entity, String.class);
                return response.getBody();
            } catch (Exception rae) {

                rae.printStackTrace();
                return rae.toString();
            }
        }

        @Override
        protected void onPostExecute(String response) {

            String generalMessage;
            pbReserv.setVisibility(View.GONE);
            this.result = response;
            try {
                JSONObject joLoadSave = new JSONObject(result);

                if ( joLoadSave.getString("status").equals("201") ) {

                    Toast.makeText(ReservacionesActivity.this, joLoadSave.toString(), Toast.LENGTH_LONG)
                            .show();

                    Log.e("Response: ", response);

                } else {
                    pbReserv.setVisibility(View.GONE);
                    JSONObject Response = new JSONObject(result);
                    generalMessage = Response.getString("status");
                    Toast.makeText(ReservacionesActivity.this, generalMessage, Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Log.e("ERROOOOOOOR ", "FALLAAAAAAA 4");
                e.printStackTrace();
            }

        }
    }

    /**
     * Window alert.
     */
    public void alertDisponible() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this,
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder1.setCustomTitle(getLayoutInflater().inflate(R.layout.view_alert_dialog, null))
                .setPositiveButton(getResources().getString(R.string.hacer_pedido), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        llyDatePicker.setVisibility(View.GONE);
                        llyArticulos.setVisibility(View.VISIBLE);
                        dialog.cancel();
                    }
                })
                .setNeutralButton(getResources().getString(R.string.apartar_salon), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveEvent();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    /**
     * Window alert save.
     */
    public void alertSaveEvent() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this,
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder1.setCustomTitle(getLayoutInflater().inflate(R.layout.view_alert_dialog_save, null))
                .setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ReservacionesActivity.this, MainActivity.class));
                    }
                }).show();
    }
}
