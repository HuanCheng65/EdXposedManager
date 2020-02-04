package com.huanchengfly.edxp.models;

import com.google.gson.annotations.SerializedName;

public class Update {
    @SerializedName("version_code")
    private int versionCode;
    @SerializedName("version_name")
    private String versionName;
    @SerializedName("edxposed_manager_version_name")
    private String edxposedManagerVersionName;
    @SerializedName("update_content")
    private String updateContent;
    @SerializedName("download_url")
    private String donwloadUrl;

    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getEdxposedManagerVersionName() {
        return edxposedManagerVersionName;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public String getDonwloadUrl() {
        return donwloadUrl;
    }
}
