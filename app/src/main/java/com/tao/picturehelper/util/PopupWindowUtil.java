package com.tao.picturehelper.util;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
public class PopupWindowUtil {

    private PopupWindow mPopupWindow;
    private View mPopupView;
    private WindowManager.LayoutParams mLayoutParams;
    private Context mContext;

    /**
     * 构造函数
     * @param mContext 界面上下文
     */
    public  PopupWindowUtil(Context mContext){
        this.mContext = mContext;
    }

    /**
     * 检测开始时加载一个窗口告诉用户正在检测
     * @param mPopupWindowLayoutID 布局文件的ID
     */
    public void showPopUpWindow(int mPopupWindowLayoutID){
        //代码实现
        mPopupView = ((Activity)mContext).getLayoutInflater().inflate(mPopupWindowLayoutID,null);
        mPopupWindow = new PopupWindow(mPopupView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addBackground();
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(mPopupView,
                Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
    }

    public void dismissPopupWindow(){
        mPopupWindow.dismiss();
    }
    /**
     * 检测开始时加载一个窗口告诉用户正在检测
     * @param mPopupWindowLayoutID 布局文件的ID
     * @param isOutsideTouchable 外部是否可以点击
     */
    public void showPopUpWindow(int mPopupWindowLayoutID,boolean isOutsideTouchable){
        //代码实现
        mPopupView = ((Activity)mContext).getLayoutInflater().inflate(mPopupWindowLayoutID,null);
        mPopupWindow = new PopupWindow(mPopupView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addBackground();
        mPopupWindow.setOutsideTouchable(isOutsideTouchable);
        mPopupWindow.showAtLocation(mPopupView,
                Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
    }

    /**
     * 添加一个背景变暗效果
     */
    private void addBackground(){
        //这里要使用context强制转换到Activity
        mLayoutParams = ((Activity)mContext).getWindow().getAttributes();
        mLayoutParams.alpha = 0.4f;
        ((Activity)mContext).getWindow().setAttributes(mLayoutParams);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = ((Activity)mContext).getWindow().getAttributes();
                layoutParams.alpha = 1f;
                ((Activity)mContext).getWindow().setAttributes(layoutParams);
            }
        });
    }
}
