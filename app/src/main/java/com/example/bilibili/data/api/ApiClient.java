package com.example.bilibili.data.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 全局唯一的网络客户端
 * 1. 把OkHttp + Retrofit 的创建代码集中，避免每个Repository重复写
 * 2. 统一给所有请求加浏览器 User-Agent，绕开B站风控
 */
public class ApiClient {

    //饿汉式单例：类加载时就创建一次，线程安全
    private static final ApiClient INSTANCE = new ApiClient();

    //真正发请求的Retrofit接口对象
    private final BiliApiService mApi;

    //构造方法私有化
    private ApiClient() {
        //自定义OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    // 拿到原始请求，再包一层加了 header 的新请求
                    Request request = chain.request().newBuilder()
                            .addHeader("User-Agent",
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                            "Chrome/120.0.0.0 Safari/537.36")
                            .build();
                    return chain.proceed(request); // 继续走这个新请求
                }).build();

        //创建Retrofit，指定 B 站接口基础地址 + JSON 解析器
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.bilibili.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //动态生成BiliApiService的实现类
        mApi = retrofit.create(BiliApiService.class);
    }

    //给外部用的唯一入口
    public static BiliApiService getApi() {
        return INSTANCE.mApi;
    }
}
