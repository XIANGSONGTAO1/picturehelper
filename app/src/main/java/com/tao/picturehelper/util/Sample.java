package com.tao.picturehelper.util;

import com.baidu.aip.ocr.AipOcr;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Sample {
    public static final String APP_ID = "23443463";
    public static final String API_KEY = "dKzpnMPZ93VyKkcwX4GChdBB";
    public static final String SECRET_KEY = "KDNAzw7VnnqZHBmlrfCu79RMwKDMs6w7";

    public void sample(AipOcr client,byte[] img) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        options.put("probability", "true");


        // 参数为本地图片路径
        String image = "test.jpg";
//        JSONObject res = client.basicGeneral(image, options);
//        System.out.println(res.toString(2));

//        // 参数为本地图片二进制数组
//        byte[] file = readImageFile(image);
//        JSONObject res = client.basicGeneral(file, options);
//        System.out.println(res.toString(2));


//        // 通用文字识别, 图片参数为远程url图片
//        JSONObject res = client.basicGeneralUrl(url, options);
//        System.out.println(res.toString(2));

    }
    public static void main(String[] args) {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        String path = "/storage/emulated/0/Pictures/Screenshots/u=2651205068,3289469537&fm=26&gp=0.jpg";
        JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
        try {
            System.out.println(res.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
