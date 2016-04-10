package cn.georgeyang.network;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.georgeyang.util.Logutil;


/**
 *
 * Created by george.yang on 16/2/25.
 */
abstract class OkRequestBase {
    protected static final OkHttpClient okHttpClient = new OkHttpClient();
    protected static final String TAG =  "request";
    protected static Context mContext;

    static {
        okHttpClient.setReadTimeout(1, TimeUnit.MINUTES);
        okHttpClient.setConnectTimeout(1, TimeUnit.MINUTES);
        okHttpClient.setWriteTimeout(5,TimeUnit.MINUTES);
    }

    public static void init(Context context) {
        if (mContext==null) {
            File sdcache = new File(context.getCacheDir().getAbsolutePath(),"netCache");
            if (!sdcache.exists()) {
                sdcache.mkdirs();
            }
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            okHttpClient.setCache(new Cache(sdcache.getAbsoluteFile(), cacheSize));
            okHttpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "max-age=6000")
                            .build();
                }
            });
        }
        mContext = context;
    }

    public void cancel (Object requestTag) {
        okHttpClient.getDispatcher().cancel(requestTag);
    }


    private static Method cacheGetMothod;
    public static <T> Resp<T> request(Request request, Type clazz) {
        Resp ret = new Resp();

        try {
            Headers header = request.headers();
            Logutil.i(TAG,"==========>request header:");
            for (String key : header.names()) {
                Logutil.i(TAG,key + ":" + header.get(key));
            }
            if (request.body()!=null) {
                Logutil.i(TAG,"body Length:" + request.body().contentLength());
            }

            Response response = null;
            try {
                Call call = okHttpClient.newCall(request);
                response = call.execute();
            } catch (Exception e) {
                Logutil.i(TAG,"net error use local cache!!" +  e.getMessage());
            }

            if (response==null) {
//                Cache cache = okHttpClient.getCache();
//                if (cacheGetMothod==null) {
//                    cacheGetMothod = cache.getClass().getDeclaredMethod("get",new Class[]{Request.class});
//                    cacheGetMothod.setAccessible(true);
//                }
//                response = (Response) cacheGetMothod.invoke(cache,new Object[]{request});
                response = getCacheResponseByRequest(request);
            }

            ret.code = response.code();
            ret.success = response.isSuccessful();

            Logutil.i(TAG,"response code:" +  response.code());

            if (response.isSuccessful()) {
                Logutil.i(TAG,"==========>response header:");
                header = response.headers();
                for (String key : header.names()) {
                    Logutil.i(TAG,key + ":" + header.get(key));
                }
                String string = response.body().string();
                Logutil.i(TAG,"==========>return:");
                Logutil.i(TAG,string);


                boolean isString = false;
                if (clazz.getClass()==Class.class) {
                    if ( ((Class) clazz) == String.class) {
                        isString = true;
                    }
                }//else getClass -= ImplForType(is List)

                if (isString) {
                    ret.data = string;
                } else {
//                    Logutil.showlog("clazz:" + clazz);
//                    String typeClass = clazz.toString();
                    Gson gson = new Gson();
//                    if (typeClass.startsWith(List.class.getName())) {
//                        Logutil.showlog("is list:" + clazz);
//                        String subClass = typeClass.substring(List.class.getName().length()+1,typeClass.length()-1);
//                        Logutil.showlog("is subClass:" + subClass);
//                        Type subType = Class.forName(subClass);
//                        JSONArray jsonArray = new JSONArray(string);
//                        List list = new ArrayList<>();
//                        for (int i=0;i<jsonArray.length();i++) {
//                            Logutil.showlog("is:" + i);
//                            String jj = jsonArray.getString(i);
//                            Logutil.showlog("json:" + jj);
//                            Object oo = gson.fromJson(jj,TypeToken.get(subType).getType());
//
//                            list.add(oo);
//                        }
//                        ret.data = list;
//
//                        Logutil.i(TAG,"==========>end:" + ret.data);
//                    } else {
//                        ret.data = gson.fromJson(string,clazz);
//                    }

                    ret.data = gson.fromJson(string,clazz);
//                    ret.data = gson.fromJson(string,TypeToken.get(clazz).getType());
                }
            }
            Logutil.i(TAG,"==========>data:" + ret.data);
        } catch (Exception e) {
            ret.error = e;
            Logutil.i(TAG,"==========>error:" + Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return ret;
    }

    public static <T> Resp<T> get (Object requestTag, String url, Map<String,Object> params, Type retClass) {
        pringUrl("get",url,params);
        Request request = buildGetRequest(requestTag,url,params);
        return request(request,retClass);
    }

    public static <T> Resp<T> post(String url, Map<String, Object> params) {
        return post("",url,params,String.class);
    }

    public static Request buildGetRequest(Object requestTag,String url, Map<String, Object> params) {
        Request request = new Request.Builder()
                .url(url + getParamStr(params))
                .tag(requestTag)
                .build();
        return request;
    }


    public static Request buildPostRequest(Object requestTag,String url, Map<String, Object> params) {
        RequestBody body;
        if (!(params==null || params.size()==0)) {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            for (String key:params.keySet()) {
                if (!TextUtils.isEmpty(key)) {
                    builder.add(key,params.get(key)+"");
                }
            }
            body = builder.build();
        } else {
            body =  RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"),"xml");
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(body)
                .tag(requestTag)
                .build();
        return request;
    }


    public static String getPostRequestCache(Object requestTag,String url, Map<String, Object> params) {
        Request request = buildPostRequest(requestTag,url,params);
        return getCacheByRequest(request);
    }

    public static String getGetRequestCache(Object requestTag,String url, Map<String, Object> params) {
        Request request = buildGetRequest(requestTag,url,params);
        return getCacheByRequest(request);
    }

    public static String getCacheByRequest (Request request) {
        Response response = getCacheResponseByRequest(request);
        if (response==null) {
            return null;
        }
        return response.body().toString();
    }

    public static Response getCacheResponseByRequest (Request request) {
        Response response = null;
        try {
            Cache cache = okHttpClient.getCache();
            if (cacheGetMothod == null) {
                cacheGetMothod = cache.getClass().getDeclaredMethod("get", new Class[]{Request.class});
                cacheGetMothod.setAccessible(true);
            }
            response = (Response) cacheGetMothod.invoke(cache, new Object[]{request});
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logutil.showlog("getCacheResponseByRequest>>" + response);
        return response;
    }

    protected static <T> Resp<T> post(Object requestTag,String url, Map<String, Object> params,Type clazz) {
        pringUrl("post",url,params);
        Request request = buildPostRequest(requestTag,url,params);
        return request(request, clazz);
    }

    private static void pringUrl(String method,String url,Map<String, Object> params) {
        Logutil.i(TAG,"==========>" +  method + ":"+ url + getParamStr(params));
    }


    /**
     * 获取参数(得到 ?a=12&b=123)
     *
     * @author ping 2014-4-10 上午9:27:01
     * @param params
     * @return
     */
    private static String getParamStr(Map<String, Object> params) {
        StringBuffer bf = new StringBuffer("?");
        if (params != null) {
            Set<String> mset = params.keySet();
            String[] keys = mset.toArray(new String[mset.size()]);

            for (String key : keys) {
                Object value = params.get(key);
                if (value == null) {
                    params.remove(key);
                } else {
                    bf.append(key + "=" + params.get(key) + "&");
                }
            }
        }
        String str = bf.toString();
        return str.substring(0, str.length() - 1);
    }

}
