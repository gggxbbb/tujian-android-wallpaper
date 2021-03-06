package ml.cerasus.pics.g;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.support.v4.app.ActivityCompat;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import cc.shinichi.library.ImagePreview;
import github.gggxbbb.MyImageViewf;
import github.gggxbbb.SetWallpaperHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //TujianUtils.View tujianView = new TujianUtils.View();
    private String[] APP_PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String img_Link;
    private String img_Title;
    private String img_Content;
    private boolean loadd = false;
    private String img_sort;
    private int img_width;
    private int img_height;
    private String img_pid = "";
    private long click_back = 0;
    private int color;

    private void setColor(int color_input) {
        color = color_input;
    }

    private void loadImg(final String sort) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void run() {
                switch (sort) {
                    case "CH":
                        getSupportActionBar().setTitle(R.string.CH);
                        break;
                    case "ZH":
                        getSupportActionBar().setTitle(R.string.ZH);
                        break;
                    case "SJ":
                        getSupportActionBar().setTitle(R.string.SJ);
                        break;
                }
            }
        });
        final Toolbar toolbar = findViewById(R.id.toolbar);
        double img_show_height;
        double img_show_width;
        if (img_width == 0 || img_height == 0) {
            img_show_height = img_show_width = Target.SIZE_ORIGINAL;
        } else {
            img_show_width = findViewById(R.id.today_show).getWidth();
            double img_show_time = img_show_width / img_width;
            img_show_height = img_height * img_show_time;
            Log.d("Tujian", "onResponse: 质量" + img_show_width + "/" + img_width + ";" + img_show_height + "/" + img_height + ":" + img_show_time);
        }
        final int img_show_width2 = (int) img_show_width;
        final int img_show_height2 = (int) img_show_height;
        loadd = true;
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ImageView img_show = findViewById(R.id.today_show);
                final ImageView img_back = findViewById(R.id.show_back);
                img_back.setImageBitmap(null);
                img_show.setImageBitmap(null);
                //noinspection deprecation
                GlideApp.with(MainActivity.this)
                        .asBitmap()
                        .load(Uri.parse(img_Link))
                        .into(new SimpleTarget<Bitmap>(img_show_width2, img_show_height2) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                getSharedPreferences("main", MODE_PRIVATE).edit().putString("sort", sort).apply();
                                Log.d("Tujian", "onResourceReady: " + resource.getWidth() + ";" + resource.getHeight());
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
                                            setColor(vibrant.getRgb());
                                        } catch (NullPointerException e) {
                                            try {
                                                Palette.Swatch vibrant = palette.getMutedSwatch();
                                                toolbar.setBackgroundColor(vibrant.getRgb());
                                                window.setStatusBarColor(vibrant.getRgb());
                                                window.setNavigationBarColor(vibrant.getRgb());
                                                setColor(vibrant.getRgb());
                                            } catch (NullPointerException ee) {
                                                ee.printStackTrace();
                                            }
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                Bitmap img_back_show = StackBlur.blur(resource, 20, false);
                                img_back.setImageBitmap(img_back_show);
                                img_show.setImageBitmap(resource);

                                //noinspection ConstantConditions
                                getSupportActionBar().setSubtitle(img_Title);
                            }
                        });
            }
        });
    }

    private void showJuZi() {
        Snackbar.make(findViewById(R.id.fab), R.string.load, Snackbar.LENGTH_SHORT).show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://yijuzhan.com/api/word.php").method("GET", null).build();
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
        SharedPreferences sharedPreferences = getSharedPreferences(sort, MODE_PRIVATE);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        final String update_date = simpleDateFormat.format(new Date());
        if (sharedPreferences.getBoolean("update", false) && (!sort.equals("SJ"))) {
            if (Objects.requireNonNull(sharedPreferences.getString("update_date", "")).equals(update_date)) {
                img_Title = sharedPreferences.getString("img_title", "");
                img_Content = sharedPreferences.getString("img_cont", "");
                img_Link = sharedPreferences.getString("img_link", "");
                img_width = sharedPreferences.getInt("img_width", 0);
                img_height = sharedPreferences.getInt("img_height", 0);
                img_pid = sharedPreferences.getString("img_pid", "");
                Log.d("Tujian", "showImage: 使用缓存数据");
                Log.d("Tujian", "showImage: 图片信息\n" + img_Title + "\n" + img_Content + "\n" + img_Link + "\n" + img_width + ";" + img_height);
                loadImg(sort);
            } else {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(R.id.fab), R.string.load, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(findViewById(R.id.fab), R.string.load, Snackbar.LENGTH_SHORT).show();
                }
            });
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        if (Objects.equals(sort, "SJ")) {
            ImgLink = "https://dp.chimon.me/api/random.php?api=yes";
        } else {
            img_sort = sort;
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
                        JSONArray pictures = jsonObject.getJSONArray("pictures");
                        JSONObject picture = pictures.getJSONObject(0);
                        String img_pid2 = picture.getString("PID");
                        if (!img_pid.equals(img_pid2) && sort.equals(img_sort)) {
                            img_Title = picture.getString("p_title");
                            img_Link = picture.getString("p_link");
                            img_Content = picture.getString("p_content");
                            img_width = picture.getIntValue("width");
                            img_height = picture.getIntValue("height");
                            if (sort.equals("SJ")) {
                                loadImg("SJ");
                            } else {
                                SharedPreferences.Editor editor = getSharedPreferences(sort, MODE_PRIVATE).edit();
                                editor.putBoolean("update", true);
                                editor.putString("img_title", img_Title);
                                editor.putString("img_cont", img_Content);
                                editor.putString("img_link", img_Link);
                                editor.putString("img_pid", img_pid2);
                                editor.putInt("img_width", img_width);
                                editor.putInt("img_height", img_height);
                                editor.putString("update_date", update_date);
                                editor.apply();
                                Log.d("Tujian", "onResponse: 图片信息\n" + img_Title + "\n" + img_Content + "\n" + img_Link + "\n" + img_width + ";" + img_height);
                                loadImg(sort);
                            }
                        }
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
                    builder.setTitle(img_Title + " (" + img_width + "*" + img_height + ")");
                    builder.setMessage(img_Content);
                    builder.setPositiveButton(R.string.knew, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton(R.string.setwall, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            //noinspection deprecation
                            Glide.with(MainActivity.this).asBitmap().load(Uri.parse(img_Link)).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    /*
                                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(MainActivity.this);
                                    try {
                                        wallpaperManager.setBitmap(resource);
                                        Snackbar.make(findViewById(R.id.fab), R.string.setd, Snackbar.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        Snackbar.make(findViewById(R.id.fab), R.string.setf, Snackbar.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                    */
                                    SetWallpaperHelper setWallpaperHelper = new SetWallpaperHelper(MainActivity.this);
                                    if (!setWallpaperHelper.forMIUI(resource)) {
                                        if (!setWallpaperHelper.forEMUI(resource)) {
                                            if (!setWallpaperHelper.byCropImage(resource)) {
                                                if (!setWallpaperHelper.byChooseActivity(resource)) {
                                                    if (!setWallpaperHelper.byWallpaperManager(resource)) {
                                                        Snackbar.make(findViewById(R.id.fab), R.string.setf, Snackbar.LENGTH_LONG).show();
                                                    } else {
                                                        Snackbar.make(findViewById(R.id.fab), R.string.setd, Snackbar.LENGTH_LONG).show();
                                                    }
                                                }
                                            }
                                        }
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
                ImagePreview.getInstance().setContext(MainActivity.this).setIndex(0).setImage(img_Link).setFolderName("Pictures/Tujian").start();
                return false;
            }
        });


        MyImageViewf buttom_show = findViewById(R.id.show_his);
        /*
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
        */
        buttom_show.setGoH(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                intent.putExtra("sort", img_sort);
                intent.putExtra("anim", true);
                intent.putExtra("main_color", color);
                startActivity(intent);
            }
        });
        buttom_show.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_group_collapse_00));

        imageView.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                String sort;
                sort = intent.getStringExtra("sort");
                if (sort == null) {
                    showImage(img_sort);
                } else {
                    showImage(sort);
                }
            }
        });
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

            /*
            shortcutInfos.add(new ShortcutInfo.Builder(MainActivity.this, "bing")
                    .setShortLabel(getResources().getString(R.string.BingDaily))
                    .setLongLabel(getResources().getString(R.string.BingDaily))
                    .setIcon(Icon.createWithResource(MainActivity.this, R.drawable.ic_camera_black_24dp))
                    .setIntent(new Intent(MainActivity.this, BingActivity.class).setAction(Intent.ACTION_VIEW))
                    .build());
            */

            shortcutInfos.add(new ShortcutInfo.Builder(MainActivity.this, "CH")
                    .setShortLabel(getResources().getString(R.string.CH))
                    .setLongLabel(getResources().getString(R.string.CH) + "-" + getResources().getString(R.string.today))
                    .setIcon(Icon.createWithResource(MainActivity.this, R.mipmap.ic_today_round))
                    .setIntent(new Intent(MainActivity.this, MainActivity.class).setAction(Intent.ACTION_VIEW).putExtra("sort", "CH"))
                    .build());

            shortcutInfos.add(new ShortcutInfo.Builder(MainActivity.this, "ZH")
                    .setShortLabel(getResources().getString(R.string.ZH))
                    .setLongLabel(getResources().getString(R.string.ZH) + "-" + getResources().getString(R.string.today))
                    .setIcon(Icon.createWithResource(MainActivity.this, R.mipmap.ic_today_round))
                    .setIntent(new Intent(MainActivity.this, MainActivity.class).setAction(Intent.ACTION_VIEW).putExtra("sort", "ZH"))
                    .build());

            shortcutInfos.add(new ShortcutInfo.Builder(MainActivity.this, "HZH")
                    .setShortLabel(getResources().getString(R.string.ZH))
                    .setLongLabel(getResources().getString(R.string.ZH) + "-" + getResources().getString(R.string.history))
                    .setIcon(Icon.createWithResource(MainActivity.this, R.mipmap.ic_history_round))
                    .setIntent(new Intent(MainActivity.this, HistoryActivity.class).setAction(Intent.ACTION_VIEW).putExtra("sort", "ZH"))
                    .build());

            shortcutInfos.add(new ShortcutInfo.Builder(MainActivity.this, "HCH")
                    .setShortLabel(getResources().getString(R.string.CH))
                    .setLongLabel(getResources().getString(R.string.CH) + "-" + getResources().getString(R.string.history))
                    .setIcon(Icon.createWithResource(MainActivity.this, R.mipmap.ic_history_round))
                    .setIntent(new Intent(MainActivity.this, HistoryActivity.class).setAction(Intent.ACTION_VIEW).putExtra("sort", "CH"))
                    .build());

            shortcutInfos.add(new ShortcutInfo.Builder(MainActivity.this, "HCP")
                    .setShortLabel(getResources().getString(R.string.ComputerWallpaper))
                    .setLongLabel(getResources().getString(R.string.ComputerWallpaper))
                    .setIcon(Icon.createWithResource(MainActivity.this, R.mipmap.ic_computer_round))
                    .setIntent(new Intent(MainActivity.this, HistoryActivity.class).setAction(Intent.ACTION_VIEW).putExtra("sort", "CP"))
                    .build());

            shortcutManager.setDynamicShortcuts(shortcutInfos);
            Log.d("Tujian", "onCreate: shortcut");

        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, APP_PERMISSION, 0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() - click_back > 2000) {
                Snackbar.make(findViewById(R.id.fab), R.string.click_back, Snackbar.LENGTH_SHORT).show();
                click_back = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
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
                if (loadd) {
                    showImage("CH");
                } else {
                    Snackbar.make(findViewById(R.id.fab), R.string.click_too_many, Snackbar.LENGTH_SHORT).show();
                }
                //今日插画
                break;
            case R.id.today_zh:
                //tujianView.showZH(webView);
                if (loadd) {
                    showImage("ZH");
                } else {
                    Snackbar.make(findViewById(R.id.fab), R.string.click_too_many, Snackbar.LENGTH_SHORT).show();
                }
                //今日杂烩
                break;
            case R.id.today_bing:
                startActivity(new Intent(MainActivity.this, BingActivity.class));
                //今日必应
                break;
            case R.id.history_ch:
                intent.putExtra("sort", "CH");
                intent.putExtra("main_color", color);
                startActivity(intent);
                //插画归档
                break;
            case R.id.history_zh:
                intent.putExtra("sort", "ZH");
                intent.putExtra("main_color", color);
                startActivity(intent);
                //杂烩归档
                break;
            case R.id.compaper:
                intent.putExtra("sort", "CP");
                intent.putExtra("main_color", color);
                startActivity(intent);
                //电脑壁纸
                break;
            case R.id.juzi:
                showJuZi();
                //句子
                break;
            case R.id.thisapp:
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.About);
                builder.setMessage(R.string.aboutAppMessage);
                builder.setPositiveButton(R.string.knew, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNeutralButton("Github", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gggxbbb/tujian-android-wallpaper"));
                        startActivity(intent1);
                    }
                });
                builder.show();
                */
                startActivity(new Intent(MainActivity.this,AboutActivity.class).putExtra("main_color", color));
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
