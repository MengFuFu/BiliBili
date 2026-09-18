package com.example.bilibili.data.repository;

import android.util.Log;

import com.example.bilibili.data.api.ApiClient;
import com.example.bilibili.data.api.ArticleResponse;
import com.example.bilibili.data.api.BiliApiService;
import com.example.bilibili.model.bean.ArticleItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 专栏数据仓库
 */
public class ColumnRepository {

    private static final String TAG = "ColumnRepository";

    public interface LoadCallback {
        void onResult(List<ArticleItem> items);
        void onError();
    }

    private final BiliApiService mApi;

    public ColumnRepository() {
        mApi = ApiClient.getApi();
    }

    public void loadArticles(final LoadCallback callback) {
        mApi.getArticles(0, 1, 20).enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                ArticleResponse body = response.body();
                if(body == null || body.code != 0) {
                    Log.e(TAG, "biz error, code=" + (body != null ? body.code : "null"));
                    callback.onError();
                    return;
                }
                callback.onResult(mapToArticleItems(body));
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                Log.e(TAG, "loadArticles failed", t);
                callback.onError();
            }
        });
    }

    private List<ArticleItem> mapToArticleItems(ArticleResponse response) {
        List<ArticleItem> items = new ArrayList<>();
        if(response == null || response.data == null) {
            return items;
        }

        for(ArticleResponse.Article article : response.data) {
            String title = article.title;
            String summary = article.summary != null ? article.summary : "";
            String author = article.author != null ? article.author.name : "";
            String view = formatView(article.stats != null ? article.stats.view : 0);

            ArticleItem item = new ArticleItem(title, summary, author, view);
            item.setCover(pickCover(article));
            items.add(item);
        }

        return items;
    }

    // 封面优先取第一张配图，没有再用 banner_url
    private String pickCover(ArticleResponse.Article article) {
        if (article.image_urls != null && !article.image_urls.isEmpty()) {
            return article.image_urls.get(0);
        }
        return article.banner_url;
    }

    // 阅读数格式化
    private String formatView(int view) {
        if (view >= 10000) {
            return String.format("%.1f万阅读", view / 10000.0);
        }
        return view + "阅读";
    }
}
