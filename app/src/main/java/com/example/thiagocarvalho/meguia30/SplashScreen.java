package com.example.thiagocarvalho.meguia30;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;import com.example.thiagocarvalho.meguia30.R;


public class SplashScreen extends Activity {

    public static final int segundos=8;
    public static final int milisegundos=segundos*1000;
    public static int delay=2;
    private ProgressBar pbprogreso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        pbprogreso=(ProgressBar)findViewById(R.id.pbprogreso);
        pbprogreso.setMax(maximo_progreso());
        aguardar();

    }
    public void aguardar(){
        new CountDownTimer(milisegundos,1000){
            @Override
            public void onTick(long millisUntilFinished){
                pbprogreso.setProgress(estabelecerProgresso(millisUntilFinished));
            }

            @Override
            public void onFinish(){
                Intent nuevofrom=new Intent(SplashScreen.this,MapsActivity.class);
                startActivity(nuevofrom);
                finish();
            }

        }.start();
    }

    public int estabelecerProgresso(long milisecounds){

        return (int) ((milisegundos-milisecounds)/1000);
    }

    public int maximo_progreso(){

        return segundos-delay;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tela_principal, menu);
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
}
