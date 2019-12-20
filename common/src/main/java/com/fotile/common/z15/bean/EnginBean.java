package com.fotile.common.z15.bean;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 项目名称：Common_z15
 * 创建时间：2019/2/26 13:58
 * 文件作者：yaohx
 * 功能描述：工程模式信息
 */
public class EnginBean {
    /**
     * 本地文件版本号
     * 每次发布一个版本，版本号+1
     */
    public int version = 0;
    /**
     * ota指向，默认指向线上环境
     */
    public EnginType ota_url = EnginType.ENGIN_URL_ONLINE;
    /**
     * 菜谱平台指向，默认指向线上环境
     */
    public EnginType recipe_url = EnginType.ENGIN_URL_ONLINE;
    /**
     * 打包模式，默认为relase模式
     */
    public EnginType pack_mode = EnginType.ENGIN_PACK_RELASE;

    public EnginBean() {

    }

    public EnginBean(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (jsonObject.has("version")) {
                    version = jsonObject.getInt("version");
                }
                if (jsonObject.has("ota_url")) {
                    String value_ota = jsonObject.getString("ota_url");
                    ota_url = EnginType.setValue(value_ota);
                }
                if (jsonObject.has("recipe_url")) {
                    String value_recipe = jsonObject.getString("recipe_url");
                    recipe_url = EnginType.setValue(value_recipe);
                }
                if (jsonObject.has("pack_mode")) {
                    String value_pack = jsonObject.getString("pack_mode");
                    pack_mode = EnginType.setValue(value_pack);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        String log = "[version:" + version + "] [ota_url:" + ota_url.getValue() + "] [recipe_url:" +
                recipe_url.getValue() + "] [pack_mode:" + pack_mode.getValue() + "]";
        return log;
    }
}
