package com.tao.picturehelper.util;

import com.alibaba.fastjson.JSONObject;
import com.tao.picturehelper.config.WxPayApiConfig;
import com.tao.picturehelper.exception.PayException;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;

import static org.xutils.common.util.MD5.md5;

public class YungouosUtils {
    public static String packageSign(Map<String, String> params, boolean urlEncoder) {
        // 先将参数以其参数名的字典序升序进行排序
        TreeMap<String, String> sortedParams = new TreeMap<String, String>(params);
        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> param : sortedParams.entrySet()) {
            String value = param.getValue();
            if (StringUtils.isBlank(value)) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(param.getKey()).append("=");
            if (urlEncoder) {
                try {
                    value = urlEncode(value);
                } catch (UnsupportedEncodingException e) {
                }
            }
            sb.append(value);
        }
        return sb.toString();
    }

    public static String urlEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, Charsets.UTF_8.name()).replace("+", "%20");
    }

    public static String createSign(Map<String, String> params, String partnerKey) {
        // 生成签名前先去除sign
        params.remove("sign");
        String stringA = packageSign(params, false);
        String stringSignTemp = stringA + "&key=" + partnerKey;
        return md5(stringSignTemp).toUpperCase();
    }
    public static String H5Pay(String out_trade_no, String total_fee, String mch_id, String body, String attach, String notify_url, String return_url, String config_no, String auto, String auto_node,
                               String key) throws PayException {
        Map<String, Object> params = new HashMap<String, Object>();
        String resultUrl = null;
        try {
            if (StrUtil.isBlank(out_trade_no)) {
                throw new PayException("订单号不能为空！");
            }
            if (StrUtil.isBlank(total_fee)) {
                throw new PayException("付款金额不能为空！");
            }
            if (StrUtil.isBlank(mch_id)) {
                throw new PayException("商户号不能为空！");
            }
            if (StrUtil.isBlank(body)) {
                throw new PayException("商品描述不能为空！");
            }
            if (StrUtil.isBlank(key)) {
                throw new PayException("支付密钥不能为空！");
            }
            params.put("out_trade_no", out_trade_no);
            params.put("total_fee", total_fee);
            params.put("mch_id", mch_id);
            params.put("body", body);
            // 上述必传参数签名
            String sign = PaySignUtil.createSign(params, key);
            params.put("attach", attach);
            params.put("notify_url", notify_url);
            params.put("return_url", return_url);
            params.put("config_no", config_no);
            params.put("auto", auto);
            params.put("auto_node", auto_node);
            params.put("sign", sign);
            String result = HttpRequest.post(WxPayApiConfig.wapPayUrl).form(params).execute().body();
            if (StrUtil.isBlank(result)) {
                throw new PayException("API接口返回为空，请联系客服");
            }
            JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
            if (jsonObject == null) {
                throw new PayException("API结果转换错误");
            }
            Integer code = jsonObject.getInteger("code");
            if (0 != code.intValue()) {
                throw new PayException(jsonObject.getString("msg"));
            }
            resultUrl = jsonObject.getString("data");
        } catch (PayException e) {
            e.printStackTrace();
            throw new PayException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new PayException(e.getMessage());
        }
        return resultUrl;
    }

}
