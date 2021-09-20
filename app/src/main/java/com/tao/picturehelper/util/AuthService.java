package com.tao.picturehelper.util;

import android.annotation.SuppressLint;

import com.alibaba.fastjson.JSONObject;
import com.tao.picturehelper.db.DBManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AuthService {
    private static Calendar expireDate;
    /**
     * 判断token是否过期
     */
    private static boolean flag = false; // 是否已经获取过了

    @SuppressLint("WrongConstant")
    public static Boolean needAuth() {
        Calendar c = Calendar.getInstance();
        c.add(5, 1); // 当前日期加一天
        return Boolean.valueOf(!flag || c.after(expireDate));
    }

    /**
     * 获取权限token
     *
     * @return 返回示例： { "access_token":
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000 }
     */
    public static String getAuth() {
        // 官网获取的 API Key 更新为你注册的
        String clientId = "dKzpnMPZ93VyKkcwX4GChdBB";
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = "KDNAzw7VnnqZHBmlrfCu79RMwKDMs6w7";

        flag = true;

        return getAuth(clientId, clientSecret);
    }

    /**
     * 获取API访问token 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     *
     * @param clientId     - 百度云官网获取的 API Key
     * @param clientSecret - 百度云官网获取的 Securet Key
     * @return assess_token
     */
    @SuppressLint("WrongConstant")
    public static String getAuth(String clientId, String clientSecret) {
        Long strart = System.currentTimeMillis();
        String access_token = "";
        int tokenCount = DBManager.getTokenCount();
        Map<String,Object> tokenMap = null;
        List<Map<String,Object>>tokenList = DBManager.queryAllToken();
        if (tokenList!=null&&tokenList.size()!=0){
            tokenMap = tokenList.get(0);
        }

        Date expireTime = null;
        if (tokenCount<=0){

            Map<String,Object> map = netGetToken(clientId,clientSecret);
            access_token = (String) map.get("access_token");
            expireTime = (Date) map.get("expire_date");
            DBManager.addToken(access_token,expireTime);
        }else if (tokenMap!=null){
            Date timeDate = null;
            String timeTemp = (String) tokenMap.get("expireTime");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                timeDate = simpleDateFormat.parse(timeTemp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (timeDate.before(new Date())){
                Map<String,Object> map = netGetToken(clientId,clientSecret);
                access_token = (String) map.get("access_token");
                expireTime = (Date) map.get("expire_time");
                DBManager.updateToken(access_token,expireTime);
            }else{
                access_token = (String) tokenMap.get("token");
            }
        }
        Long time = System.currentTimeMillis()-strart;
        System.out.println("******************"+time+"***********************************");
        return access_token;

    }
    @SuppressLint("WrongConstant")
    public static Map<String,Object> netGetToken(String clientId, String clientSecret){
        Map<String,Object> resMap = new HashMap<>();
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + clientId
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + clientSecret;

        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.connect();

            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String access_token = jsonObject.getString("access_token");

            Integer expires_in = Integer.valueOf(String.valueOf(jsonObject.get("expires_in")));

            System.out.println("expires_in:" + expires_in);

            Calendar c = Calendar.getInstance();
            System.out.println("现在日期：" + c.get(c.YEAR) + "/" + c.get(c.MONTH) + "/" + c.get(c.DAY_OF_MONTH));
            c.add(13, expires_in.intValue());
            System.out.println("过期日期：" + c.get(c.YEAR) + "/" + c.get(c.MONTH) + "/" + c.get(c.DAY_OF_MONTH));
            expireDate = c;
            Date expireTime = expireDate.getTime();
            resMap.put("access_token",access_token);
            resMap.put("expire_date",expireTime);
            return resMap;

        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }
    @SuppressLint("WrongConstant")
    public static Date getExpireDate(String result){
        Calendar expireDate = null;

        JSONObject jsonObject = JSONObject.parseObject(result);
        String access_token = jsonObject.getString("access_token");

        Integer expires_in = Integer.valueOf(String.valueOf(jsonObject.get("expires_in")));

        System.out.println("expires_in:" + expires_in);

        Calendar c = Calendar.getInstance();
        System.out.println("现在日期：" + c.get(c.YEAR) + "/" + c.get(c.MONTH) + "/" + c.get(c.DAY_OF_MONTH));
        c.add(13, expires_in.intValue());
        System.out.println("过期日期：" + c.get(c.YEAR) + "/" + c.get(c.MONTH) + "/" + c.get(c.DAY_OF_MONTH));
        expireDate = c;
        return expireDate.getTime();
    }

    public static void main(String[] args) {

        System.out.println("flag:" + flag);

        System.out.println("needAuth():" + needAuth().booleanValue());

        //第一次请求
        if (needAuth().booleanValue()) {
            String access_token = getAuth();

            System.out.println("flag:" + flag);

            System.out.println("access_token:" + access_token);
        } else {
            System.out.println("token未过期，不需要重新获取");
        }

        //第二次请求
        if (needAuth().booleanValue()) {
            String access_token = getAuth();

            System.out.println("flag:" + flag);

            System.out.println("access_token:" + access_token);
        } else {
            System.out.println("token未过期，不需要重新获取");
        }

    }

}
