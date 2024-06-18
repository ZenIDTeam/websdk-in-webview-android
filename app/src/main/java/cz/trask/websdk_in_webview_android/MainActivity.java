package cz.trask.websdk_in_webview_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    private static final String SHARED_PREFERENCES = "preferences";
    private static final String PREFERENCE_URL = "url";
    private static final String PREFERENCE_USERNAME = "username";
    private static final String PREFERENCE_PASSWORD = "password";
    private static final String PREFERENCE_REMEMBER_CREDENTIALS = "remember_credentials";

    private EditText mEtUrl;
    private EditText mEtUsername;
    private EditText mEtPassword;
    private CheckBox mChbRememberCredentials;
    private Button mBtnOpenWebSdk;
    private SharedPreferences mSharedPreferences;
    private boolean mRememberCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSharedPreferences();
        bindView();
        setView();
    }

    private void setSharedPreferences() {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            mSharedPreferences = EncryptedSharedPreferences.create(
                    SHARED_PREFERENCES,
                    masterKeyAlias,
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void bindView() {
        mEtUrl = findViewById(R.id.et_url);
        mEtUsername = findViewById(R.id.et_username);
        mEtPassword = findViewById(R.id.et_password);
        mChbRememberCredentials = findViewById(R.id.chb_remember_credentials);
        mBtnOpenWebSdk = findViewById(R.id.btn_open_web_sdk);
    }

    private void setView() {
        String url = mSharedPreferences.getString(PREFERENCE_URL, null);
        if (TextUtils.isEmpty(url)) {
            url = BuildConfig.MY_URL;
        }
        String username = mSharedPreferences.getString(PREFERENCE_USERNAME, null);
        String password = mSharedPreferences.getString(PREFERENCE_PASSWORD, null);
        mRememberCredentials = mSharedPreferences.getBoolean(PREFERENCE_REMEMBER_CREDENTIALS, true);

        mEtUrl.setText(url);
        mEtUsername.setText(username);
        mEtPassword.setText(password);
        mChbRememberCredentials.setChecked(mRememberCredentials);
        mBtnOpenWebSdk.setOnClickListener(v -> openWebSdk());
    }

    private void openWebSdk() {
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        saveData();
        openWebViewScreen(username, password);
    }

    private void saveData() {
        mRememberCredentials = mChbRememberCredentials.isChecked();
        mSharedPreferences.edit().putBoolean(PREFERENCE_REMEMBER_CREDENTIALS, mRememberCredentials).apply();
        if (mRememberCredentials) {
            String url = mEtUrl.getText().toString();
            String username = mEtUsername.getText().toString();
            String password = mEtPassword.getText().toString();
            mSharedPreferences.edit().putString(PREFERENCE_URL, url).apply();
            mSharedPreferences.edit().putString(PREFERENCE_USERNAME, username).apply();
            mSharedPreferences.edit().putString(PREFERENCE_PASSWORD, password).apply();
        } else {
            mEtUrl.setText(BuildConfig.MY_URL);
            mEtUsername.setText(null);
            mEtPassword.setText(null);
            mSharedPreferences.edit().putString(PREFERENCE_URL, null).apply();
            mSharedPreferences.edit().putString(PREFERENCE_USERNAME, null).apply();
            mSharedPreferences.edit().putString(PREFERENCE_PASSWORD, null).apply();
        }
    }

    private void openWebViewScreen(String username, String password) {
        Intent intent = WebViewActivity.newIntent(MainActivity.this, username, password);
        startActivity(intent);
    }
}
