package com.tao.picturehelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tao.picturehelper.db.DBManager;

import java.io.File;

public class SelectPicPopupWindowActivity extends Activity implements View.OnClickListener {
    private static final int SHARE_RESULT_NO_ERROR = 400;
    private static final int SHARE_RESULT_FILE_NOT_FOUND =404 ;
    String currentPath = "";
    private Button btn_take_photo, btn_pick_photo, btn_cancel;
    private LinearLayout layout;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_pic_popup_window);
        btn_take_photo = (Button) this.findViewById(R.id.share);
        btn_pick_photo = (Button) this.findViewById(R.id.readword);
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
        layout = (LinearLayout) findViewById(R.id.pop_layout);
        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }
        });
        //添加按钮监听
        btn_cancel.setOnClickListener(this);
        btn_pick_photo.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        currentPath = bundle.getString("path");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:
                Uri uri = getUriFromFile(new File(currentPath));
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent, "分享到"));
                break;
            case R.id.readword:
                String result = DBManager.queryPicrureByPath(currentPath);
                copyClipboard(result);
                Toast.makeText(getApplicationContext(), this.getApplication().getString(R.string.clipboard),
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_cancel:
                break;
            default:
                break;
        }
        finish();
    }
    public void copyClipboard(String content) {
        ClipboardManager myClipboard = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);
        ClipData primaryClip = ClipData.newPlainText("text", content);
        assert myClipboard != null;
        myClipboard.setPrimaryClip(primaryClip);

    }

    /**
     * 获取本地文件的uri
     * @param file
     * @return
     */
    public static Uri getUriFromFile(File file)  {
        Uri imageUri = null;
        if (file != null && file.exists() && file.isFile()) {
            imageUri = Uri.fromFile(file);
        }
        return imageUri;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(alertDialog != null){
            alertDialog.dismiss();
        }
    }
}
