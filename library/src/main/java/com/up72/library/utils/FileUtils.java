package com.up72.library.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.Random;

/**
 * 文件缓存
 */
@SuppressWarnings("ALL")
public class FileUtils {
    private String cacheDir = null;

    private Log log = new Log(getClass());

    private static class Holder {
        private static FileUtils fileInstance = new FileUtils();
    }

    private FileUtils() {
    }

    public static FileUtils getInstance() {
        return Holder.fileInstance;
    }


    public void init(Context context) {
        if (context != null) {
            File file;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
                file = context.getExternalCacheDir();
            } else {
                file = context.getCacheDir();
            }
            if (file != null && file.exists()) {
                cacheDir = file.getAbsolutePath();
                return;
            }
        }
        if (cacheDir != null) {
            //删除拍照或裁剪缓存的图片
            File fileDir = new File(cacheDir + "/" + "tempImages");
            if (fileDir.exists()) {
                FileUtils.getInstance().deleteFolderFile(fileDir.getAbsolutePath(), false);
            }
        }
    }

    /**
     * 创建一张新图片
     *
     * @param fileName 图片名称（需带后缀名,eg: image.jpg）
     * @return file or null
     */
    @Nullable
    public File createNewImage() {
        return createNewImage(null);
    }

    /**
     * 创建一张新图片
     *
     * @param fileName 图片名称（需带后缀名,eg: image.jpg）
     * @return file or null
     */
    @Nullable
    public File createNewImage(@Nullable String fileName) {
        if (cacheDir != null) {
            File fileDir = new File(cacheDir + "/" + "tempImages");
            if (!fileDir.exists() && !fileDir.mkdirs()) {
                return null;
            }
            if (fileName == null || fileName.length() == 0) {
                fileName = String.format(Locale.getDefault(), "%d%d.jpg", System.currentTimeMillis(), new Random().nextInt(10000) + 1);
            }

            File newFile = new File(fileDir, fileName);
            try {
                if (!newFile.exists() && !newFile.createNewFile()) {
                    return null;
                }
                return newFile;
            } catch (IOException e) {
                log.e("创建新图片失败" + e.getCause());
            }
        }
        return null;
    }

    /**
     * 读取本地缓存文件
     *
     * @param fileName : 文件名（不带任何的 “/ ”和后缀名）
     * @return Object: 当缓存过期或读取异常时返回null
     */
    public Object readCache(String fileName) {
        return this.readCache(0, fileName, 0);
    }

    /**
     * 读取文件
     *
     * @param uid      用户ID（未登录传0）
     * @param fileName 文件名（不带任何的 “/ ”和后缀名）
     * @return object 读取失败时，返回null
     */
    public Object readCache(int uid, String fileName) {
        return this.readCache(uid, fileName, 0);
    }

    /**
     * 读取本地缓存文件
     *
     * @param fileName       : 文件名（不带任何的 “/ ”和后缀名）
     * @param expirationTime : 缓存文件过期时间（秒）
     * @return Object: 当缓存过期或读取异常时返回null
     */
    public Object readCache(String fileName, int expirationTime) {
        return this.readCache(0, fileName, expirationTime);
    }

    /**
     * 读取本地缓存文件
     *
     * @param uid            用户ID（未登录传0）
     * @param fileName       : 文件名（不带任何的 “/ ”和后缀名）
     * @param expirationTime : 缓存文件过期时间（秒）
     * @return Object: 当缓存过期或读取异常时返回null
     */
    public Object readCache(int uid, String fileName, int expirationTime) {
        if (cacheDir == null) {
            return null;
        }
        if (uid <= 0) {
            uid = 0;
        }
        Object returnObj = null;
        try {
            File file = new File(cacheDir + "/" + uid + "/" + fileName + ".cache");
            if (!file.exists()) {
                return null;
            }
            if (expirationTime <= 0 || System.currentTimeMillis() - file.lastModified() < expirationTime * 1000) {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                returnObj = ois.readObject();
                fis.close();
            }
        } catch (Exception e) {
            log.e("读取文件发生错误");
            returnObj = null;
        }
        return returnObj;
    }

    /**
     * 将对象写入文件
     *
     * @param fileName 文件名（不带任何的 “/ ”和后缀名）
     * @param obj      写入的对象
     */
    public void writeCache(String fileName, Object obj) {
        this.writeCache(0, fileName, obj);
    }

    /**
     * 将对象写入文件
     *
     * @param uid      用户ID（未登录传0）
     * @param fileName 文件名（不带任何的 “/ ”和后缀名）
     * @param obj      写入的对象
     */
    public void writeCache(int uid, String fileName, Object obj) {
        if (cacheDir == null) {
            return;
        }
        if (uid <= 0) {
            uid = 0;
        }
        try {
            File fileDir = new File(cacheDir + "/" + uid);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File file = new File(fileDir.getAbsolutePath() + "/" + fileName + ".cache");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            fos.close();
        } catch (Exception e) {
            log.e("写入文件发生错误-->" + e.getMessage());
        }
    }

    /**
     * 删除缓存目录下指定的缓存文件
     *
     * @param fileName
     * @return boolean 当返回值为ture的时候，表示该缓存文件已删除或不存在
     */
    public boolean deleteCache(String fileName) {
        return this.deleteCache(0, fileName);
    }

    /**
     * 删除缓存目录下指定的缓存文件
     *
     * @param fileName
     * @return boolean 当返回值为ture的时候，表示该缓存文件已删除或不存在
     */
    public boolean deleteCache(int uid, String fileName) {
        boolean b = false;
        try {
            File file = new File(cacheDir + "/" + uid + "/" + fileName + ".cache");
            if (file.exists()) {
                // 文件存在则删除
                b = file.delete();
            } else {
                // 文件不存在也认为文件删除成功
                b = true;
            }
        } catch (Exception e) {
            log.e("删除文件发生错误-->" + e.getMessage());
        }
        return b;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param filePath       文件夹路径
     * @param deleteThisPath 是否可以删除本文件夹（true可以删除）
     * @return
     */
    public void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (File f : files) {
                        deleteFolderFile(f.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                log.e("删除文件发生错误-->" + e.getMessage());
            }
        }
    }

    public long getSDCardTotalSize() {
        long result = 0;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File filePath = Environment.getExternalStorageDirectory();    //获得sd卡的路径
            StatFs stat = new StatFs(filePath.getPath());                 //创建StatFs对象
            long blockSize = stat.getBlockSize();                         //获取block的size
            long totalBlocks = stat.getBlockCount();                     //获取block的总数
            result = blockSize * totalBlocks;          //总共大小
        }
        return result;
    }

    public long getSDCardAvailableSize() {
        long result = 0;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File filePath = Environment.getExternalStorageDirectory();    //获得sd卡的路径
            StatFs stat = new StatFs(filePath.getPath());                 //创建StatFs对象
            long blockSize = stat.getBlockSize();                  //总共大小
            long availableBlocks = stat.getAvailableBlocks();             //获取可用块大小
            result = blockSize * availableBlocks;
        }
        return result;
    }

    public void deleteCacheAll() {
        deleteFolderFile(cacheDir, false);
    }

    public long getCacheSize() {
        long result = 0;
        if (cacheDir != null) {
            result = getFolderSize(new File(cacheDir));
        }
        return result;
    }

    /**
     * 获取文件或者文件夹大小
     *
     * @param file 文件或者文件夹
     * @return 大小，单位byte
     */
    public long getFolderSize(String path) {
        return getFolderSize(new File(path));
    }

    /**
     * 获取文件或者文件夹大小
     *
     * @param file 文件或者文件夹
     * @return 大小，单位byte
     */
    public long getFolderSize(File file) {
        if (file == null) {
            return 0;
        }
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.e("获取文件或文件夹大小失败-->" + e.getMessage());
        }
        return size;
    }
}