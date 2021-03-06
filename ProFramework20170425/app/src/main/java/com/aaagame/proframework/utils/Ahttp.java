package com.aaagame.proframework.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.aaagame.proframework.BaseApplication;
import com.aaagame.proframework.activity.BaseFragmentActivity;
import com.aaagame.proframework.dialog.AALoadingDialog;

import org.xutils.common.Callback;
import org.xutils.common.util.MD5;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

public class Ahttp {
    /**
     * 连接超时时长， 框架默认连接超时时长为：15s
     */
    public static final int connTimeout = 1000 * 50;
    public static final String Default_Param = "data";
    public static final String Default_Msg = "正在处理数据...";
    public static final String Default_Msg_Get = "正在获取数据...";
    // public static final String Default_Version = "2.1";
    public static final String Default_AddStr = "zyaf_core";
    public static final String Default_Plat = "android";
    //    public static final String MyBase_Addr = "/";
//    public static final String Local_Addr = MyBase_Addr + "rest/";
//    public static final String Using_Addr = Local_Addr;
    private RequestParams params;
    public AALoadingDialog loadingDialog;
    public Activity myActivity;

    private void initBase(Activity myActivity, String url, String data, String ck) {
        params = new RequestParams(url);
        params.addBodyParameter(Default_Param, data);
        initComParams(data, ck);
        this.myActivity = myActivity;
    }

    /**
     * 文件传输接口强制使用Multipart
     */
    public void hasFile() {
        params.setMultipart(true);
    }

    public Ahttp(Activity myActivity, String url, Object o, String ck) {
        initBase(myActivity, url, AACom.getGson().toJson(o), ck);
    }

    public Ahttp(Activity myActivity, String url, String data, String ck) {
        initBase(myActivity, url, data, ck);
    }

    public Ahttp(String url, String data, String ck) {
        initBase(myActivity, url, data, ck);
    }

    public Ahttp(String url, String data) {
        initBase(null, url, data, "");
    }

    public Ahttp(Activity myActivity, String url) {
        initBase(myActivity, url, "", "");
    }

    public Ahttp(Activity myActivity, String url, boolean thirdReq) {
        initBase(myActivity, url, "", "");
    }

    /**
     * 初始化必要请求参数
     *
     * @param data
     */
    private void initComParams(String data, String tk) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        params.addBodyParameter("plat", Default_Plat);
        params.addBodyParameter("timestamp", timestamp);
        if (myActivity != null && BaseApplication.MyVersionName.equals("0")) {
            BaseApplication.MyVersionName = AAMethod.getVersionName(myActivity);
        }
        params.addBodyParameter("v", BaseApplication.MyVersionName);
        String sign = "";
        StringBuilder builder = new StringBuilder();
        builder.append(Default_AddStr);
        builder.append("timestamp").append(timestamp);
        builder.append("plat").append("android");
        builder.append("v").append(BaseApplication.MyVersionName);
        builder.append("data").append(data);
        builder.append(Default_AddStr);
        sign = MD5.md5(builder.toString());
        if (!TextUtils.isEmpty(tk)) {
            params.addBodyParameter("tk", tk);
        }
        params.addBodyParameter("sign", sign);
        Log.d("param", "jsonString url = " + builder.toString() + "     tk= " + tk);
    }

    public Ahttp addStrParams(String key, Object o) {
        params.addBodyParameter(key, AACom.getGson().toJson(o));
        return this;
    }

    /**
     * 添加文件
     *
     * @param key
     * @param filePath
     * @return
     */
    public Ahttp addFile(String key, String filePath) {
        try {
            // params.addBodyParameter(key, new FileInputStream(filePath), new
            // File(filePath).length());
            File tmpFile = new File(filePath);
            System.out.println(filePath + "图片状态" + tmpFile.exists() + FileSizeUtil.getAutoFileOrFilesSize(filePath));
//            System.out.println("用户信息" + AAMethod.getMIMEType(tmpFile));
//            params.setUseCookie(false);
            params.addBodyParameter(key, tmpFile, AAMethod.getMIMEType(tmpFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 显示带title的提示框
     *
     * @param title
     * @param message
     */
    public void showLoading(String title, String message) {
        loadingDialog = new AALoadingDialog(myActivity);
        loadingDialog.setTitle(title);
        loadingDialog.setMsg(message);
        loadingDialog.show();
    }

    /**
     * 显示只有内容的提示框
     *
     * @param message
     */
    public void showLoading(String message) {
        loadingDialog = new AALoadingDialog(myActivity);
        loadingDialog.setMsg(message);
        loadingDialog.show();
    }

    /**
     * post方式请求
     *
     * @param requestCallBack
     * @param <T>
     */
    public <T> void send(Callback.CommonCallback<T> requestCallBack) {
        if (loadingDialog == null && myActivity != null) {
            try {
                if (((BaseFragmentActivity) myActivity).reqShowAlert) {
                    loadingDialog = new AALoadingDialog(myActivity);
                    loadingDialog.setMsg(Default_Msg);
                    loadingDialog.show();
                } else {
                    ((BaseFragmentActivity) myActivity).reqShowAlert = true;
                }
            } catch (Exception e) {
                try {
                    if (myActivity != null) {
                        loadingDialog = new AALoadingDialog(myActivity);
                        loadingDialog.setMsg(Default_Msg);
                        loadingDialog.show();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        params.setConnectTimeout(connTimeout);
        params.setReadTimeout(connTimeout);
        x.http().post(params, requestCallBack);

    }

    /**
     * get方式请求
     *
     * @param requestCallBack
     * @param <T>
     */
    public <T> void sendGet(Callback.CommonCallback<T> requestCallBack) {
        if (loadingDialog == null && myActivity != null) {
            try {
                if (((BaseFragmentActivity) myActivity).reqShowAlert) {
                    loadingDialog = new AALoadingDialog(myActivity);
                    loadingDialog.setMsg(Default_Msg);
                    loadingDialog.show();
                } else {
                    ((BaseFragmentActivity) myActivity).reqShowAlert = true;
                }
            } catch (Exception e) {
                try {
                    if (myActivity != null) {
                        loadingDialog = new AALoadingDialog(myActivity);
                        loadingDialog.setMsg(Default_Msg);
                        loadingDialog.show();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        params.setConnectTimeout(connTimeout);
        x.http().get(params, requestCallBack);

    }

}