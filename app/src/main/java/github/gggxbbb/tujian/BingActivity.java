package github.gggxbbb.tujian;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;

import cc.shinichi.library.ImagePreview;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bing);

        final ImageView show = findViewById(R.id.bing_show);
        final TextView info = findViewById(R.id.bing_info);
        OkHttpClient okHttpClient = new OkHttpClient();
        String ImgLink = "https://dp.chimon.me/fapp/api.php?sort=bing";
        Request request = new Request.Builder().url(ImgLink).method("GET", null).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snackbar.make(show, R.string.loadf, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String htmlStr = response.body().string();
                Log.d("Tujian", "onResponse: "+htmlStr);
                final String img_url,img_cont,img_page;
                img_url = htmlStr.split("【url】")[1];
                img_cont = htmlStr.split("【copyright】")[1];
                img_page = htmlStr.split("【copyrightlink】")[1];
                Log.d("Tujian", "onResponse: "+img_url);
                Log.d("Tujian", "onResponse: "+img_cont);
                Log.d("Tujian", "onResponse: "+img_page);
                BingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle(R.string.BingDaily);
                        Glide.with(BingActivity.this).load(Uri.parse(img_url)).into(show);
                        info.setText(img_cont);
                        info.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(img_page)));
                                return false;
                            }
                        });
                        show.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                ImagePreview.getInstance().setContext(BingActivity.this).setIndex(0).setImage(img_url).start();
                                return false;
                            }
                        });
                    }
                });
            }
        });
    }
}
