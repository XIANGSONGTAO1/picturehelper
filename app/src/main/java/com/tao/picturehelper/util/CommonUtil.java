package com.tao.picturehelper.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.baidu.aip.ocr.AipOcr;

import org.json.JSONException;


public class CommonUtil { // 请求url
    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static String generalBasic(String path,String token) {
        long start = System.currentTimeMillis();
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        try {
            // 本地文件路径
            String filePath = path;
            Bitmap bitmap = BitmapAndStringUtils.getBitmap(path);
            Bitmap bitmap1 = BitmapAndStringUtils.compressImage(bitmap);
            String temp = BitmapAndStringUtils.convertIconToString(bitmap1);
//            File file = new File(filePath);
//            byte[] imgData = FileUtil.readFileByBytes(filePath);
//            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(temp, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = token;

            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            long time = System.currentTimeMillis()-start;
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+time+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getOrderIdByTime() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate=sdf.format(new Date());
        String result="";
        Random random=new Random();
        for(int i=0;i<3;i++){
            result+=random.nextInt(10);
        }
        return newDate+result;
    }
    public static boolean judgeThreadByName(String threadName){

        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];

        currentGroup.enumerate(lstThreads);
        for (int i = 0; i < noThreads; i++) {
            String nm = lstThreads[i].getName();
            if (nm.equals(threadName)) {
                return true;
            }
        }
        return false;
    }
    public static String getCurrentLanguage(){
        String locale = Locale.getDefault().getLanguage();
        return locale;
    }
    public static String sample(AipOcr client, String img) {
        String wordsFinal = "";
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        options.put("probability", "true");

        // 参数为本地图片二进制数组
        byte[] file = readImageFile(img);


        Long start = System.currentTimeMillis();

        org.json.JSONObject res = client.basicGeneral(file, options);
        Long end = System.currentTimeMillis();
        Long time = end - start;
        System.out.println(time);
        System.out.println("******************************************");

        try {
            int num = res.getInt("words_result_num");
            if (num > 0) {
                org.json.JSONArray jsonArray = res.getJSONArray("words_result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    org.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String words = jsonObject.getString("words");
                    wordsFinal += words;
                }
            }

//            System.out.println(res.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wordsFinal;


    }

    private static Bitmap changeMapSize(Bitmap bit) {
        double MaxSize = 200.00;//图片允许最大空间
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] b = bos.toByteArray();//字节
        //字节转换为 KB
        double mid = b.length / 1024;//KB
        Bitmap bitmap1 = null;
        if (MaxSize < mid) {
            //图片超过规定大小
            double n = mid / MaxSize; //允许压缩倍数
            double newWidth = bit.getWidth() / n;
            double newHeight = bit.getHeight() / n;
            Matrix matrix = new Matrix();
            matrix.postScale(((float) newWidth) / bit.getWidth(), ((float) newHeight) / bit.getHeight());
            bitmap1 = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), matrix, true);
        } else {
            bitmap1 = bit;
        }
        return bitmap1;
    }

    private static byte[] readImageFile(String path) {
        Bitmap bitMap = BitmapAndStringUtils.getBitmap(path);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap2 = changeMapSize(bitMap);
        bitmap2.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}