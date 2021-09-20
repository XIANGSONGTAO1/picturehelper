package com.tao.picturehelper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tao.picturehelper.base.BaseActivity;
import com.tao.picturehelper.util.ClickUtil;
import com.tao.picturehelper.util.HttpClientClass;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    String username, password;
    String restResult;

    EditText number;
    EditText psw;
    TextView regist;
    HttpClientClass httpclient = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        number = (EditText) findViewById(R.id.Regist_username);
        psw = (EditText) findViewById(R.id.Regist_password);
        regist = (TextView) findViewById(R.id.Regist_regist);
        regist.setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.register;
    }

    //设置返回按钮
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
        System.out.println("按下了back键   onBackPressed()");
    }


    @Override
    public void onClick(View view) {
        if (ClickUtil.isFastDoubleClick()) {
            Toast.makeText(getApplicationContext(), getApplication().getString(R.string.click_error), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {

            case R.id.Regist_regist:
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        String res = (String) msg.obj;
                        switch (msg.what) {
                            case 2:
                                JSONObject jsonObject = JSON.parseObject(res);
                                Integer errcode = jsonObject.getInteger("errcode");
                                String errMsg = jsonObject.getString("errmsg");
                                if (errcode != 200) {
                                    if (errcode == 402) {
                                        Toast.makeText(getApplicationContext(), getApplication().getString(R.string.user_exist), Toast.LENGTH_SHORT).show();
                                    } else if (errcode == 400) {
                                        Toast.makeText(getApplicationContext(), getApplication().getString(R.string.name_or_pass_not_null), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_SHORT).show();
                                    Map<String, Object> info = (Map<String, Object>) jsonObject.get("data");
                                    Intent intent = new Intent();
                                    intent.setClass(getApplicationContext(), LoginActivity.class);
                                    //将输入传递给主界面
                                    intent.putExtra("username", username);
                                    intent.putExtra("password", password);
                                    setResult(1, intent);
                                    //传递完毕
                                    finish();
                                }
                                break;
                        }
                        super.handleMessage(msg);
                    }
                };
                username = number.getText().toString();
                password = psw.getText().toString();
                int position = password.indexOf(" ");
                int position2 = username.indexOf(" ");

                if (position != -1 || position2 != -1) {
                    Toast.makeText(getApplicationContext(), this.getString(R.string.password_no_space), Toast.LENGTH_SHORT).show();
                    break;
                }
                String st = "http://www.kakaxc.com/user/register?username=" + username + "&password=" + password;//json接口
                Map<String, Object> map = new LinkedHashMap<>();
                httpclient = new HttpClientClass(st, "GET", "JSON", map, handler, 2);
                httpclient.start();
                break;
        }
    }
}
