package com.tao.picturehelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.tao.picturehelper.db.DBManager;
import com.tao.picturehelper.util.CommonUtil;
import com.tao.picturehelper.util.HttpClientClass;
import com.tao.picturehelper.util.YungouosUtils;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyCenterActivity extends AppCompatActivity implements View.OnClickListener {
    TextView usernameTV;
    TextView vipLevleTV;
    TextView pictureCountTV;
    TextView logoutTv;
    ImageView vip1;
    ImageView vip2;
    ImageView vip3;
    TextView vip1_num;
    TextView vip2_num;
    TextView vip3_num;
    boolean isChina = true;
    // Replace with your application client ID and secret
    String clientId = "AVipe4dEaLsdN2gQ1OFKaeGcPkzxkVqIXaCp-_a7spo67ROJVYEdA9vhhwHQlEHhYPGdsUwl0nh86Xcm";
    String clientSecret = "EL6GWCxC-RekX9qBZO0iH_GbgJwcdyXTRwCQUcqq7zqsrAY9kuCV_hs50bIjaHTTHigpDXUP3z4WoD3t";

    APIContext context = new APIContext(clientId, clientSecret, "live");
    //    //配置何种支付环境，一般沙盒，正式
//    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
//    private static final String DEFAULT_CURRENCY = "USD";
//    //你所注册的APP Id
//    private static final String CONFIG_CLIENT_ID = "AWQlT4UrMcVRre_QZK9LxrWWzPDldTpGJUlfS9itJXbPTPyvc8H6lPp0oiH2IyobVZ_jDY7dwlIygpht";
//    private static final int REQUEST_CODE_PAYMENT = 1;
//    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
//    private static final int REQUEST_CODE_PROFILE_SHARING = 3;
//    private static PayPalConfiguration paypalConfig = new PayPalConfiguration().environment(CONFIG_ENVIRONMENT)
//            .clientId(CONFIG_CLIENT_ID);
    //以下配置是授权支付的时候用到的
//.merchantName("Example Merchant")
// .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
//.merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String res = (String) msg.obj;
            switch (msg.what) {
                case 2:
                    JSONObject jsonObject = JSON.parseObject(res);
                    Integer errcode = jsonObject.getInteger("errcode");
                    String errMsg = jsonObject.getString("errmsg");

                    Map<String, Object> info = (Map<String, Object>) jsonObject.get("data");
                    if (errcode != 200) {
                        Toast.makeText(getApplicationContext(), "退出失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_SHORT).show();
                        DBManager.deleteAllUser();
//                        DBManager.deleteAllpicture();
                        DBManager.deleteAllPaths();
                        DBManager.deleteAllVips();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case 3:
                    JSONObject jsonObjectx = JSON.parseObject(res);
                    Integer errcodex = jsonObjectx.getInteger("errcode");
                    String errMsgx = jsonObjectx.getString("errmsg");
                    if (errcodex == 200) {
//                        Toast.makeText(getApplicationContext(), "账单入库成功", Toast.LENGTH_SHORT);
                    }
                    break;
                case 4:
                    JSONObject jsonObject1 = JSON.parseObject(res);
                    Integer code = jsonObject1.getInteger("code");
                    String data = jsonObject1.getString("data");
                    if (code == 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                PayTask alipay = new PayTask(MyCenterActivity.this);
                                Map<String, String> result = alipay.payV2(data, true);
                            }
                        }).start();
                    }
                    break;
                case 5:
                    JSONObject jsonObject5 = JSON.parseObject(res);
                    Integer code5 = jsonObject5.getInteger("errcode");
                    JSONArray jsonArray5 = jsonObject5.getJSONArray("data");
                    if (code5 == 200) {
                        for (int i = 0; i < jsonArray5.size(); i++) {
                            JSONObject jsonObject2 = (JSONObject) jsonArray5.get(i);
                            Integer vipLevel = (Integer) jsonObject2.get("vipLevel");
                            BigDecimal price = (BigDecimal) jsonObject2.get("price");
                            BigDecimal dollarPrice = (BigDecimal) jsonObject2.get("dollarPrice");
                            Integer picNum = (Integer) jsonObject2.get("picNum");
                            DBManager.udpateVip(String.valueOf(vipLevel), String.valueOf(price), String.valueOf(picNum), String.valueOf(dollarPrice));
                        }
                    }
                    break;
                case 6:
                    JSONObject jsonObject6 = JSON.parseObject(res);
                    Integer code6 = jsonObject6.getInteger("errcode");
                    if (code6 == 200) {
                        JSONArray jsonArray6 = jsonObject6.getJSONArray("data");
                        JSONObject jsonObject6_1 = (JSONObject) jsonArray6.get(0);
                        String username = (String) jsonObject6_1.get("username");
                        Integer vipLevel = (Integer) jsonObject6_1.get("vipLevel");
                        DBManager.udpateUserVipLevel(username, vipLevel);
                    }
                    break;

            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.vip1:
                Map<String, String> mapLevel1 = DBManager.queryVipByVipLevel(String.valueOf(1));
                String price = mapLevel1.get("price");
                String dollarPrice = mapLevel1.get("dollarPrice");
                if (isChina) {

                    alipayMoney(price, "初级vip订单","1");
                } else {
                    paypalMoney(dollarPrice, "初级vip订单","1");
                }
                break;
            case R.id.vip2:
                Map<String, String> mapLevel2 = DBManager.queryVipByVipLevel(String.valueOf(2));
                String price2 = mapLevel2.get("price");
                String dollarPrice2 = mapLevel2.get("dollarPrice");

                if (isChina) {
                    alipayMoney(price2, "中级vip订单","2");
                } else {
                    paypalMoney(dollarPrice2, "中级vip订单","2");
                }
                break;
            case R.id.vip3:
                Map<String, String> mapLevel3 = DBManager.queryVipByVipLevel(String.valueOf(3));
                String price3 = mapLevel3.get("price");
                String dollarPrice3 = mapLevel3.get("dollarPrice");

                if (isChina) {
                    alipayMoney(price3, "高级vip订单","3");
                } else {
                    paypalMoney(dollarPrice3, "高级vip订单","3");
                }
                break;
            case R.id.logout:
                String username = usernameTV.getText().toString();
                String st = "http://www.kakaxc.com/user/logOut?username=" + username;//json接口
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                HttpClientClass httpclient = new HttpClientClass(st, "GET", "JSON", map, handler, 2);
                httpclient.start();
                break;

        }
    }

    public void alipayMoney(String totalFee, String bodyMes,String vipLevel) {
        Map<String, String> mapx = new LinkedHashMap<String, String>();
        String outOrderNo = CommonUtil.getOrderIdByTime();
        String usernamex = usernameTV.getText().toString();

        String stx = "http://www.kakaxc.com/order/addOrder?outTradeNum=" + outOrderNo + "&username=" + usernamex + "&vipLevel=" + vipLevel + "&money=" + totalFee+ "&dollarFlag=0";//json接口
        HttpClientClass httpClientClass = new HttpClientClass(stx, "GET", "JSON", mapx, handler, 3);
        httpClientClass.start();

        String stx2 = "https://api.pay.yungouos.com/api/pay/alipay/appPay";//json接口
        Map<String, String> map = new HashMap<>();
        map.put("out_trade_no", outOrderNo);
        map.put("total_fee", totalFee);
        map.put("mch_id", "2088110266662534");
        map.put("body", bodyMes);
        String sign = YungouosUtils.createSign(map, "D63F5C7B0968406BAE8BD2AF4C292810");
        map.put("notify_url", "http://www.kakaxc.com/order/updateOrder");
        map.put("sign", sign);
        HttpClientClass httpClientClass2 = new HttpClientClass(stx2, "POST", "Form", map, handler, 4);
        httpClientClass2.start();
    }

    public void paypalMoney(String totalFee, String bodyMes,String vipLevel) {
        // Set payer details
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

// Set redirect URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://www.kakaxc.com/order/doPalpayExec");
        redirectUrls.setReturnUrl("http://www.kakaxc.com/order/doPalpayExec");

// Set payment details
        Details details = new Details();
        details.setShipping("0");
        details.setSubtotal(totalFee);
        details.setTax("0");

// Payment amount
        Amount amount = new Amount();
        amount.setCurrency("USD");
// Total must be equal to sum of shipping, tax and subtotal.
        amount.setTotal(totalFee);
        amount.setDetails(details);

// Transaction information
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction
                .setDescription("This is the payment transaction description.");

// Add transaction to a list
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

// Add payment details
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setRedirectUrls(redirectUrls);
        payment.setTransactions(transactions);
        // Create payment
        try {
            Payment createdPayment = payment.create(this.context);

            Iterator links = createdPayment.getLinks().iterator();
            while (links.hasNext()) {
                Links link = (Links) links.next();
                if (link.getRel().equalsIgnoreCase("approval_url")) {
                    Map<String, String> mapx = new LinkedHashMap<String, String>();
                    String usernamex = usernameTV.getText().toString();
                    String stx = "http://www.kakaxc.com/order/addOrder?outTradeNum=" + createdPayment.getId() + "&username=" + usernamex + "&vipLevel=" + vipLevel + "&money=" + totalFee+ "&dollarFlag=1";//json接口
                    HttpClientClass httpClientClass = new HttpClientClass(stx, "GET", "JSON", mapx, handler, 3);
                    httpClientClass.start();
                    Intent httpIntent = new Intent(Intent.ACTION_VIEW);

                    httpIntent.setData(Uri.parse(link.getHref()));

                    startActivity(httpIntent);
                    // Redirect the customer to link.getHref()
                }
            }
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String language = CommonUtil.getCurrentLanguage();
        if (!language.equals("zh")) {
            isChina = false;
//            Intent intent = new Intent(this, PayPalService.class);
//            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
//            startService(intent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_center);
        usernameTV = (TextView) findViewById(R.id.username);
        vipLevleTV = (TextView) findViewById(R.id.vipLevle);
        pictureCountTV = (TextView) findViewById(R.id.picturecount);
        logoutTv = (TextView) findViewById(R.id.logout);
        vip1 = (ImageView) findViewById(R.id.vip1);
        vip2 = (ImageView) findViewById(R.id.vip2);
        vip3 = (ImageView) findViewById(R.id.vip3);
        vip1_num = (TextView) findViewById(R.id.vip1_num);
        vip2_num = (TextView) findViewById(R.id.vip2_num);
        vip3_num = (TextView) findViewById(R.id.vip3_num);
        Map<String, String> mapLevel1 = DBManager.queryVipByVipLevel(String.valueOf(1));
        String vip1_num_string = mapLevel1.get("picNum");
        String vip1_price_string = mapLevel1.get("price");
        String dollarPrice1 = mapLevel1.get("dollarPrice");
        if (!isChina) {
            vip1_price_string = dollarPrice1;
        }
        vip1_num.setText(vip1_price_string + this.getString(R.string.yuan) + "          " + vip1_num_string + this.getString(R.string.pictures));
        Map<String, String> mapLevel2 = DBManager.queryVipByVipLevel(String.valueOf(2));
        String vip1_num_string2 = mapLevel2.get("picNum");
        String vip1_price_string2 = mapLevel2.get("price");
        String dollarPrice2 = mapLevel2.get("dollarPrice");
        if (!isChina) {
            vip1_price_string2 = dollarPrice2;
        }
        vip2_num.setText(vip1_price_string2 + this.getString(R.string.yuan) + "         " + vip1_num_string2 + this.getString(R.string.pictures));
        Map<String, String> mapLevel3 = DBManager.queryVipByVipLevel(String.valueOf(3));
        String vip1_num_string3 = mapLevel3.get("picNum");
        String vip1_price_string3 = mapLevel3.get("price");
        String dollarPrice3 = mapLevel3.get("dollarPrice");
        if (!isChina) {
            vip1_price_string3 = dollarPrice3;
        }
        vip3_num.setText(vip1_price_string3 + this.getString(R.string.yuan) + "          " + vip1_num_string3 + this.getString(R.string.pictures));
        Map<String, Object> map = DBManager.queryUser();
        String username = (String) map.get("username");
        String vipLevel = (String) map.get("vipLevel");
        usernameTV.setText(username);
        if (vipLevel.equals("1")) {
            vipLevleTV.setText(this.getString(R.string.primaryVip));
        } else if (vipLevel.equals("2")) {
            vipLevleTV.setText(this.getString(R.string.intermediateVip));
        } else if (vipLevel.equals("3")) {
            vipLevleTV.setText(this.getString(R.string.seniorVip));
        } else {
            vipLevleTV.setText(this.getString(R.string.commonUser));
        }
        Map<String, String> mapLevel4 = DBManager.queryVipByVipLevel(vipLevel);
        if (mapLevel4 != null) {
            String vip1_num_string4 = mapLevel4.get("picNum");
            pictureCountTV.setText(vip1_num_string4 + this.getString(R.string.pictures));

        }
        vip1.setOnClickListener(this);
        vip2.setOnClickListener(this);
        vip3.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
        String st = "http://www.kakaxc.com/vip/getAllVip";
        Map<String, Object> mapx = new LinkedHashMap<String, Object>();
        HttpClientClass httpclient = new HttpClientClass(st, "GET", "JSON", mapx, handler, 5);
        httpclient.start();
        String st2 = "http://www.kakaxc.com/user/getVipByUsername?username=" + username;
        Map<String, Object> mapx2 = new LinkedHashMap<String, Object>();
        HttpClientClass httpclient2 = new HttpClientClass(st2, "GET", "JSON", mapx2, handler, 6);
        httpclient2.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (data == null) {
//            return;
//        }
//
//        PaymentConfirmation confirm = data
//                .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//
//        String paymentId;
//        try {
//            paymentId = confirm.toJSONObject().getJSONObject("response")
//                    .getString("id");
//
//            String payment_client = confirm.getPayment().toJSONObject()
//                    .toString();
//
//            Log.e("", "paymentId: " + paymentId + ", payment_json: " + payment_client);
//            // TODO ：把paymentId和payment_json传递给自己的服务器以确认你的款项是否收到或者收全
//            // TODO ：得到服务器返回的结果，你就可以跳转成功页面或者做相应的处理了
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    @Override
    public void onDestroy() {
//        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

}
