package com.reception.candy.candyreception.controller;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.reception.candy.candyreception.R;

public class PromocionesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promociones);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        switch (item.getItemId()){

            case R.id.action_inicio:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.action_reservaciones:
                finish();
                startActivity(new Intent(this, ReservacionesActivity.class));
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
                Intent i = new Intent(getBaseContext(), PromocionesActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
