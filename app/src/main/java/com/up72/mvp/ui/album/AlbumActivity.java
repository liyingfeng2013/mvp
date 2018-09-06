package com.up72.mvp.ui.album;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.up72.mvp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AlbumActivity extends AppCompatActivity implements View.OnClickListener, AlbumAdapter.ICallback {
    private static final String ALL_PHOTO = "全部图片";
    private TextView tvTitle;
    private GridView gvContent;
    private TextView tvFolder;
    private TextView tvCount;

    private Map<String, List<String>> albumMap;
    private AlbumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        this.tvTitle = (TextView) findViewById(R.id.tv_title);
        this.tvCount = (TextView) findViewById(R.id.tv_count);
        this.tvFolder = (TextView) findViewById(R.id.tv_folder);
        this.gvContent = (GridView) findViewById(R.id.gv_content);

        this.tvCount.setOnClickListener(this);
        this.tvFolder.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_done).setOnClickListener(this);

        initData();
    }

    private void initData() {
        int max = 9;// TODO: 17.3.2 Select Max
        tvTitle.setText(String.format(Locale.getDefault(), "图片 (%d/%d)", 0, max));
        tvCount.setText(String.format(Locale.getDefault(), "（ %d ）", 0));
        gvContent.setAdapter(adapter = new AlbumAdapter(max, false, this));
        setDataChange(null);//设置默认数据

        //开启子线程获取本地图片数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                albumMap = getPhotoAlbum(AlbumActivity.this);
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void setDataChange(@Nullable String folderName) {
        if (folderName == null) {
            folderName = ALL_PHOTO;
        }
        if (albumMap == null || !albumMap.containsKey(folderName)) {
            tvFolder.setText(folderName);
            adapter.replaceAll(null);
        } else {
            List<String> list = albumMap.get(folderName);
            adapter.replaceAll(list);
            tvFolder.setText(String.format(Locale.getDefault(), "%s (%d)", folderName, list.size()));
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                setDataChange(ALL_PHOTO);
            }
            return false;
        }
    });

    /**
     * 获取文件夹列表
     *
     * @param context {@link Context}.
     * @return {@code Map<String, List<String>>}.
     */
    public Map<String, List<String>> getPhotoAlbum(Context context) {
        /*// 图片ID
        MediaStore.Images.Media._ID
        // 图片完整路径
        MediaStore.Images.Media.DATA
        // 文件名称
        MediaStore.Images.Media.DISPLAY_NAME
        // 被添加到库中的时间
        MediaStore.Images.Media.DATE_ADDED
        // 目录ID
        MediaStore.Images.Media.BUCKET_ID
        // 所在文件夹名称
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME*/
        final String[] STORE_IMAGES = {
                // 图片完整路径。
                MediaStore.Images.Media.DATA,
                //所在文件夹名称
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
        Map<String, List<String>> albumFolderMap = new HashMap<>();
        while (cursor.moveToNext()) {
            String imagePath = cursor.getString(0);
            String bucketName = cursor.getString(1);

            List<String> all = albumFolderMap.get(ALL_PHOTO);
            if (all == null) {
                all = new ArrayList<>();
            }
            all.add(imagePath);
            albumFolderMap.put(ALL_PHOTO, all);

            List<String> albumFolder = albumFolderMap.get(bucketName);
            if (albumFolder == null) {
                albumFolder = new ArrayList<>();
            }
            albumFolder.add(imagePath);
            albumFolderMap.put(bucketName, albumFolder);
        }
        cursor.close();
        return albumFolderMap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.iv_done://完成
                if (adapter != null) {
                    String[] paths = adapter.getSelectPaths();
                    if (paths != null && paths.length > 0) {
                    }
                }
                break;
            case R.id.tv_count://预览
                if (adapter != null) {
                    String[] paths = adapter.getSelectPaths();
                    if (paths != null && paths.length > 0) {
                        toViewImage(paths, 0);
                    }
                }
                break;
            case R.id.tv_folder:
                if (albumMap != null && albumMap.size() > 0) {
                    final List<String> keys = new ArrayList<>();
                    List<String> items = new ArrayList<>();
                    for (Map.Entry<String, List<String>> map : albumMap.entrySet()) {
                        String key = map.getKey();
                        if (!key.equals(ALL_PHOTO)) {
                            keys.add(key);
                            items.add(String.format(Locale.getDefault(), "%s (%d)", key, map.getValue().size()));
                        }
                    }
                    Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
                    Collections.sort(items, String.CASE_INSENSITIVE_ORDER);
                    keys.add(0, ALL_PHOTO);
                    items.add(0, String.format(Locale.getDefault(), "%s (%d)", ALL_PHOTO,
                            albumMap.containsKey(ALL_PHOTO) ? albumMap.get(ALL_PHOTO).size() : 0));
                    new AlertDialog.Builder(this).setItems(items.toArray(new String[items.size()])
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    setDataChange(keys.get(which));
                                }
                            }).show();
                }
                break;
        }
    }

    @Override
    public void openCamera() {
    }

    @Override
    public void details(String[] paths, int position) {
        toViewImage(paths, position);
    }

    @Override
    public void onSelectChange(int selectCount, int max) {
        tvCount.setText(String.format(Locale.getDefault(), "（ %d ）", selectCount));
        tvTitle.setText(String.format(Locale.getDefault(), "图片 (%d/%d)", selectCount, max));
    }

    private void toViewImage(String[] paths, int position) {
        PhotoViewer.of(this).setSource(paths, position)
                .setSelect(adapter != null ? adapter.getSelectPaths() : null)
                .setMax(adapter != null ? adapter.getMax() : 1).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (adapter != null) {
            String[] paths = PhotoViewer.getSelectPaths(requestCode, resultCode, data);
            adapter.setSelectList(paths);
        }
    }
}