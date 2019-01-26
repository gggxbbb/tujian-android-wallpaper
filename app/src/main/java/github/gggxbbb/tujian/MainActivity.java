package github.gggxbbb.tujian;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;


import java.io.IOException;

import cc.shinichi.library.ImagePreview;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //TujianUtils.View tujianView = new TujianUtils.View();
    private String img_Link;
    private String img_Title;
    private String img_Content;
    private boolean loadd = false;

    private void showImage(final String sort) {
        loadd = false;
        String Link = "https://dp.chimon.me/api/today.php?sort=";
        String ImgLink = "";
        switch (sort) {
            case "CH":
                ImgLink = Link + "二次元";
                break;
            case "ZH":
                ImgLink = Link + "杂烩";
                break;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(ImgLink).method("GET", null).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snackbar.make(findViewById(R.id.fab), R.string.loadf, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String htmlStr = response.body().string();
                Log.d("Tujian", "onResponse: " + htmlStr);
                JSONObject jsonObject = JSON.parseObject(htmlStr);
                String ifok;
                try {
                    ifok = jsonObject.getString("status");
                } catch (JSONException e) {
                    ifok = "Unknown";
                    e.printStackTrace();
                }
                if (ifok.equals("ok")) {
                    try {
                        JSONArray pictures = jsonObject.getJSONArray("pictures");
                        JSONObject picture = pictures.getJSONObject(0);
                        img_Title = picture.getString("p_title");
                        final String imgLink = picture.getString("p_link");
                        img_Link = imgLink;
                        img_Content = picture.getString("p_content");
                        loadd = true;
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GlideApp.with(MainActivity.this).load(Uri.parse(imgLink)).into((ImageView) findViewById(R.id.today_show));
                                switch (sort) {
                                    case "CH":
                                        setTitle(R.string.CH);
                                        break;
                                    case "ZH":
                                        setTitle(R.string.ZH);
                                        break;
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String err = getResources().getString(R.string.loadf) + ifok;
                    Snackbar.make(findViewById(R.id.fab), err, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadd){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(img_Title);
                    builder.setMessage(img_Content);
                    builder.setPositiveButton(R.string.knew, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton(R.string.setwall, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Glide.with(MainActivity.this).asBitmap().load(Uri.parse(img_Link)).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(MainActivity.this);
                                    try {
                                        wallpaperManager.setBitmap(resource);
                                        Snackbar.make(findViewById(R.id.fab),R.string.setd,Snackbar.LENGTH_LONG).show();
                                    }catch (IOException e){
                                        Snackbar.make(findViewById(R.id.fab),R.string.setf,Snackbar.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                    builder.setNeutralButton(R.string.share, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String shareMessage=String.format(getResources().getString(R.string.shareMessage),img_Title,img_Content,img_Link);
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }else {
                    Snackbar.make(findViewById(R.id.fab),R.string.load,Snackbar.LENGTH_LONG).show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ImageView imageView = findViewById(R.id.today_show);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ImagePreview.getInstance().setContext(MainActivity.this).setIndex(0).setImage(img_Link).start();
                return false;
            }
        });



        showImage("CH");

        //tujianView.showCH(webView);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //菜单栏点击事件

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        //侧栏点击事件
        // TODO: 2019/1/21 加入“实验性功能”设置 
        switch (id) {
            case R.id.today_ch:
                //tujianView.showCH(webView);
                showImage("CH");
                Snackbar.make(findViewById(R.id.fab), R.string.load, Snackbar.LENGTH_SHORT).show();
                //今日插画
                break;
            case R.id.today_zh:
                //tujianView.showZH(webView);
                Snackbar.make(findViewById(R.id.fab), R.string.load, Snackbar.LENGTH_SHORT).show();
                showImage("ZH");
                //今日杂烩
                break;
            case R.id.today_bing:
                startActivity(new Intent(MainActivity.this,BingActivity.class));
                //今日必应
                break;
            case R.id.history_ch:
                intent.putExtra("sort", "CH");
                startActivity(intent);
                //插画归档
                break;
            case R.id.history_zh:
                intent.putExtra("sort", "ZH");
                startActivity(intent);
                //杂烩归档
                break;
            case R.id.compaper:
                intent.putExtra("sort", "CP");
                startActivity(intent);
                //电脑壁纸
                break;
            case R.id.juzi:
                // TODO: 2019/1/21 实现句子显示 
                //句子
                break;
            case R.id.thisapp:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.About);
                builder.setMessage(R.string.aboutAppMessage);
                builder.setPositiveButton(R.string.knew, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                //关于程序
                break;
            case R.id.thisproject:
                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dp.chimon.me"));
                startActivity(intent1);
                //关于项目
                break;
            default:
                Toast.makeText(MainActivity.this, R.string.nothing, Toast.LENGTH_LONG).show();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
