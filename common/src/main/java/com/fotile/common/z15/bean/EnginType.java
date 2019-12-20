package com.fotile.common.z15.bean;

/**
 * 项目名称：Common_z15
 * 创建时间：2019/2/26 17:55
 * 文件作者：yaohx
 * 功能描述：环境指向
 */
public enum EnginType {

    ENGIN_URL_ONLINE("online"),  //环境指向-线上
    ENGIN_URL_DEVELOP("develop"), //环境指向-开发
    ENGIN_URL_TEST("test"), //环境指向-测试

    ENGIN_PACK_RELASE("relase"),//relase模式
    ENGIN_PACK_DEBUG("debug");  //debug模式

    private String value = "";

    EnginType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EnginType setValue(String value) {
        if ("online".equals(value)) {
            return ENGIN_URL_ONLINE;
        }
        if ("develop".equals(value)) {
            return ENGIN_URL_DEVELOP;
        }
        if ("test".equals(value)) {
            return ENGIN_URL_TEST;
        }
        if ("relase".equals(value)) {
            return ENGIN_PACK_RELASE;
        }
        if ("debug".equals(value)) {
            return ENGIN_PACK_DEBUG;
        }
        return ENGIN_URL_ONLINE;
    }
}
