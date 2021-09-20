package com.tao.picturehelper.fragment;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.ocr.AipOcr;
import com.tao.picturehelper.ImageGalleryActivity;
import com.tao.picturehelper.R;
import com.tao.picturehelper.adapter.ImageAdapter;
import com.tao.picturehelper.adapter.ImageFolderAdapter;
import com.tao.picturehelper.base.BaseFragment;
import com.tao.picturehelper.base.BaseRecyclerAdapter;
import com.tao.picturehelper.bean.Image;
import com.tao.picturehelper.bean.ImageFolder;
import com.tao.picturehelper.db.DBManager;
import com.tao.picturehelper.listener.ImageLoaderListener;
import com.tao.picturehelper.util.BitmapAndStringUtils;
import com.tao.picturehelper.util.DeviceUtil;
import com.tao.picturehelper.widget.ImageFolderPopupWindow;
import com.tao.picturehelper.widget.SpaceGridItemDecoration;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Rick Ge on 2017/1/10.
 */

public class AlbumFragment extends BaseFragment implements ImageLoaderListener, View.OnClickListener, BaseRecyclerAdapter.OnItemClickListener {
    @BindView(R.id.rv_image)
    RecyclerView mContentView;
    @BindView(R.id.btn_title_select)
    Button mSelectFolderView;
    @BindView(R.id.iv_title_select)
    ImageView mSelectFolderIcon;
    @BindView(R.id.toolbar)
    View mToolbar;

    private AlertDialog mAlertDialog = null;
    View view;
    private LoaderListener mCursorLoader;
    private ImageFolderAdapter mImageFolderAdapter;
    private ImageAdapter mImageAdapter;
    private ImageFolderPopupWindow mFolderPopupWindow;

