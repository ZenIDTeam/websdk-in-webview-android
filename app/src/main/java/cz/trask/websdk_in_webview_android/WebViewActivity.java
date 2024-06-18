package cz.trask.websdk_in_webview_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG_FRAGMENT = "webView";
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_PASSWORD = "password";

    public static Intent newIntent(Context context, String username, String password) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_PASSWORD, password);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        String username = getIntent().getStringExtra(EXTRA_USERNAME);
        String password = getIntent().getStringExtra(EXTRA_PASSWORD);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (fragment == null) {
            fragment = WebViewFragment.newInstance(username, password);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment, TAG_FRAGMENT)
                .commit();
    }
}
