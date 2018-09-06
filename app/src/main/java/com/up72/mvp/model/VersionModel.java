package com.up72.mvp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 版本类
 * Created by LYF on 2016/11/27.
 */
public class VersionModel implements Parcelable {
    private String name;//版本名称（1.0.1）
    private int code;//版本号（版本更新的标识）
    private String size;//安装包大小
    private String content;//更新内容
    private int must;//是否必须更新 1必须更新
    private String downloadUrl;//下载更新链接

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public String getSize() {
        return size;
    }

    public String getContent() {
        return content;
    }

    public boolean isMust() {
        return must == 1;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public VersionModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.code);
        dest.writeString(this.size);
        dest.writeString(this.content);
        dest.writeInt(this.must);
        dest.writeString(this.downloadUrl);
    }

    protected VersionModel(Parcel in) {
        this.name = in.readString();
        this.code = in.readInt();
        this.size = in.readString();
        this.content = in.readString();
        this.must = in.readInt();
        this.downloadUrl = in.readString();
    }

    public static final Creator<VersionModel> CREATOR = new Creator<VersionModel>() {
        @Override
        public VersionModel createFromParcel(Parcel source) {
            return new VersionModel(source);
        }

        @Override
        public VersionModel[] newArray(int size) {
            return new VersionModel[size];
        }
    };
}