package com.lbank.java.api.sdk.client;

import com.lbank.java.api.sdk.constant.Contant;
import com.lbank.java.api.sdk.security.AuthenticationInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

/**
 * @author steel.cheng
 */
public class LBankJavaApiSdkServiceGenerator {

    /**
     * 实例化
     *
     * @param serviceClass
     * @return
     */
    public static <S> S createService(Class<S> serviceClass, String apiKey,String secret,String signMethod) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Contant.BASE_URL).addConverterFactory(JacksonConverterFactory.create());
        Retrofit retrofit = builder.build();

        if (!StringUtils.isEmpty(apiKey) && !StringUtils.isEmpty(secret) && !StringUtils.isEmpty(signMethod)) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(apiKey,secret,signMethod);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
                //添加日志拦截器
                httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC));
                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }
        return retrofit.create(serviceClass);
    }

    /**
     * @param call
     * @return
     * @throws Exception
     */
    public static <T> T executeSync(Call<T> call) throws Exception {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw new Exception("error");
            }
        } catch (IOException e) {
            throw new Exception(e);
        }
    }
}