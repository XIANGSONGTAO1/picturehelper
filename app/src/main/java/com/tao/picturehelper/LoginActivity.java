package com.tao.picturehelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tao.picturehelper.db.DBManager;
import com.tao.picturehelper.util.HttpClientClass;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    HttpClientClass httpclient;
    EditText Euesrname;
    EditText Epassword;
    TextView login;
    TextView regist;
    String restResult;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String res = (String) msg.obj;
            switch (msg.what) {
                case 1:
                    JSONObject jsonObject = JSON.parseObject(res);
                    Integer errcode = jsonObject.getInteger("errcode");
                    String errMsg = jsonObject.getString("errmsg");
                    if (errcode != 200) {
                        if (errcode==400){
                            Toast.makeText(getApplicationContext(), getApplication().getString(R.string.name_or_pass_not_null), Toast.LENGTH_SHORT).show();
                        }
                        if (errcode==401){
                            Toast.makeText(getApplicationContext(), getApplication().getString(R.string.name_or_pass_not_null), Toast.LENGTH_SHORT).show();
                        }
                        if (errcode==403){
                            Toast.makeText(getApplicationContext(), getApplication().getString(R.string.alredy_login), Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        JSONObject jsonObject1 = (JSONObject) jsonObject.get("data");
                        JSONArray jsonArray = (JSONArray) jsonObject1.getJSONArray("vips");
                        for (int i = 0;i<jsonArray.size();i++){
                            JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
                            Integer vipLevel = (Integer) jsonObject2.get("vipLevel");
                            BigDecimal price = (BigDecimal) jsonObject2.get("price");
                            BigDecimal dollarPrice = (BigDecimal) jsonObject2.get("dollarPrice");
                            Integer picNum = (Integer) jsonObject2.get("picNum");
                            DBManager.addVip(String.valueOf(vipLevel),String.valueOf(price),String.valueOf(picNum), String.valueOf(dollarPrice));
                        }
                        Map<String, Object> info = (Map<String, Object>) jsonObject1.get("user");
                        Toast.makeText(getApplicationContext(), getApplication().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        DBManager.addUser((String)info.get("username"),(int)info.get("vipLevel"));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //匹配id
        Euesrname = (EditText) findViewById(R.id.M_username);
        Epassword = (EditText) findViewById(R.id.M_password);
        login = (TextView) findViewById(R.id.M_login);
        regist = (TextView) findViewById(R.id.M_regist);
        regist.setOnClickListener(this);
        login.setOnClickListener(this);
        //设置自带toolbar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("登录");
    }

    //返回数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //将注册数据保存在集合里
        if (data != null) {
            String username = data.getStringExtra("username");
            Euesrname.setText(username);

        }
        //设置账号为注册的账号
    }


    //设置再按一次结束程序
    private long exitTime = 0;

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//                System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    //标题栏返回箭头设置
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.M_login:
                String deviceId = getIMEI(getApplicationContext());
                String username = Euesrname.getText().toString();
                String password = Epassword.getText().toString();
                int position = password.indexOf(" ");
                int position2 = username.indexOf(" ");
                if (position != -1 || position2 != -1) {
                    Toast.makeText(getApplicationContext(), this.getString(R.string.password_no_space), Toast.LENGTH_SHORT).show();
                    break;
                }
                String st = "http://www.kakaxc.com/user/login?username="+username+"&password="+password+"&deviceId="+deviceId;//json接口
                Map<String, Object> map = new LinkedHashMap<String, Object>();

                httpclient = new HttpClientClass(st, "GET", "JSON", map, handler,1);
                httpclient.start();
                break;
            case R.id.M_regist:
                Intent intent = new Intent();
                intent.setClass(this, RegisterActivity.class);
                this.startActivityForResult(intent, 1);
                break;
        }
    }
    public String getIMEI(Context context) {
        String imei="";
        try {
//            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = Settings.System.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID);;
        } catch (Exception e) {
            imei = "";
        }
        return imei;
    }
}
