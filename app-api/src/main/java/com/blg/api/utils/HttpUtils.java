package com.blg.api.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     *
     * @param params 请求参数，集合形式
     * @param host
     * @param port
     * @param path
     */
    public HttpEntity HttpGetMethod(Map<String, String> paramMap, String host,
                                           Integer port, String path) throws IOException, URISyntaxException {
        // 设置uri信息,并将参数集合放入uri;
        URI uri = new URIBuilder().setScheme("http").setHost(host)
                .setPort(port).setPath(path).setParameters(convertParamsToList(paramMap)).build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet(uri);
        return doGet(httpGet);
    }

    /**
     * 构建 HttpGet 请求，用?拼接请求参数
     * panhongtong
     *
     * @param uri
     * @param params
     */
    public HttpEntity HttpGetMethod(String uri, StringBuffer params) throws IOException {
        // 创建Get请求
        HttpGet httpGet = new HttpGet(uri + "?" + params);
        return doGet(httpGet);
    }

    /**
     * 发送请求
     * panhongtong
     */
    private HttpEntity doGet(HttpGet httpGet) throws IOException {
        // 配置信息
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000).setSocketTimeout(5000).setRedirectsEnabled(true).build();
        httpGet.setConfig(requestConfig);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             // 由客户端执行(发送)Get请求,将结果封装到响应模型中
             CloseableHttpResponse response = httpClient.execute(httpGet)
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
     *
     * @param url         请求路径
     * @param paramObject 参数对象
     */
    public HttpEntity doPost(String url, Object paramObject) throws IOException {
        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);
        String jsonString = JSON.toJSONString(paramObject);
        StringEntity entity = new StringEntity(jsonString, "UTF-8");
        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             // 由客户端执行(发送)Post请求，封装到响应模型中
             CloseableHttpResponse response = httpClient.execute(httpPost)) {
            // 从响应模型中获取响应实体
            return response.getEntity();
        } catch (Exception e) {
            log.error("发送post请求失败，失败路径为" + url);
            throw e;
        }
    }


    /**
     * 发送POST请求
     * panhongtong
     *
     * @param url
     * @param jsonStr JSON字符串
     */
    public HttpEntity doPost(String url, String jsonStr) throws IOException {
        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(jsonStr, "UTF-8");
        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             // 由客户端执行(发送)Post请求，封装到响应模型中
             CloseableHttpResponse response = httpClient.execute(httpPost)) {
            // 从响应模型中获取响应实体
            return response.getEntity();
        } catch (Exception e) {
            log.error("发送post请求失败，失败路径为" + url, e);
            throw e;
        }
    }

    /**
     * 发送post请求，默认参数编码格式为UTF-8
     *
     * @param url      请求路径
     * @param paramMap Map参数
     */
    public HttpEntity doPost(String url, Map<String, String> paramMap) {
        return doPost(url, paramMap, StandardCharsets.UTF_8);
    }

    /**
     * 发送post请求
     *
     * @param url      请求路径
     * @param paramMap Map参数
     * @param charset  编码格式
     */
    public HttpEntity doPost(String url, Map<String, String> paramMap, Charset charset) {
        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);
        //构建表单参数
        httpPost.setEntity(convertParamsToEntity(paramMap, charset));
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             // 由客户端执行(发送)Post请求，封装到响应模型中
             CloseableHttpResponse response = httpClient.execute(httpPost)) {
            // 从响应模型中获取响应实体
            return response.getEntity();
            //String jsonAccessToken = EntityUtils.toString(responseEntity, "UTF-8");
        } catch (ParseException | IOException e) {
            log.error("发送post请求失败，失败路径为" + url, e);
            return null;
        }
    }

    /**
     * 将map参数转换为 List<BasicNameValuePair>
     *
     * @param paramMap 参数
     */
    public List<NameValuePair> convertParamsToList(Map<String, String> paramMap) {
        List<NameValuePair> params = new ArrayList<>();
        paramMap.forEach((key, val) -> params.add(new BasicNameValuePair(key, val)));
        return params;
    }

    /**
     * 将map参数转换为 UrlEncodedFormEntity
     * 默认编码格式为UTF-8
     *
     * @param paramMap 参数
     */
    public UrlEncodedFormEntity convertParamsToEntity(Map<String, String> paramMap) {
        return convertParamsToEntity(paramMap, StandardCharsets.UTF_8);
    }

    /**
     * 将map参数转换为 List<BasicNameValuePair> -> UrlEncodedFormEntity
     *
     * @param paramMap 参数
     * @param charset  编码格式
     */
    public UrlEncodedFormEntity convertParamsToEntity(Map<String, String> paramMap, Charset charset) {
        List<BasicNameValuePair> params = new ArrayList<>();
        paramMap.forEach((key, val) -> params.add(new BasicNameValuePair(key, val)));
        return new UrlEncodedFormEntity(params, charset);
    }

    /**
     * 将响应体转换为JSON字符串，默认编码格式为UTF-8
     *
     * @param responseEntity 响应实体
     */
    public String responseToString(HttpEntity responseEntity) {
        return responseToString(responseEntity, StandardCharsets.UTF_8);
    }

    /**
     * 将响应体转换为JSON字符串
     *
     * @param responseEntity 响应实体
     * @param charset        编码格式
     */
    public String responseToString(HttpEntity responseEntity, Charset charset) {
        String result;
        try {
            result = EntityUtils.toString(responseEntity, charset);
        } catch (IOException e) {
            log.error("响应转换发生异常", e);
            return null;
        }
        return result;
    }
}
