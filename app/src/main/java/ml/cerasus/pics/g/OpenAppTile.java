package ml.cerasus.pics.g;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

import java.lang.reflect.Method;

@RequiresApi(api = Build.VERSION_CODES.N)
public class OpenAppTile extends TileService {

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        tile.setState(Tile.STATE_ACTIVE);
        tile.updateTile();
        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onTileAdded() {
        Tile tile = getQsTile();
        tile.setState(Tile.STATE_ACTIVE);
        tile.updateTile();
        super.onTileAdded();
    }

    @Override
    public void onClick() {
        try {
            @SuppressLint("WrongConstant")
            Object tools = getSystemService("statusbar");
            Method method;
            @SuppressLint("PrivateApi")
            Class<?> clazz = Class.forName("android.app.StatusBarManager");
            method = clazz.getMethod("collapsePaneks");
            method.setAccessible(true);
            method.invoke(tools);
        }catch (Exception e){
            e.printStackTrace();
        }
        startActivity(new Intent(OpenAppTile.this,MainActivity.class));
        super.onClick();
    }
}
