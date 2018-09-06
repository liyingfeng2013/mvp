package com.up72.mvp.ui.album;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.up72.mvp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 相册适配器
 * Created by LYF on 17.2.6.
 */
class AlbumAdapter extends BaseAdapter {
    private List<String> dataList = new ArrayList<>();
    private List<String> selectList = new ArrayList<>();

    private int max;//最大可选个数（max>=1）
    private boolean hasCamera = false;//是否有相机选项（默认false） true有,默认第一个
    private ICallback callback;

    /**
     * @param max       最大可选个数（max>=1）
     * @param hasCamera 是否有相机选项（默认false） true有,默认第一个
     * @param callback  回调
     */
    AlbumAdapter(int max, boolean hasCamera, ICallback callback) {
        if (max < 1) {
            max = 1;
        }
        this.max = max;
        this.hasCamera = hasCamera;
        this.callback = callback;
        if (hasCamera) {
            dataList.add("");
        }
    }

    void replaceAll(@Nullable List<String> list) {
        dataList.clear();
        if (list != null && list.size() > 0) {
            for (String str : list) {
                if (str != null && str.length() != 0) {
                    dataList.add(str);
                }
            }
        }
        if (hasCamera) {
            dataList.add(0, "");
        }
        notifyDataSetChanged();
    }

    int getMax() {
        return max;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
            holder.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo);
            holder.ivSelect = (ImageView) convertView.findViewById(R.id.iv_select);
            convertView.setTag(holder);
        }

        String path = dataList.get(position);
        if (path.length() == 0 && position == 0) {
            holder.ivSelect.setVisibility(View.GONE);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.ivPhoto.setBackgroundColor(Color.parseColor("#424242"));
            holder.ivPhoto.setImageResource(R.drawable.ic_add_photo_white);
        } else {
            holder.ivSelect.setVisibility(View.VISIBLE);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.ivPhoto.setBackgroundColor(Color.TRANSPARENT);
            Glide.with(parent.getContext()).load(path).placeholder(R.drawable.ic_photo).into(holder.ivPhoto);
            holder.ivSelect.setSelected(selectList.contains(path));
        }
        holder.ivPhoto.setOnClickListener(new OnImageClickListener(position));
        holder.ivSelect.setOnClickListener(new OnSelectClickListener(position));

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivPhoto;
        ImageView ivSelect;
    }

    void setSelectList(@Nullable String[] selectArray) {
        this.selectList.clear();
        if (selectArray != null && selectArray.length > 0) {
            this.selectList.addAll(Arrays.asList(selectArray));
        }
        notifyDataSetChanged();
        if (callback != null) {
            callback.onSelectChange(selectList.size(), max);
        }
    }

    @Nullable
    String[] getSelectPaths() {
        if (selectList != null && selectList.size() > 0) {
            return selectList.toArray(new String[selectList.size()]);
        }
        return null;
    }

    private class OnImageClickListener implements View.OnClickListener {
        private int position;

        OnImageClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (callback != null) {
                if (hasCamera && position == 0) {
                    callback.openCamera();
                    return;
                }
                if (dataList != null) {
                    List<String> list = new ArrayList<>();
                    list.addAll(dataList);
                    int index = position;
                    if (hasCamera && list.size() > 1) {
                        index--;
                        list.remove(0);
                    }

                    callback.details(list.toArray(new String[list.size()]), index);
                }
            }
        }
    }

    private class OnSelectClickListener implements View.OnClickListener {
        private int position;

        OnSelectClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (dataList != null && position > -1 && position < dataList.size()) {
                String path = dataList.get(position);
                boolean value = selectList.contains(path);
                if (value) {
                    selectList.remove(path);
                } else {
                    if (max <= 1) {
                        selectList.clear();
                        selectList.add(path);
                        notifyDataSetChanged();
                    } else if (selectList.size() >= max) {
                        return;
                    } else {
                        selectList.add(path);
                    }
                }
                v.setSelected(!value);
                if (callback != null) {
                    callback.onSelectChange(selectList.size(), max);
                }
            }
        }
    }

    interface ICallback {
        void openCamera();

        void details(String[] paths, int position);

        void onSelectChange(int selectCount, int max);
    }
}