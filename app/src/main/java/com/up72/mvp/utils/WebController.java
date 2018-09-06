package com.up72.mvp.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.up72.library.utils.Log;
import com.up72.mvp.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebView 控制器
 * Created by LYF on 2016/11/13.
 */
public class WebController {
    private static final String NOT_NETWORK_URL = "file:///android_asset/web/index.html";
    private WebView webView;
    private List<String> historyUrl = new ArrayList<>();//历史URL
    private Callback mCallback;
    private Log log = new Log(getClass());

    @SuppressLint("SetJavaScriptEnabled")
    public WebController(WebView webView, Callback callback) {
        this.webView = webView;
        this.mCallback = callback;
        WebSettings settings = webView.getSettings();
        if (settings != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                settings.setLoadsImagesAutomatically(true);
            } else {
                settings.setLoadsImagesAutomatically(false);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                //5.0以上默认不支持https、http混合模式，一下代码开启混合模式
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);//只从网络获取
//            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//只读本地缓存
            settings.setDomStorageEnabled(true);
            settings.setJavaScriptEnabled(true);
            settings.setDefaultTextEncodingName("UTF-8");
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            webView.setWebViewClient(new MyWebViewClient());
        }
    }

    public void loadUrl(String url) {
        if (webView != null && url != null && url.length() > 0) {
            if (mCallback != null) {
                mCallback.loading(true);
            }
            String str = url.toLowerCase();
            if (!str.startsWith("http://") && !str.startsWith("https://")) {
                url = Constants.baseHostUrl + url;
            }
            webView.loadUrl(url);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            log.e(url);
            String str = url.toLowerCase();
            if (str.startsWith("http://") || str.startsWith("https://")) {
                Map<String, String> params = parseParams(url);
                if (params == null || !params.containsKey("noHistory") || !params.get("noHistory").equals("true")) {
                    if (historyUrl.size() > 0 && historyUrl.get(historyUrl.size() - 1).equals("")) {
                        historyUrl.remove(historyUrl.size() - 1);
                    }
                    historyUrl.add(url);
                } else {
                    if (historyUrl.size() <= 0 || !historyUrl.get(historyUrl.size() - 1).equals("")) {
                        historyUrl.add("");
                    }
                }
                view.loadUrl(url);
            } else {
                parse(url);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mCallback != null) {
                mCallback.loading(false);
            }
            if (!webView.getSettings().getLoadsImagesAutomatically()) {
                webView.getSettings().setLoadsImagesAutomatically(true);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            view.loadUrl(NOT_NETWORK_URL);
        }
    }

    /**
     * 解析URL中的参数
     *
     * @param url http://或https://开头的URL
     */
    private Map<String, String> parseParams(@NonNull String url) {
        int index = url.indexOf("?") + 1;
        if (index > 0 && index < url.length()) {
            String params = url.substring(index);
            String[] paramArr = params.split("&");
            Map<String, String> result = new HashMap<>();
            for (String param : paramArr) {
                String[] arr = param.split("=");
                if (arr.length == 2) {
                    result.put(arr[0], arr[1]);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * 解析特定URL
     *
     * @param url 非http://和https://开头的URL
     */
    private void parse(@NonNull String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String fun = "";
        JSONObject jsonParams = null;
        int index = url.indexOf("{");
        if (index > -1 && index < url.length()) {
            fun = url.substring(0, index).toLowerCase();
            try {
                jsonParams = new JSONObject(url.substring(index));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        switch (fun) {
            case "refresh:"://重载当前页面
                if (webView != null) {
                    if (historyUrl != null && historyUrl.size() > 0) {
                        webView.loadUrl(historyUrl.get(historyUrl.size() - 1));
                    }
                }
                break;
        }
    }

    /**
     * 回退(返回上一个页面，会重新加载URL)
     */
    public void goBack() {
        if (webView != null) {
            webView.clearHistory();
        }
        if (historyUrl != null && historyUrl.size() > 1) {
            historyUrl.remove(historyUrl.size() - 1);
            if (webView != null) {
                webView.loadUrl(historyUrl.get(historyUrl.size() - 1));
            }
            return;
        }
        if (mCallback != null) {
            mCallback.onFinish();
        }
    }

    public interface Callback {
        void loading(boolean show);

        void onFinish();
    }
}