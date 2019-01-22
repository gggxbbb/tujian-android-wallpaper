package github.gggxbbb.tujian;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent = getIntent();
        String sort = intent.getStringExtra("sort");
        TujianUtils.View showView = new TujianUtils.View();
        WebView webView = findViewById(R.id.webView_history);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                //Snackbar.make(findViewById(R.id.webView_history),R.string.loaddone,Snackbar.LENGTH_LONG).show();
                super.onPageFinished(view, url);
            }
        });

        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                final String imgurl = result.getExtra();
                int type = result.getType();
                if (type == WebView.HitTestResult.IMAGE_TYPE){
                    Log.d("Tujian", "onLongClick:长按图片 "+imgurl);
                    AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                    builder.setTitle(R.string.ifDownload);
                    builder.setMessage(imgurl);
                    builder.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(imgurl));
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
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
            default:
                finish();
        }
    }
}
