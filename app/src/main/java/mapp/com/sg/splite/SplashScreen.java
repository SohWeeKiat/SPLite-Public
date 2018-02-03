package mapp.com.sg.splite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sharedPref = this.getSharedPreferences("GlobalVariables", Context.MODE_PRIVATE);
        //launch the layout-> splash.xml
        setContentView(R.layout.splash);
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                boolean DoLogin = false;
                try {
                    long id = sharedPref.getLong("UserID",-1);
                    if (id < 0)
                        DoLogin = true;
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashScreen.this,DoLogin ? LoginActivity.class : MainMenu.class);
                    startActivity(intent);
                    SplashScreen.this.finish();
                }
            }
        };
        splashThread.start();
    }
}
