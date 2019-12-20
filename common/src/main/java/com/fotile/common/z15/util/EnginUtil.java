package com.fotile.common.z15.util;

import com.fotile.common.z15.bean.EnginBean;
import com.fotile.common.z15.bean.EnginType;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 项目名称：Common_z15
 * 创建时间：2019/2/26 14:33
 * 文件作者：yaohx
 * 功能描述：工程模式处理类
 */
public class EnginUtil {
    /**
     * 工厂模式信息文件保存的文件夹
     */
    private static final String folder = "/sdcard/ft_engin/";
    /**
     * 工厂模式信息文件保存的文件名称
     */
    private static final String fileName = "engin.txt";

    /**
     * 返回本地文件中保存的Engin对象
     * 如果没有对应的本地文件 return null
     *
     * @return
     */
    public static EnginBean getEnginBean() {
        EnginBean enginBean = new EnginBean();
        File file = new File(folder + fileName);
        if (file.exists()) {
            try {
                InputStream inputStream = new FileInputStream(file);
                int length = inputStream.available();
                byte[] bb = new byte[length];
                inputStream.read(bb);
                String result = new String(bb, "utf-8");
                enginBean = new EnginBean(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return enginBean;
    }

    /**
     * 如果目标版本号 > 本地保存版本号，将所有字段指向默认值
     *
     * @param version
     * @return
     */
    public static boolean resetEnginBean(int version) {
        EnginBean enginBean = getEnginBean();
        if (null == enginBean) {
            enginBean = new EnginBean();
        }
        //如果目标版本号>本地保存版本号，将所有字段指向默认值
        if (version > enginBean.version || version == 0) {
            enginBean.version = version;
            enginBean.ota_url = EnginType.ENGIN_URL_ONLINE;
            enginBean.recipe_url = EnginType.ENGIN_URL_ONLINE;
            enginBean.pack_mode = EnginType.ENGIN_PACK_RELASE;
            updateEnginBean(enginBean);
            return true;
        }
        return false;
    }

    /**
     * 更新本地文件保存的信息，如果不存在本地文件则创建新文件
     *
     * @param enginBean
     */
    public static void updateEnginBean(EnginBean enginBean) {
        if (null != enginBean) {
            try {
                File file = new File(folder + fileName);
                //如果文件不存在，新建一个文件
                if (!file.exists()) {
                    FileUtil.createFolder(folder);
                    file.createNewFile();
                }

                String content = parse2Json(enginBean);
                //将字符串内容写入文件
                OutputStream outputStream = new FileOutputStream(file);
                byte[] bb = content.getBytes();
                outputStream.write(bb);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String parse2Json(EnginBean enginBean) {
        JSONObject jsonObject = new JSONObject();
        String version = enginBean.version + "";
        String ota_url = enginBean.ota_url.getValue();
        String recipe_url = enginBean.recipe_url.getValue();
        String pack_mode = enginBean.pack_mode.getValue();
        try {
            jsonObject.put("version", version);
            jsonObject.put("ota_url", ota_url);
            jsonObject.put("recipe_url", recipe_url);
            jsonObject.put("pack_mode", pack_mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 删除本地保存的文件
     */
    public void removeEnginBean() {
        File file = new File(folder + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean isDebug(){
        EnginBean enginBean = EnginUtil.getEnginBean();
        return enginBean.pack_mode==EnginType.ENGIN_PACK_DEBUG;
    }

}
