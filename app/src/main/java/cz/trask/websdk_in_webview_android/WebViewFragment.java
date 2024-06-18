package cz.trask.websdk_in_webview_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.HttpAuthHandler;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import timber.log.Timber;

public class WebViewFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION_CODE = 7;

    private WebView webView;

    public static WebViewFragment newInstance(String username, String password) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(WebViewActivity.EXTRA_USERNAME, username);
        args.putString(WebViewActivity.EXTRA_PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        String username = null;
        String password = null;

        if (getArguments() != null) {
            username = getArguments().getString(WebViewActivity.EXTRA_USERNAME);
            password = getArguments().getString(WebViewActivity.EXTRA_PASSWORD);
        }

        FrameLayout frameLayout = view.findViewById(R.id.webViewContainer);
        if (webView == null && getContext() != null) {
            webView = new WebView(getContext());
            setupWebView(username, password);
        }
        frameLayout.removeAllViews();
        frameLayout.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return view;
    }

    @Override
    public void onDestroyView() {
        if (webView.getParent() instanceof ViewGroup) {
            ((ViewGroup) webView.getParent()).removeView(webView);
        }
        super.onDestroyView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (isCameraPermissionGranted()) {
                Timber.d("Permissions granted.");
                loadUrl();
            } else {
                String msg = "Camera permission is required.";
                Timber.d(msg);
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                Activity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(String username, String password) {
        Timber.d("setupWebView");
        webView.setWebViewClient(new MyWebViewClient(username, password));
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        if (isCameraPermissionGranted()) {
            loadUrl();
        } else {
            requestCameraPermission();
        }
    }

    private void loadUrl() {
        Timber.i("Loading %s", BuildConfig.MY_URL);
        webView.loadUrl(BuildConfig.MY_URL);
    }

    private boolean isCameraPermissionGranted() {
        if (getContext() == null) {
            return false;
        }
        int selfPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        return selfPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
    }

    private static class MyWebViewClient extends WebViewClient {

        private final String username;
        private final String password;

        public MyWebViewClient(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Timber.d("shouldOverrideUrlLoading: %s", request.getUrl());
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            Timber.d("onReceivedHttpAuthRequest: %s", host);
            handler.proceed(username, password);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Timber.d("onReceivedError: %s", error);
            super.onReceivedError(view, request, error);
        }
    }

    private static class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            Timber.d("onPermissionRequest");
            request.grant(request.getResources());
        }
    }
}
