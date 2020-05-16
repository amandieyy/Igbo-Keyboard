package igbokey.igwe.amanda;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.List;

import igbokey.igwe.amanda.R;

public class MainActivity extends AppCompatActivity {

    private PrefManager prefManager;

    @Override
    protected void onResume() {
        super.onResume();
        checkPrefs();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // use this to start and trigger a service
        //Intent i= new Intent(this, MyInputMethodService.class);
        //startService(i);

        prefManager = new PrefManager(this);

        checkPrefs();

        final Switch switchOnOff = findViewById(R.id.switchOnOff);
        switchOnOff.setChecked(prefManager.isActivated());
        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!prefManager.isActivated()) {
                        enableComponent();
                        prefManager.setIsActivated(true);
                    }
                } else {
                    if (prefManager.isActivated()) {
                        disableComponent();
                        prefManager.setIsActivated(false);
                    }
                }

                checkPrefs();

            }
        });

        ImageView btnSelectKeyboard = findViewById(R.id.imageViewKeyboard);
        btnSelectKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefManager.isAdded() && prefManager.isActivated()) {
                    InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                    imeManager.showInputMethodPicker();
                } else if (!prefManager.isAdded() && prefManager.isActivated()){
                    Intent enableIntent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
                    enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(enableIntent);
                }

            }
        });


    }

    private void disableComponent() {
        PackageManager pm = getPackageManager();
        if (pm != null) {
            pm.setComponentEnabledSetting(new ComponentName(this, MyInputMethodService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    private void enableComponent() {
        PackageManager pm = getPackageManager();
        if (pm != null) {
            pm.setComponentEnabledSetting(new ComponentName(this, MyInputMethodService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }


    private boolean isKeyboardAddedInSettings() {
        String packageLocal = getPackageName();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            List<InputMethodInfo> list = inputMethodManager.getEnabledInputMethodList();

            // check if our keyboard is enabled as input method
            for (InputMethodInfo inputMethod : list) {
                String packageName = inputMethod.getPackageName();
                if (packageName.equals(packageLocal)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void checkPrefs() {

        prefManager.setAdded(isKeyboardAddedInSettings());

        if (prefManager.isActivated() && prefManager.isAdded()) {
            Toast.makeText(getApplicationContext(),"Your keyboard is ready to use.",Toast.LENGTH_SHORT).show();
        } else if (!prefManager.isActivated()) {
            Toast.makeText(getApplicationContext(),"Your keyboard is disabled.",Toast.LENGTH_SHORT).show();
        } else if (!prefManager.isAdded()) {
            Toast.makeText(getApplicationContext(),"Your keyboard is not added or enabled in Android settings yet.",Toast.LENGTH_SHORT).show();
        }
    }




}
