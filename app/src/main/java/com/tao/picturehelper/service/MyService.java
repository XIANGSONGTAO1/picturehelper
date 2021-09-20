package com.tao.picturehelper.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.IBinder;
import android.provider.MediaStore;

import com.baidu.aip.ocr.AipOcr;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.Word;
import com.baidu.ocr.sdk.model.WordSimple;
import com.tao.picturehelper.db.DBManager;
import com.tao.picturehelper.util.BitmapAndStringUtils;
import com.tao.picturehelper.util.CommonUtil;
import com.tao.picturehelper.util.RecognizeService;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MyService extends Service {
    Cursor cursor;
    //    AipOcr aipOcr = new AipOcr("23556839", "tMmy60Dz7T36U9alZSLVrPUV", "LAQPhOpiKgsIDrL8ON1VIf5OnyWbdlez");
    String syString = new String();
    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.MINI_THUMB_MAGIC,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 服务第一次创建的时候调用
     */
    @Override
    public void onCreate() {
        super.onCreate();
        initAccessToken();
    }

    public void recGeneralBasic(Context ctx, String filePath, final RecognizeService.ServiceListener listener) {
        GeneralParams param = new GeneralParams();
        param.setDetectDirection(true);
        param.setVertexesLocation(true);
        param.setRecognizeGranularity(GeneralParams.GRANULARITY_SMALL);
        System.out.println(filePath);
        param.setImageFile(new File(filePath));

        OCR.getInstance(ctx).recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                StringBuilder sb = new StringBuilder();
                for (WordSimple wordSimple : result.getWordList()) {
                    String tmp = wordSimple.getWords();
                    sb.append(tmp);
                }
                DBManager.addPicture(filePath, sb.toString());
            }

            @Override
            public void onError(OCRError error) {
                listener.onResult(error.getMessage());
            }
        });
    }

    //    private void initAccessTokenWithAkSk() {
//        OCR.getInstance(getApplicationContext()).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
//            @Override
//            public void onResult(AccessToken result) {
//                String token = result.getAccessToken();
//            }
//
//            @Override
//            public void onError(OCRError error) {
//                error.printStackTrace();
//
//            }
//        }, getApplicationContext(), "tMmy60Dz7T36U9alZSLVrPUV", "LAQPhOpiKgsIDrL8ON1VIf5OnyWbdlez");
//    }
    private void initAccessToken() {
        OCR.getInstance(getApplicationContext()).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
            }
        }, getApplicationContext());
    }

    /**
     * 服务每一次启动的时候调用
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IdentityThread identityThread = new IdentityThread();
        new Thread(identityThread, "identityThread").start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public String sample(AipOcr client, String img) {
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
    AipOcr aipOcr = new AipOcr("23556839","tMmy60Dz7T36U9alZSLVrPUV","LAQPhOpiKgsIDrL8ON1VIf5OnyWbdlez");
    public class IdentityThread implements Runnable {
        @Override
        public void run() {

            List<String> x = DBManager.queryAllPath();
//        List<String> x = (List<String>) intent.getSerializableExtra("data");
            if (x == null || x.size() == 0) {
                stopSelf();
            } else {
                for (String string : x) {

                    String dbResult = DBManager.queryPicrureByPath(string);
                    if (dbResult == null) {
                        String result = CommonUtil.sample(aipOcr,string);
                        DBManager.addPicture(string, result);
//                        recGeneralBasic(getApplicationContext(), string, new RecognizeService.ServiceListener() {
//                            @Override
//                            public void onResult(String result) {
//
//                            }
//                        });
                    }
                }
            }
        }
    }

}
