package com.huanchengfly.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class VersionUtil {
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getPackageName(Context context) {
        String packageName = "";
        try {
            packageName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }

    public static String getVersionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /*
    public static void showDownloadDialog(Context context, NewUpdateBean.ResultBean versionInfo) {
        List<String> list = new ArrayList<>();
        for (NewUpdateBean.DownloadBean downloadBean : versionInfo.getDownloads()) {
            list.add(downloadBean.getName());
        }
        String[] arr = list.toArray(new String[0]);
        DialogUtil.build(context)
                .setTitle(R.string.title_dialog_download)
                .setCancelable(versionInfo.isCancelable())
                .setItems(arr, (dialog, which) -> {
                    NewUpdateBean.DownloadBean downloadBean = versionInfo.getDownloads().get(which);
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downloadBean.getUrl())));
                })
                .create()
                .show();
    }

    static class DownloadInfoBean {
        private String name;
        private String link;

        public DownloadInfoBean(String name, String link) {
            this.name = name;
            this.link = link;
        }

        public String getName() {
            return name;
        }

        public DownloadInfoBean setName(String name) {
            this.name = name;
            return this;
        }

        public String getLink() {
            return link;
        }

        public DownloadInfoBean setLink(String link) {
            this.link = link;
            return this;
        }
    }
    */
}