    private String[] mImageSources;
    public String tokenx;
    String searchParam = "";
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Map<String, String> res = (Map<String, String>) msg.obj;
                    String tipInfo = res.get("tipInfo");
                    String identityResult = res.get("identityResult");
                    String path = res.get("path");
                    DBManager.addPicture(path, identityResult);
                    mAlertDialog.setTitle(tipInfo);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_image;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        searchParam = bundle.getString("searchParam");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mContentView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mContentView.addItemDecoration(new SpaceGridItemDecoration((int) DeviceUtil.dipToPx(getResources(), 1)));
        mImageAdapter = new ImageAdapter(getContext(), this);
        mImageFolderAdapter = new ImageFolderAdapter(getContext(), this);
        mContentView.setAdapter(mImageAdapter);
        mContentView.setItemAnimator(null);
        mImageAdapter.setOnItemClickListener(this);

    }

    @Override
    protected void initData() {

        super.initData();
        mCursorLoader = new LoaderListener();
        getLoaderManager().initLoader(0, null, mCursorLoader);

        view = View.inflate(getContext(), R.layout.dialog_layout, null);
//        Intent startIntent = new Intent(getActivity(), MyService.class);
//        getActivity().startService(startIntent);
//        mAlertDialog = new AlertDialog.Builder(getActivity())
//                .setView(view).create();
//
//        mAlertDialog.setTitle("第一次安装读取较慢，可按home键后台读取");
//        mAlertDialog.show();
    }

    @Override
    public void displayImage(ImageView iv, String path) {
        // Load image
        getImgLoader().load(path)
                .asBitmap()
                .centerCrop()
                .error(R.mipmap.ic_split_graph)
                .into(iv);
    }

    @OnClick({R.id.btn_title_select})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_title_select:
                showPopupFolderList();
                break;
        }
    }

    /**
     * 创建弹出的相册
     */
    private void showPopupFolderList() {
        if (mFolderPopupWindow == null) {
            ImageFolderPopupWindow popupWindow = new ImageFolderPopupWindow(getContext(), new ImageFolderPopupWindow.Callback() {
                @Override
                public void onSelect(ImageFolder imageFolder) {
                    addImagesToAdapter(imageFolder.getImages());
                }

                @Override
                public void onDismiss() {
                    mSelectFolderIcon.setImageResource(R.mipmap.ic_arrow_bottom);
                }

                @Override
                public void onShow() {
                    mSelectFolderIcon.setImageResource(R.mipmap.ic_arrow_top);
                }
            });
            popupWindow.setAdapter(mImageFolderAdapter);
            mFolderPopupWindow = popupWindow;
        }
        mFolderPopupWindow.showAsDropDown(mToolbar);
    }

    @Override
    public void onItemClick(int position) {
        ImageGalleryActivity.show(getContext(), mImageSources, position);
    }

    private class LoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == 0) {
                //数据库光标加载器
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            int col = data.getCount();
            if (data != null) {
                final ArrayList<Image> images = new ArrayList<>();
                final List<ImageFolder> imageFolders = new ArrayList<>();
                final ImageFolder defaultFolder = new ImageFolder();
                defaultFolder.setName("全部照片");
                defaultFolder.setPath("");
                imageFolders.add(defaultFolder);
                int totalCount = 100;
                Map<String, Object> map = DBManager.queryUser();
                if (map != null && map.size() != 0) {
                    String vipLevel = (String) map.get("vipLevel");
                    Map<String,String> stringMap = DBManager.queryVipByVipLevel(vipLevel);
                    if (stringMap==null){
                        totalCount = 20;
                    }else{
                        String picNum = stringMap.get("picNum");
                        totalCount = Integer.valueOf(picNum);
                    }
                }
                int count = data.getCount();

                int identyCount = 0;
                if (count > 0) {
                    data.moveToFirst();
                    do {
                        identyCount++;
                        if (identyCount > totalCount) {
                            break;
                        }
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        int id = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                        String thumbPath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                        String bucket = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                        Image image = new Image();
                        image.setPath(path);
                        String pathtmp = DBManager.queryPathByPath(path);
                        if (pathtmp==null){
                            DBManager.addPath(path);
                        }
                        String dbResult = DBManager.queryPicrureByPath(path);
                        image.setName(name);
                        image.setDate(dateTime);
                        image.setId(id);
                        image.setThumbPath(thumbPath);
                        image.setFolderName(bucket);
                        if (searchParam != null && !"".equals(searchParam)) {
                            if (dbResult != null) {
                                int position = dbResult.indexOf(searchParam);
                                if (position == -1) {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        }
                        images.add(image);

                        File imageFile = new File(path);
                        File folderFile = imageFile.getParentFile();
                        ImageFolder folder = new ImageFolder();
                        folder.setName(folderFile.getName());
                        folder.setPath(folderFile.getAbsolutePath());
                        if (!imageFolders.contains(folder)) {
                            folder.getImages().add(image);
                            folder.setAlbumPath(image.getPath());//默认相册封面
                            imageFolders.add(folder);
                        } else {
                            // 更新
                            ImageFolder f = imageFolders.get(imageFolders.indexOf(folder));
                            f.getImages().add(image);
                        }
                    } while (data.moveToNext());

                }

                addImagesToAdapter(images);
                defaultFolder.getImages().addAll(images);
                defaultFolder.setAlbumPath(images.size() > 0 ? images.get(0).getPath() : null);
                mImageFolderAdapter.resetItem(imageFolders);
            }
        }


        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
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

    private void addImagesToAdapter(ArrayList<Image> images) {
        mImageAdapter.resetItem(images);
        mImageSources = toArray(images);
    }


    private static String[] toArray(List<Image> images) {
        if (images == null)
            return null;
        int len = images.size();
        if (len == 0)
            return null;

        String[] strings = new String[len];
        int i = 0;
        for (Image image : images) {
            strings[i] = image.getPath();
            i++;
        }
        return strings;
    }

    private String infoPopText(final String result) {
        String res = "";
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONArray array = (JSONArray) jsonObject.get("words_result");
        int num = (int) jsonObject.get("words_result_num");
        if (num <= 0) {
            return res;
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject1 = (JSONObject) array.get(i);
            String temp = (String) jsonObject1.get("words");
            res += temp;
        }
        this.tokenx = res;
        return res;
    }


}
