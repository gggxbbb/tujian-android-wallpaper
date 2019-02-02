package ml.cerasus.pics.g;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.graphics.Palette;
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
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;


import net.qiujuer.genius.blur.StackBlur;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private String img_sort;
    private int img_width;
    private int img_height;


    private void showJuZi() {
        Snackbar.make(findViewById(R.id.fab), R.string.load, Snackbar.LENGTH_SHORT).show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("https://dp.chimon.me/api/hitokoto.php").method("GET", null).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Snackbar.make(findViewById(R.id.fab), R.string.loadf, Snackbar.LENGTH_LONG).show();
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String htmlStr = response.body().string();
                Log.d("Tujian", "onResponse: " + htmlStr);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.Juzi);
                        builder.setMessage(htmlStr);
                        builder.setPositiveButton(R.string.done_read, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setNegativeButton(R.string.nextJ, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showJuZi();
                            }
                        });
                        builder.setNeutralButton(R.string.copy, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clipData = ClipData.newPlainText("text", htmlStr);
                                clipboardManager.setPrimaryClip(clipData);
                                Snackbar.make(findViewById(R.id.fab), R.string.done_read, Snackbar.LENGTH_LONG).show();
                            }
                        });
                        builder.show();
                    }
                });
            }
        });
    }

    private void showImage(final String sort) {
        loadd = false;
        String Link = "https://dp.chimon.me/api/today.php?sort=";
        String ImgLink = "";
        final Toolbar toolbar = findViewById(R.id.toolbar);
        if (Objects.equals(sort, "SJ")) {
            ImgLink = "https://dp.chimon.me/api/random.php?api=yes";
        } else {
            img_sort = sort;
            getSharedPreferences("main", MODE_PRIVATE).edit().putString("sort", sort).apply();
            switch (sort) {
                case "CH":
                    ImgLink = Link + "二次元";
                    break;
                case "ZH":
                    ImgLink = Link + "杂烩";
                    break;
            }
        }
        toolbar.setSubtitle(null);
        setTitle(R.string.app_name);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(ImgLink).method("GET", null).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Snackbar.make(findViewById(R.id.fab), R.string.loadf, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String htmlStr = Objects.requireNonNull(response.body()).string();
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
                        double img_show_height, img_show_width;
                        JSONArray pictures = jsonObject.getJSONArray("pictures");
                        JSONObject picture = pictures.getJSONObject(0);
                        img_Title = picture.getString("p_title");
                        final String imgLink = picture.getString("p_link");
                        img_Link = imgLink;
                        img_Content = picture.getString("p_content");
                        img_width = picture.getIntValue("width");
                        img_height = picture.getIntValue("height");
                        Log.d("Tujian", "onResponse: 图片信息\n" + img_Title + "\n" + img_Content + "\n" + img_Link + "\n" + img_width + ";" + img_height);
                        if (img_width == 0 || img_height == 0) {
                            img_show_height = img_show_width = Target.SIZE_ORIGINAL;
                        } else {
                            img_show_width = findViewById(R.id.today_show).getWidth();
                            double img_show_time = img_show_width / img_width;
                            img_show_height = img_height * img_show_time;
                            Log.d("Tujian", "onResponse: 质量" + img_show_width + ";" + img_show_height + ":" + img_show_time);
                        }
                        final int img_show_width2 = (int) img_show_width;
                        final int img_show_height2 = (int) img_show_height;
                        loadd = true;
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //noinspection deprecation
                                GlideApp.with(MainActivity.this).asBitmap().load(Uri.parse(imgLink)).into(new SimpleTarget<Bitmap>(img_show_width2, img_show_height2) {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        Log.d("Tujian", "onResourceReady: " + resource.getWidth() + ";" + resource.getHeight());
                                        ImageView img_show = findViewById(R.id.today_show);
                                        ImageView img_back = findViewById(R.id.show_back);
                                        toolbar.setSubtitle(img_Title);
                                        final Window window = getWindow();
                                        Palette.Builder builder = new Palette.Builder(resource);
                                        builder.generate(new Palette.PaletteAsyncListener() {
                                            @SuppressWarnings("ConstantConditions")
                                            @Override
                                            public void onGenerated(@Nullable Palette palette) {
                                                try {
                                                    Palette.Swatch vibrant = palette.getVibrantSwatch();
                                                    toolbar.setBackgroundColor(vibrant.getRgb());
                                                    window.setStatusBarColor(vibrant.getRgb());
                                                    window.setNavigationBarColor(vibrant.getRgb());
                                                } catch (NullPointerException e) {
                                                    try {
                                                        Palette.Swatch vibrant = palette.getMutedSwatch();
                                                        toolbar.setBackgroundColor(vibrant.getRgb());
                                                        window.setStatusBarColor(vibrant.getRgb());
                                                        window.setNavigationBarColor(vibrant.getRgb());
                                                    } catch (NullPointerException ee) {
                                                        ee.printStackTrace();
                                                    }
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                        Bitmap img_back_show = StackBlur.blur(resource, 20, false);
                                        img_back.setImageBitmap(null);
                                        img_back.setImageBitmap(img_back_show);
                                        img_show.setImageBitmap(null);
                                        img_show.setImageBitmap(resource);
                                    }
                                });
                                switch (sort) {
                                    case "CH":
                                        setTitle(R.string.CH);
                                        break;
                                    case "ZH":
                                        setTitle(R.string.ZH);
                                        break;
                                    case "SJ":
                                        setTitle(R.string.SJ);
                                        break;
                                }
                            }
                        });
                    } catch (JSONException e) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showImage("SJ");
                            }
                        });

                        e.printStackTrace();
                    }
                } else {
                    //String err = getResources().getString(R.string.loadf) + ifok;
                    //Snackbar.make(findViewById(R.id.fab), err, Snackbar.LENGTH_LONG).show();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showImage("SJ");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img_sort = getSharedPreferences("main", MODE_PRIVATE).getString("sort", "ZH");
        Toolbar toolbar = findViewById(R.id.toolbar);
        //NestedScrollView scro = findViewById(R.id.today_scro);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadd) {
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
                            //noinspection deprecation
                            Glide.with(MainActivity.this).asBitmap().load(Uri.parse(img_Link)).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(MainActivity.this);
                                    try {
                                        wallpaperManager.setBitmap(resource);
                                        Snackbar.make(findViewById(R.id.fab), R.string.setd, Snackbar.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        Snackbar.make(findViewById(R.id.fab), R.string.setf, Snackbar.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                    builder.setNeutralButton(R.string.share, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String shareMessage = String.format(getResources().getString(R.string.shareMessage), img_Title, img_Content, img_Link);
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                } else {
                    Snackbar.make(findViewById(R.id.fab), R.string.load, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ImageView imageView = findViewById(R.id.today_show);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ImagePreview.getInstance().setContext(MainActivity.this).setIndex(0).setImage(img_Link).start();
                return false;
            }
        });


        ImageView buttom_show = findViewById(R.id.show_his);
        buttom_show.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                intent.putExtra("sort", img_sort);
                intent.putExtra("anim", true);
                startActivity(intent);
                return false;
            }
        });

        showImage(img_sort);

        //tujianView.showCH(webView);

        if (!getSharedPreferences("first", MODE_PRIVATE).getBoolean("help_first", false)) {
            TapTargetView.showFor(MainActivity.this,
                    TapTarget.forView(findViewById(R.id.fab), "", getResources().getString(R.string.help_clickAndLong))
                            .dimColor(android.R.color.darker_gray)
                            .textColor(android.R.color.black)
                            .outerCircleColor(android.R.color.white)
                            .transparentTarget(true)
                            .drawShadow(true)
                            .cancelable(false)
                            .tintTarget(false)
            );
            Log.d("Tujian", "onCreate: Show help.");
            getSharedPreferences("first", MODE_PRIVATE).edit().putBoolean("help_first", true).apply();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            List<ShortcutInfo> shortcutInfos = new ArrayList<>();
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

            shortcutInfos.add(new ShortcutInfo.Builder(MainActivity.this, "bing")
                    .setShortLabel(getResources().getString(R.string.BingDaily))
                    .setLongLabel(getResources().getString(R.string.BingDaily))
                    .setIcon(Icon.createWithResource(MainActivity.this, R.drawable.ic_camera_black_24dp))
                    .setIntent(new Intent(MainActivity.this, BingActivity.class).setAction(Intent.ACTION_VIEW))
                    .build());

            shortcutManager.setDynamicShortcuts(shortcutInfos);
            Log.d("Tujian", "onCreate: shortcut");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        switch (id) {
            case R.id.action_help:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.help)
                        .setMessage(R.string.HelpMessage)
                        .setPositiveButton(R.string.knew, null)
                        .show();
                break;
        }

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        //侧栏点击事件
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
                startActivity(new Intent(MainActivity.this, BingActivity.class));
                //今日必应
                break;
            case R.id.history_ch:
                intent.putExtra("sort", "CH");
                intent.putExtra("mian_color", findViewById(R.id.toolbar).getDrawingCacheBackgroundColor());
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
                showJuZi();
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
            case R.id.tgbot:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Tujiansays")));
                break;
            case R.id.qqchat:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqapi://card/show_pslcard?src_type=internal&verson=1&uin=472863370&card_type=group&source=qrcode")));
                break;
            case R.id.p_list:
                break;
            default:
                Toast.makeText(MainActivity.this, R.string.nothing, Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onLowMemory() {
        Glide.get(MainActivity.this).clearMemory();
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(MainActivity.this).clearMemory();
        }
        Glide.get(MainActivity.this).trimMemory(level);
        super.onTrimMemory(level);
    }
}
