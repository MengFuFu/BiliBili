package com.example.bilibili.data.api;

import com.example.bilibili.model.bean.BangumiItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * B站接口定义
 */
public interface BiliApiService {

    //热门视频列表：ps 是每页条数，pn是页码
    @GET("x/web-interface/popular")
    Call<PopularResponse> getPopular(@Query("ps") int ps, @Query("pn") int pn);

    //番剧索引
    @GET("pgc/season/index/result")
    Call<BangumiResponse> getBangumi(@Query("type") int type,
                                     @Query("season_type") int seasonType,
                                     @Query("pagesize") int pageSize,
                                     @Query("page") int page);

    //分区排行榜：rid是分区id，day是榜单天数
    @GET("x/web-interface/ranking/region")
    Call<RegionRankingResponse> getRegionRanking(@Query("rid") int rid, @Query("day") int day);

    //专栏推荐文章列表：cid = 0表示全部
    @GET("x/article/recommends")
    Call<ArticleResponse> getArticles(@Query("cid") int cid,
                                      @Query("pn") int pn,
                                      @Query("ps") int ps);
}
