package com.reception.candy.candyreception.controller;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.reception.candy.candyreception.R;
import com.reception.candy.candyreception.util.Constant;
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

public class CustomerActivity extends AppCompatActivity {

    @Bind(R.id.pb_Customer)
    RelativeLayout pbCustom;

    @Bind(R.id.edt_nombre)
    EditText edtNombre;
    @Bind(R.id.edt_apellido)
    EditText edtApellido;
    @Bind(R.id.edt_edad)
    EditText edtEdad;
    @Bind(R.id.edt_direccion)
    EditText edtDireccion;
    @Bind(R.id.edt_telefono)
    EditText edtTelefono;
    @Bind(R.id.edt_email)
    EditText edtEmail;

    private String msjValida = Constant.MSJ_VALIDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
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
                startActivity(new Intent(getApplicationContext(), MainActivity.class).
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initPropierties() {
        ButterKnife.bind(this);

        pbCustom.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CustomerActivity.this, MainActivity.class));
        finish();
    }

    public void envia(View v) {

        if(validate()) {
            saveCustomer();
        }else{
            Toast.makeText(this, msjValida, Toast.LENGTH_SHORT).show();
            msjValida = Constant.MSJ_VALIDA;
        }
    }

    public void cancela(View v) {
        startActivity(new Intent(CustomerActivity.this, MainActivity.class));
        finish();

    }

    public boolean validate(){
        Boolean valid = true;
        if (edtNombre.getText().toString().trim().equals("")) {
            msjValida = msjValida + "\n" + "* Nombre";
            valid = false;
        }
        if (edtApellido.getText().toString().trim().equals("")) {
            msjValida = msjValida + "\n" + "* Apellido";
            valid = false;
        }
        if (edtEdad.getText().toString().trim().equals("")) {
            msjValida = msjValida + "\n" + "* Edad";
            valid = false;
        } else if (Integer.valueOf(edtEdad.getText().toString().trim()) < 18) {
            Toast.makeText(this, "Debes ser mayor de edad", Toast.LENGTH_SHORT).show();
        }
        if (edtDireccion.getText().toString().trim().equals("")) {
            msjValida = msjValida + "\n" + "* Dirección";
            valid = false;
        }
        if (edtTelefono.getText().toString().trim().equals("")) {
            msjValida = msjValida + "\n" + "* Teléfono";
            valid = false;
        }
        if (edtEmail.getText().toString().trim().equals("")) {
            msjValida = msjValida + "\n" + "* E-mail";
            valid = false;
        }
        return valid;
    }

    /* Web Service save the customer.*/
    public void saveCustomer() {

        String name = edtNombre.getText().toString().trim();
        String lastName = edtApellido.getText().toString().trim();
        int age = Integer.valueOf(edtEdad.getText().toString().trim());
        String address = edtDireccion.getText().toString().trim();
        String phone = edtTelefono.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        JSONObject jsonCustomer = new JSONObject();

        try {
            jsonCustomer.put(Constant.FIRST_NAME, name);
            jsonCustomer.put("last_name", lastName);
            jsonCustomer.put("age", age);
            jsonCustomer.put("address", address);
            jsonCustomer.put("phone", phone);
            jsonCustomer.put("email", email);

            Log.e("Customer : ", jsonCustomer.toString());
        } catch (JSONException e) {
            Log.e("ERROOOOOOOR", "FALLAAAAAAA 1");
            e.printStackTrace();
        }

        new LoadCustomer(URLS.SAVE_CUSTOMER, jsonCustomer).execute();
    }

    private class LoadCustomer extends AsyncTask<Void, Void, String> {


        private String saveUrl;
        private HttpEntity<String> entity;
        private RestTemplate restTemplate;
        private String result;
        private JSONObject cjsonObject;

        public LoadCustomer(String url, JSONObject jsonObject) {
            saveUrl = url;
            cjsonObject = jsonObject;
            Log.e(saveUrl, "");
        }

        @Override
        protected void onPreExecute() {

            pbCustom.setVisibility(View.VISIBLE);

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
            pbCustom.setVisibility(View.GONE);
            this.result = response;
            try {
                JSONObject joLoadSave = new JSONObject(result);

                if ( joLoadSave.getString("status").equals("201") ) {

                    JSONObject joCustomer = joLoadSave.getJSONObject("customer");

                    String owner = joCustomer.getString("_id");
                    Intent i = new Intent(CustomerActivity.this, ReservacionesActivity.class);
                    i.putExtra("owner", owner);
                    startActivity(i);

                    Log.e("Response: ", response);

                } else {
                    pbCustom.setVisibility(View.GONE);
                    JSONObject Response = new JSONObject(result);
                    generalMessage = Response.getString("status");
                    Toast.makeText(CustomerActivity.this, generalMessage, Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Log.e("ERROOOOOOOR ", "FALLAAAAAAA 4");
                generalMessage = "No se pudo establecer conexión con el servidor " +
                        "\n intente más tarde";
                Toast.makeText(CustomerActivity.this, generalMessage, Toast.LENGTH_LONG)
                        .show();
                startActivity(new Intent(CustomerActivity.this, MainActivity.class));
                e.printStackTrace();
            }

        }
    }

}
