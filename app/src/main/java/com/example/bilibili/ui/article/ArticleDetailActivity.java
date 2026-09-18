package com.example.bilibili.ui.article;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bilibili.R;

/**
 * 文章详细页：用WebView加载B站文章页
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private static final String EXTRA_URL = "extra_url";
    private static final String EXTRA_TITLE = "extra_title";

    public static void startActivity(Context context, String url, String title) {
        Intent intent = new Intent(context, ArticleDetailActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        String url = getIntent().getStringExtra(EXTRA_URL);
        String title = getIntent().getStringExtra(EXTRA_TITLE);

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title != null ? title : "文章");

        //返回按钮
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        WebView webView = findViewById(R.id.web_view);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        // 用桌面 UA，避免 B 站手机页强制跳客户端
        settings.setUserAgentString(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/120.0.0.0 Safari/537.36");
        // 让链接在 WebView 内部打开，不跳系统浏览器
        webView.setWebViewClient(new WebViewClient());

        if(url != null && !url.isEmpty()) {
            webView.loadUrl(url);
        }
    }
}
