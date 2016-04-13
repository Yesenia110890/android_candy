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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.reception.candy.candyreception.R;
import com.reception.candy.candyreception.util.Constant;
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

public class ReservacionesActivity extends AppCompatActivity {

    @Bind(R.id.pb_Reserv)
    RelativeLayout pbReserv;
    @Bind(R.id.edt_disponibilidad)
    TextView tvDisponibilidad;
    @Bind(R.id.lly_articulos)
    LinearLayout llyArticulos;
    @Bind(R.id.lly_date_picker)
    LinearLayout llyDatePicker;
    @Bind(R.id.event_date)
    DatePicker eventDate;
    @Bind(R.id.edt_cant_mesas)
    EditText edtCantMesas;
    @Bind(R.id.chx_manteles)
    CheckBox chxManteles;
    @Bind(R.id.chx_faldones)
    CheckBox chxFaldones;
    @Bind(R.id.chx_monos)
    CheckBox chxMonos;
    @Bind(R.id.spn_color)
    Spinner spnColor;
    @Bind(R.id.edt_description)
    EditText edtDescription;

    private String msjValida = Constant.MSJ_VALIDA;

    String eventDateReserved;
    String owner;
    String description = "";
    List<String> productos = new ArrayList<>();
    int cantMesas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservaciones);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initPropierties();
    }

    public void initPropierties() {
        ButterKnife.bind(this);

        pbReserv.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        owner = bundle.getString("owner");

        String[] colors = getResources().getStringArray(R.array.colores);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                colors);
        spnColor.setAdapter(adapter);
    }

    public void guardaEvento(View v) {
        if (validEvent()) {
            saveEvent();
        } else {
            Toast.makeText(this, msjValida, Toast.LENGTH_SHORT).show();
            msjValida = Constant.MSJ_VALIDA;
        }
    }

    public void irALoza(View v) {
        if (validEvent()) {
            Intent i = new Intent(ReservacionesActivity.this, LozaActivity.class);
            i.putStringArrayListExtra("products", (ArrayList<String>) productos);
            i.putExtra("cantMesas", cantMesas);
            i.putExtra("owner", owner);
            i.putExtra("eventDate", eventDateReserved);
            i.putExtra("description", description);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(ReservacionesActivity.this, msjValida, Toast.LENGTH_SHORT).show();
            msjValida = Constant.MSJ_VALIDA;
        }
    }

    public void ConsultaDisponibilidad(View v) {
        createEvent();
    }

    private void createEvent() {
        eventDateReserved = eventDate.getYear() + "." +
                (eventDate.getMonth() + 1) + "." + eventDate.getDayOfMonth();

        availabilityEvent();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public boolean validEvent() {
        Boolean productsList = true;
        if (llyArticulos.getVisibility() == View.VISIBLE) {

            description = edtDescription.getText().toString().trim();

            if (edtCantMesas.getText().toString().trim().equals("")) {
                msjValida = msjValida + "\n" + "* Cantidad \n de mesas";
                productsList = false;
            } else {
                cantMesas = Integer.parseInt(edtCantMesas.getText().toString().trim());
                productos.add("MESAS : " + cantMesas);

                if (chxManteles.isChecked()) {
                    productos.add("MANTELES : " + cantMesas);
                }
                if (chxFaldones.isChecked()) {
                    productos.add("FALDONES : " + (cantMesas * 10));
                }
                if (chxMonos.isChecked()) {
                    String colorMoño = spnColor.getSelectedItem().toString();
                    productos.add("MONOS : " + (cantMesas * 10) + " " + colorMoño);
                }
            }
        }

        return productsList;
    }

    /* Web Service save the event.*/
    public void saveEvent() {

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

                if (joLoadSave.getString("status").equals("201")) {

                    alertSaveEvent();

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

    /* Web Service confirm availability to event.*/
    public void availabilityEvent() {

        Log.e(URLS.VERIFY_AVAILABILITY.concat(eventDateReserved), "");
        new AvailabilityEvent(URLS.VERIFY_AVAILABILITY.concat(eventDateReserved)).execute();
    }

    private class AvailabilityEvent extends AsyncTask<Void, Void, String> {


        private String saveUrl;
        private HttpEntity<String> entity;
        private RestTemplate restTemplate;
        private String result;

        public AvailabilityEvent(String url) {
            saveUrl = url;
            Log.e(saveUrl, "");
        }

        @Override
        protected void onPreExecute() {

            pbReserv.setVisibility(View.VISIBLE);

            entity = new HttpEntity<>( Util.getRequestHeaders());
            restTemplate = new RestTemplate(Util.clientHttpRequestFactory());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                ResponseEntity<String> response = restTemplate.exchange(saveUrl, HttpMethod.GET,
                        entity, String.class);
                return String.valueOf(response);
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

                String availability = "";

                if (!joLoadSave.getString("status").equals("500")) {

                    if (joLoadSave.getString("status").equals("200")) {
                        availability = joLoadSave.getString("detail");
                    } else if(joLoadSave.getString("status").equals("404")){
                        availability = joLoadSave.getJSONObject("error").getString("detail");
                    }

                    tvDisponibilidad.setText(availability);

                    if (tvDisponibilidad.getText() == "Disponible") {
                        alertDisponible();
                    } else {
                        Toast.makeText(ReservacionesActivity.this, Constant.FECHA_DISPONIBLE,
                                Toast.LENGTH_LONG)
                                .show();
                    }

                } else {
                    pbReserv.setVisibility(View.GONE);
                    JSONObject Response = new JSONObject(result);
                    generalMessage = Response.getString("status");
                    Toast.makeText(ReservacionesActivity.this, generalMessage, Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Log.e("ERROOOOOOOR AVAILABLE", "FALLAAAAAAA 4");
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
                        if (validEvent()) {
                            saveEvent();
                        } else {
                            Toast.makeText(ReservacionesActivity.this, msjValida,
                                    Toast.LENGTH_SHORT).show();
                            msjValida = Constant.MSJ_VALIDA;
                        }
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
