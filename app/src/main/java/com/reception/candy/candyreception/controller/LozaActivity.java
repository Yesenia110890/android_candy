package com.reception.candy.candyreception.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.reception.candy.candyreception.R;
import com.reception.candy.candyreception.util.URLS;
import com.reception.candy.candyreception.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LozaActivity extends AppCompatActivity {

    @Bind(R.id.pb_Loza)
    RelativeLayout pbLoza;

    @Bind(R.id.chx_cuch_sopera)
    CheckBox chxCuchSopera;
    @Bind(R.id.chx_cuch_pastel)
    CheckBox chxCuchPastelera;
    @Bind(R.id.chx_tenedor)
    CheckBox chxTenedor;
    @Bind(R.id.chx_cuchillo)
    CheckBox chxCuchillo;
    @Bind(R.id.chx_plato_ext_gde)
    CheckBox chxPlatoExtGde;
    @Bind(R.id.chx_plato_ext_chico)
    CheckBox chxPlatoExtChico;
    @Bind(R.id.chx_plato_pastel)
    CheckBox chxPlatoPastelero;
    @Bind(R.id.chx_plato_hondo)
    CheckBox chxPlatoHondo;
    @Bind(R.id.chx_servilleta)
    CheckBox chxServilleta;
    @Bind(R.id.chx_salero)
    CheckBox chxSalero;
    @Bind(R.id.chx_hielera)
    CheckBox chxHielera;
    @Bind(R.id.chx_tortillero)
    CheckBox chxTortillero;
    @Bind(R.id.chx_copa_agua)
    CheckBox chxCopaAgua;
    @Bind(R.id.chx_copa_vino)
    CheckBox chxCopaVino;
    @Bind(R.id.chx_vaso)
    CheckBox chxVasoCristal;

    String eventDateReserved;
    String owner;
    String description = "";
    List<String> productos = new ArrayList<>();
    int cantMesas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loza);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initPropierties();
    }

    private void initPropierties() {
        ButterKnife.bind(this);

        pbLoza.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        owner = bundle.getString("owner");
        description = bundle.getString("description");
        eventDateReserved = bundle.getString("eventDate");
        cantMesas = bundle.getInt("cantMesas");
        productos = getIntent().getStringArrayListExtra("products");
    }

    private void products(){
        if (chxCuchSopera.isChecked()) {
            productos.add("CUCHARAS SOPERAS : " + (cantMesas * 10));
        }
        if (chxCuchPastelera.isChecked()) {
            productos.add("CUCHARAS PASTELERAS : " + (cantMesas * 10));
        }
        if (chxTenedor.isChecked()) {
            productos.add("TENEDORES : " + (cantMesas * 10));
        }
        if (chxCuchillo.isChecked()) {
            productos.add("CUCHILLOS : " + (cantMesas * 10));
        }
        if (chxPlatoExtGde.isChecked()) {
            productos.add("PLATOS EXTENDIDOS GDES : " + (cantMesas * 10));
        }
        if (chxPlatoExtChico.isChecked()) {
            productos.add("PLATOS EXTENDIDOS CHICOS : " + (cantMesas * 10));
        }
        if (chxPlatoPastelero.isChecked()) {
            productos.add("PLATOS PASTELEROS : " + (cantMesas * 10));
        }
        if (chxPlatoHondo.isChecked()) {
            productos.add("PLATOS HONDOS : " + (cantMesas * 10));
        }
        if (chxServilleta.isChecked()) {
            productos.add("SERVILLETAS : " + (cantMesas * 10));
        }
        if (chxSalero.isChecked()) {
            productos.add("SALEROS : " + (cantMesas * 2));
        }
        if (chxHielera.isChecked()) {
            productos.add("HIELERAS : " + cantMesas);
        }
        if (chxTortillero.isChecked()) {
            productos.add("TORTILLEROS : " + (cantMesas * 2));
        }
        if (chxCopaAgua.isChecked()) {
            productos.add("COPAS AGUA : " + (cantMesas * 10));
        }
        if (chxCopaVino.isChecked()) {
            productos.add("COPAS VINO : " + (cantMesas * 10));
        }
        if (chxVasoCristal.isChecked()) {
            productos.add("VASOS : " + (cantMesas * 10));
        }
    }

    public void guardarEvento(View v){
        saveEvent();
    }

    /* Web Service save the event.*/
    public void saveEvent() {
        products();
        JSONObject jsonEvent = new JSONObject();
        JSONArray jsonProductos = new JSONArray(productos);

        try {
            jsonEvent.put("event_date", eventDateReserved);
            jsonEvent.put("owner", owner);
            jsonEvent.put("description", description);
            jsonEvent.put("products", jsonProductos);

            Log.e("Event : ", jsonEvent.toString());
        } catch (JSONException e) {
            Log.e("ERROOOOOOOR", "FALLAAAAAAA 1");
            e.printStackTrace();
        }

        new SaveEvent(URLS.SAVE_EVENT, jsonEvent).execute();
    }

    private class SaveEvent extends AsyncTask<Void, Void, String> {


        private String saveUrl;
        private HttpEntity<String> entity;
        private RestTemplate restTemplate;
        private String result;
        private JSONObject cjsonObject;

        public SaveEvent(String url, JSONObject jsonObject) {
            saveUrl = url;
            cjsonObject = jsonObject;
            Log.e(saveUrl, "");
        }

        @Override
        protected void onPreExecute() {

            pbLoza.setVisibility(View.VISIBLE);

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
            pbLoza.setVisibility(View.GONE);
            this.result = response;
            try {
                JSONObject joLoadSave = new JSONObject(result);

                if ( joLoadSave.getString("status").equals("201") ) {

                    alertSaveEvent();

                    Log.e("Response: ", response);

                } else {
                    pbLoza.setVisibility(View.GONE);
                    JSONObject Response = new JSONObject(result);
                    generalMessage = Response.getString("status");
                    Toast.makeText(LozaActivity.this, generalMessage, Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Log.e("ERROOOOOOOR ", "FALLAAAAAAA 4");
                e.printStackTrace();
            }

        }
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
                        startActivity(new Intent(LozaActivity.this, MainActivity.class));
                    }
                }).show();
    }
}
