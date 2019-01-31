package ml.cerasus.pics.g;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cc.shinichi.library.ImagePreview;

public class HistoryActivity extends AppCompatActivity {

    boolean an = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent = getIntent();
        String sort = intent.getStringExtra("sort");
        an = intent.getBooleanExtra("anim", false);

        if (an){
            overridePendingTransition(R.anim.bottom_in, R.anim.buttom_out);
        }


        TujianUtils.View showView = new TujianUtils.View();
        WebView webView = findViewById(R.id.webView_history);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                //Snackbar.make(findViewById(R.id.webView_history),R.string.loaddone,Snackbar.LENGTH_LONG).show();
                super.onPageFinished(view, url);
            }
        });

        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21){
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                final String imgurl = result.getExtra();
                int type = result.getType();
                if (type == WebView.HitTestResult.IMAGE_TYPE){
                    Log.d("Tujian", "onLongClick:长按图片 "+imgurl);
                    assert imgurl != null;
                    ImagePreview.getInstance().setContext(HistoryActivity.this).setIndex(0).setImage(imgurl).start();
                }
                return true;
            }
        });
        
        Snackbar.make(webView,R.string.load,Snackbar.LENGTH_LONG).show();
        switch (sort) {
            case "CH":
                showView.showHistoryCH(webView);
                setTitle(R.string.CH);
                break;
            case "ZH":
                showView.showHistoryZH(webView);
                setTitle(R.string.ZH);
                break;
            case "CP":
                showView.showCom(webView);
                setTitle(R.string.ComputerWallpaper);
                break;
            default:
                finish();
        }
    }
}
