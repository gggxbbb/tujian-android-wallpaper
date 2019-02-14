package ml.cerasus.pics.g;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        int color = intent.getIntExtra("main_color", getResources().getColor(R.color.colorPrimary));
        Window window = getWindow();
        toolbar.setBackgroundColor(color);
        window.setStatusBarColor(color);
        window.setNavigationBarColor(color);
        findViewById(R.id.toolbar).setBackgroundColor(color);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setBackgroundColor(color);
        collapsingToolbarLayout.setStatusBarScrimColor(color);
        collapsingToolbarLayout.setContentScrimColor(color);
        findViewById(R.id.app_bar).setBackgroundColor(color);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gggxbbb/tujian-android-wallpaper"));
                startActivity(intent1);
            }
        });
    }
}
