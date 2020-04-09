package com.blg.framework.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @Auther: panhongtong
 * @Date: 2020/3/30 16:57
 * @Description: httpclient工具类
 */
@Slf4j
public class HttpUtils {

    /**
     * 构建 HttpGet 请求，拼接请求路径
     * panhongtong
     * @param params 请求参数，集合形式
     * @param host
     * @param port
     * @param path
     */
    public static HttpEntity HttpGetMethod(List<NameValuePair> params, String host,
                                Integer port, String path) throws IOException, URISyntaxException {
        // 设置uri信息,并将参数集合放入uri;
        URI uri = new URIBuilder().setScheme("http").setHost(host)
                .setPort(port).setPath(path).setParameters(params).build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet(uri);
        return doGet(httpGet);
    }

    /**
     * 构建 HttpGet 请求，用?拼接请求参数
     * panhongtong
     * @param uri
     * @param params
     */
    public static HttpEntity HttpGetMethod(String uri,StringBuffer params) throws IOException {
        // 创建Get请求
        HttpGet httpGet = new HttpGet(uri + "?" + params);
        return doGet(httpGet);
    }

    /**
     * 发送请求
     * panhongtong
     */
    private static HttpEntity doGet(HttpGet httpGet) throws IOException {
        // 配置信息
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000).setSocketTimeout(5000).setRedirectsEnabled(true).build();
        httpGet.setConfig(requestConfig);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             // 由客户端执行(发送)Get请求,将结果封装到响应模型中
             CloseableHttpResponse response = httpClient.execute(httpGet);
        ) {
            // 从响应模型中获取响应实体
            return response.getEntity();
        } catch (IOException e) {
            log.error("发送get请求失败，失败路径为" + httpGet.getURI());
            throw e;
        }
    }

    /**
     * 发送POST请求
     * panhongtong
     * @param url 请求路径
     * @param paramObject 参数对象
     */
    public static HttpEntity doPost(String url,Object paramObject) throws IOException {
        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);
        String jsonString = JSON.toJSONString(paramObject);
        StringEntity entity = new StringEntity(jsonString, "UTF-8");
        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            // 由客户端执行(发送)Post请求，封装到响应模型中
            CloseableHttpResponse response = httpClient.execute(httpPost)) {
            // 从响应模型中获取响应实体
            //return EntityUtils.toString(responseEntity, "UTF-8");
            return response.getEntity();
        } catch (Exception e) {
            log.error("发送post请求失败，失败路径为" + url);
            throw  e;
        }
    }


    /**
     * 发送POST请求
     * panhongtong
     * @param url
     * @param jsonStr JSON字符串
     */
    public static HttpEntity doPostString(String url,String jsonStr) throws IOException {
        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(jsonStr, "UTF-8");
        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            // 由客户端执行(发送)Post请求，封装到响应模型中
            CloseableHttpResponse response = httpClient.execute(httpPost)) {
            // 从响应模型中获取响应实体
            return response.getEntity();
        } catch (Exception e) {
            log.error("发送post请求失败，失败路径为" + url);
            throw  e;
        }
    }
}
