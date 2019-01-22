package github.gggxbbb.tujian;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TujianUtils.View tujianView = new TujianUtils.View();
    String Sort;

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
                String Link = "https://dp.chimon.me/api/today.php?sort=";
                String ImgLink = "";
                switch (Sort) {
                    case "CH":
                        ImgLink = Link + "二次元";
                        break;
                    case "ZH":
                        ImgLink = Link + "杂烩";
                        break;
                }
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(ImgLink).method("GET", null).build();
                final Call call = okHttpClient.newCall(request);
                Snackbar.make(view, R.string.load, Snackbar.LENGTH_SHORT).setAction(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        call.cancel();
                    }
                }).show();
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
                                final String imgTitle = picture.getString("p_title");
                                final String imgLink = picture.getString("p_link");
                                final String imgCont = picture.getString("p_content");
                                Log.d("Tujian", "onResponse: title:" + imgTitle + ";link" + imgLink + ";cont" + imgCont);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(imgTitle);
                                builder.setMessage(imgCont);
                                builder.setPositiveButton(R.string.knew, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.setNegativeButton(R.string.download, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(imgLink));
                                        startActivity(intent);
                                    }
                                });
                                builder.setNeutralButton(R.string.share, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String shareMessage=String.format(getResources().getString(R.string.shareMessage),imgTitle,imgCont,imgLink);
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                                        startActivity(intent);
                                    }
                                });
                                Looper.prepare();
                                builder.show();
                                Looper.loop();
                            } catch (JSONException e) {
                                String err = getResources().getString(R.string.loadf) + ifok;
                                Snackbar.make(findViewById(R.id.fab), err, Snackbar.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            String err = getResources().getString(R.string.loadf) + ifok;
                            Snackbar.make(findViewById(R.id.fab), err, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Snackbar.make(findViewById(R.id.fab), R.string.load, Snackbar.LENGTH_LONG).show();
        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //Snackbar.make(findViewById(R.id.fab), R.string.loaddone, Snackbar.LENGTH_SHORT).show();
                super.onPageFinished(view, url);
            }
        });
        Sort = "CH";
        tujianView.showCH(webView);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        WebView webView = findViewById(R.id.webView);
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        //侧栏点击事件
        // TODO: 2019/1/21 加入“实验性功能”设置 
        switch (id) {
            case R.id.today_ch:
                tujianView.showCH(webView);
                Snackbar.make(findViewById(R.id.fab), R.string.load, Snackbar.LENGTH_SHORT).show();
                Sort = "CH";
                //今日插画
                break;
            case R.id.today_zh:
                tujianView.showZH(webView);
                Snackbar.make(findViewById(R.id.fab), R.string.load, Snackbar.LENGTH_SHORT).show();
                Sort = "ZH";
                //今日杂烩
                break;
            case R.id.today_bing:
                // TODO: 2019/1/21 创建新的activity显示必应日图 
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
                // TODO: 2019/1/21 创建新的activity显示电脑壁纸 
                //电脑壁纸
                break;
            case R.id.juzi:
                // TODO: 2019/1/21 实现句子显示 
                //句子
                break;
            case R.id.thisapp:
                // TODO: 2019/1/21 显示应用程序关于 
                //关于程序
                break;
            case R.id.thisproject:
                // TODO: 2019/1/21 显示项目介绍(使用activity)，支持跳转至网站 
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
