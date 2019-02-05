package ml.cerasus.pics.g;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateUtils {
    private String packgetName;
    private int Vname;
    private String Link_update;

    public void UpdateUtils(Context context,String DataLink) throws PackageManager.NameNotFoundException {
        packgetName = context.getPackageName();
        Vname = context.getPackageManager().getPackageInfo(packgetName, 0).versionCode;
        Link_update = DataLink;
    }

    public void start(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(Link_update).method("GET", null).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    public class callback{
        public void failed(){}
        public void ok(String now_v,String new_v,String new_cont){}
    }
}
