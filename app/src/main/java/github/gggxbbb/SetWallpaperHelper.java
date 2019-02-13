package github.gggxbbb;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;

public class SetWallpaperHelper {
    private Context context;
    private String[] APP_PERMISSION={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public SetWallpaperHelper(Context context_use){
        context=context_use;
    }

    public boolean byWallpaperManager(Bitmap bitmap){
        try{
            WallpaperManager wallpaperManager = (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);
            if(bitmap!=null){
                wallpaperManager.setBitmap(bitmap);
                return true;
            }else {
                return false;
            }
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean byCropImage(Bitmap bitmap){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity)context, APP_PERMISSION, 0);
            }
        }
        try{
            Intent intent = new Intent("com.android.camera.CropImage");
            @SuppressWarnings("deprecation")
            int width = context.getWallpaperDesiredMinimumWidth();
            @SuppressWarnings("deprecation")
            int height = context.getWallpaperDesiredMinimumHeight();
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);
            intent.putExtra("aspectX", width);
            intent.putExtra("aspectY", height);
            intent.putExtra("scale", true);
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("setWallpaper", true);
            intent.putExtra("data", Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,null, null)));
            context.startActivity(intent);
            return true;
        } catch (RuntimeException e1){
            e1.printStackTrace();
            return false;
        }
    }

    public boolean byChooseActivity(Bitmap bitmap){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity)context, APP_PERMISSION, 0);
            }
        }
        try {
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("mimeType", "image/*");
            intent.setData(Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,null, null)));
            context.startActivity(intent);
            return true;
        } catch (NullPointerException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public boolean forMIUI(Bitmap bitmap){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity)context, APP_PERMISSION, 0);
            }
        }
        try{
            ComponentName componentName = new ComponentName("com.android.thememanager","com.android.thememanager.activity.WallpaperDetailActivity");
            Intent intent = new Intent("miui.intent.action.START_WALLPAPER_DETAIL");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,null, null)),"image/*" );
            intent.putExtra("mimeType", "image/*");
            intent.setComponent(componentName);
            context.startActivity(intent);
            return true;
        }catch(RuntimeException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean forEMUI(Bitmap bitmap){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity)context, APP_PERMISSION, 0);
            }
        }
        try{
            ComponentName componentName = new ComponentName("com.android.gallery3d","com.android.gallery3d.app.Wallpaper");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,null, null)),"image/*" );
            intent.putExtra("mimeType", "image/*");
            intent.setComponent(componentName);
            context.startActivity(intent);
            return true;
        }catch(RuntimeException e){
            e.printStackTrace();
            return false;
        }
    }
}
