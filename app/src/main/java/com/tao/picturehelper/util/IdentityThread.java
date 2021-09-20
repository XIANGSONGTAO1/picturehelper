package com.tao.picturehelper.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;

import com.baidu.aip.ocr.AipOcr;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IdentityThread extends Thread {
    private String img;
    private AipOcr client;
    private Handler handler;
    private int what;
    private int identyCount;


    public IdentityThread(String img, AipOcr client, Handler handler,int what,int identyCount) {
        this.client = client;
        this.img = img;
        this.handler = handler;
        this.what = what;
        this.identyCount=identyCount;
    }

    @Override
    public void run() {
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
        Message message = new Message();
        message.what=what;
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("tipInfo","正在读取第"+identyCount+"张图片");
        resultMap.put("identityResult",wordsFinal);
        resultMap.put("path",img);
        message.obj=resultMap;
        handler.sendMessage(message);


        super.run();
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
}
