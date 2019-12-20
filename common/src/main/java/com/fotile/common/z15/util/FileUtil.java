package com.fotile.common.z15.util;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * 文件名称：FileUtil
 * 创建时间：2017/8/7 15:38
 * 文件作者：yaohx
 * 功能描述：文件操作类
 */
public class FileUtil {

    /**
     * 创建文件夹，如果已经存在则什么都不做
     *
     * @param folderPath 文件夹的绝对路径
     * @return File
     */
    public static File createFolder(String folderPath) {
        File folder = null;
        if (!TextUtils.isEmpty(folderPath)) {
            folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
        return folder;
    }

    public static File createNewFile(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            File file = new File(filename);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    return file;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * 删除File
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 删除目录下的所有文件包括文件夹
     *
     * @param fodler
     */
    public static void deleteFolder(String fodler) {
        if (!TextUtils.isEmpty(fodler)) {
            File dir = new File(fodler);
            if (dir.isDirectory() && dir.exists()) {
                File[] files = dir.listFiles();
                // 如果没有文件
                if (null == files || files.length == 0) {
                    dir.delete();
                }
                if (null != files) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            deleteFolder(file.getPath());
                        } else {
                            // 删除目录下的所有文件
                            file.delete();
                        }
                    }
                    dir.delete();
                }
                dir.delete();
            }
        }
    }


    /**
     * 删除目录下的所有文件-不删除文件夹
     * 删除爱奇艺缓存使用，其他请勿调用
     *
     * @param fodler
     */
    public static void deleteFolderFiles4AQY(String fodler) {
        if (!TextUtils.isEmpty(fodler)) {
            File dir = new File(fodler);
            if (dir.isDirectory() && dir.exists()) {
                File[] files = dir.listFiles();
                if (null != files) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            String diranme = file.getName();
                            //下列目录不执行删除
                            if (diranme.contains("baidu") || diranme.contains("cude_ad_db_dir") || diranme.contains
                                    ("cudeDB") || diranme.contains("iqiyi_p2p")) {

                            } else {
                                deleteFolderFiles4AQY(file.getPath());
                            }
                        } else {
                            // 删除目录下的所有文件
                            file.delete();
                        }
                    }
                }
            }
        }
    }

}
