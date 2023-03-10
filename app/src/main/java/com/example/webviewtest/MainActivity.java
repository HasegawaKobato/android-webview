package com.example.webviewtest;

import static android.content.Context.CLIPBOARD_SERVICE;
import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.webviewtest.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.function.Function;

class JsObject {
    @JavascriptInterface
    public void execute(String query, Function<String, String> callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            callback.apply(query);
        }
    }
}
class WebAppInterface {
    private Context mContext;
    private Activity mActivity;

    public WebAppInterface(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
    }

    @JavascriptInterface
    public void copyToClipboard(String text) {
        Log.i("clip", text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Do something if permission granted
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("demo", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "已複製到剪貼板", Toast.LENGTH_SHORT).show();
            } else {
                requestPermissions(mActivity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            // Do something if permission granted
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("demo", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(mContext, "已複製到剪貼板", Toast.LENGTH_SHORT).show();
        }
    }
}
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    //宣告
    WebView webview;
    WebSettings webSettings;
    String url="https://www.google.com/";
    String iframe = "<iframe src=\"" + url + "\" style=\"display: block;width: 100vw;height:100vh;\"></iframe>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        final Button button = findViewById(R.id.button_first);
        button.setVisibility(View.VISIBLE);
        Log.i("Test", button.toString());
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i("Test", "in");
                showWebview(view);
            }
        });
    }

    protected void showWebview(View view) {
        final Button button = findViewById(R.id.button_first);
        button.setVisibility(View.INVISIBLE);
        webview = findViewById(R.id.webview);
        webview.setFitsSystemWindows(false);
        webview.clearCache(true);
        webSettings=webview.getSettings();
        webSettings.setJavaScriptEnabled(true);//開啟javascript功能
        webSettings.setDomStorageEnabled(true);
        webview.addJavascriptInterface(new JsObject(), "opener");
        webview.addJavascriptInterface(new WebAppInterface(this, this.getParent()), "NativeAndroid");
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        WebViewClient webClient = new WebViewClient() {

        };
        webview.setWebViewClient(webClient);//新增瀏覽器客戶端
        webview.loadUrl(url);//讀取url網站
//        webview.loadData(iframe, "text/html", null);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}