package github.gggxbbb.tujian;

import android.webkit.WebView;

public class TujianUtils {
    public static class View {
        public void showZH(WebView webView){
            webView.loadUrl("https://dp.chimon.me/fapp/today.php?sort=杂烩");
        }
        public void showCH(WebView webView){
            webView.loadUrl("https://dp.chimon.me/fapp/today.php?sort=二次元");
        }
        public void showBing(WebView webView){
            webView.loadUrl("https://dp.chimon.me/fapp/bing.php");
        }
        public void showHistoryZH(WebView webView){
            webView.loadUrl("https://dp.chimon.me/fapp/old.php?sort=杂烩");
        }
        public void showHistoryCH(WebView webView){
            webView.loadUrl("https://dp.chimon.me/fapp/old.php?sort=二次元");
        }
        public void showCom(WebView webView){
            webView.loadUrl("https://dp.chimon.me/fapp/old.php?sort=电脑壁纸");
        }
    }
}
