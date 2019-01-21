package github.gggxbbb.tujian;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent = getIntent();
        String sort = intent.getStringExtra("sort");
        TujianUtils.View showView = new TujianUtils.View();
        WebView webView = findViewById(R.id.webView_history);
        Snackbar.make(webView,R.string.load,Snackbar.LENGTH_LONG).show();
        switch (sort) {
            case "CH":
                showView.showCH(webView);
                setTitle(R.string.CH);
                break;
            case "ZH":
                showView.showZH(webView);
                setTitle(R.string.ZH);
                break;
            default:
                finish();
        }
    }
}
