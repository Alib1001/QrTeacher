package com.app.qrteachernavigation.API;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.qrteachernavigation.models.TimeTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://ec2-3-93-201-72.compute-1.amazonaws.com:8083";
    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    String jwtToken = preferences.getString("jwtToken", "");

                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + jwtToken)
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static List<TimeTable> parseJsonToTimeTableList(String json) {
        Type listType = new TypeToken<List<TimeTable>>(){}.getType();
        return new Gson().fromJson(json, listType);
    }

    public static String parseTimeTableListToJson(List<TimeTable> timeTableList) {
        Gson gson = new Gson();
        return gson.toJson(timeTableList);
    }


}