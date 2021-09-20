package com.tao.picturehelper.util;

import android.content.Context;
import android.content.ContextWrapper;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;

public class OrcUtil {

    public static void initAccessTokenWithAkSk(Context context) {
        boolean result = false;

        ContextWrapper contextWrapper = new ContextWrapper(context);
        OCR aipOcr = OCR.getInstance(context);
        aipOcr.initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                System.out.println("AK，SK方式获取token失败");
            }
        }, context,  "dKzpnMPZ93VyKkcwX4GChdBB", "KDNAzw7VnnqZHBmlrfCu79RMwKDMs6w7");
    }
}
