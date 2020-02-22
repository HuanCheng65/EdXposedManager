package com.huanchengfly.edxp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.huanchengfly.about.AboutPage;
import com.huanchengfly.edxp.models.Update;
import com.huanchengfly.utils.VersionUtil;

import org.meowcat.edxposed.manager.R;
import org.meowcat.edxposed.manager.XposedApp;
import org.meowcat.edxposed.manager.XposedBaseActivity;
import org.meowcat.edxposed.manager.util.NavUtil;
import org.meowcat.edxposed.manager.util.json.JSONUtils;

public class AboutActivity extends XposedBaseActivity implements View.OnClickListener {
    public static final int STATE_ERROR = 0;
    public static final int STATE_NO_UPDATE = 1;
    public static final int STATE_UPDATE = 2;

    private int mUpdateState;

    private View updateTip;
    private TextView updateTipHeaderTv;
    private TextView updateTipTitleTv;
    private TextView updateTipContentTv;
    private Button downloadBtn;
    private Button dismissBtn;

    private AboutPage mAboutPage;
    private Update mUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_container);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewGroup container = findViewById(R.id.container);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.nav_item_about);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View headerView = View.inflate(this, R.layout.header_about, null);
        updateTip = headerView.findViewById(R.id.header_update_tip_shadow);
        updateTipHeaderTv = headerView.findViewById(R.id.header_update_tip_header_title);
        updateTipTitleTv = headerView.findViewById(R.id.header_update_tip_title);
        updateTipContentTv = headerView.findViewById(R.id.header_update_tip_content);
        downloadBtn = headerView.findViewById(R.id.header_update_tip_button_download);
        dismissBtn = headerView.findViewById(R.id.header_update_tip_button_dismiss);
        downloadBtn.setOnClickListener(this);
        dismissBtn.setOnClickListener(this);
        int colorIcon = XposedApp.getColor(this);
        mAboutPage = new AboutPage(this)
                .setHeaderView(headerView)
                .addItem(new AboutPage.Item(getString(R.string.about_version_label), VersionUtil.getVersionName(this) + "_" + Config.VERSION_NAME, R.drawable.ic_two_tone_info, colorIcon)
                        /*.setOnClickListener(v -> checkUpdate())*/)
                .addItem(new AboutPage.Item(getString(R.string.about_source_label), "获取最新更新、提出建议或反馈")
                        .setIcon(R.drawable.ic_github, colorIcon)
                        .setOnClickListener(v -> NavUtil.startURL(this, getString(R.string.source_url_beautified))))
                .addTitle(getString(R.string.support), colorIcon)
                .addItem(new AboutPage.Item(getString(R.string.support_group_qq_label), getString(R.string.support_group_qq_number))
                        .setIcon(R.drawable.ic_qq, colorIcon)
                        .setOnClickListener(v -> NavUtil.startURL(this, getString(R.string.group_qq_link))))
                .addItem(new AboutPage.Item(getString(R.string.support_group_telegram_label), getString(R.string.support_group_telegram_name))
                        .setIcon(R.drawable.ic_two_tone_group, colorIcon)
                        .setOnClickListener(v -> NavUtil.startURL(this, getString(R.string.group_telegram_link))))
                .addItem(new AboutPage.Item(getString(R.string.support_group_telegram_channel_label), getString(R.string.support_group_telegram_channel_name))
                        .setIcon(R.drawable.ic_two_tone_mode_comment, colorIcon)
                        .setOnClickListener(v -> NavUtil.startURL(this, getString(R.string.support_group_telegram_channel))))
                .addItem(new AboutPage.Item(getString(R.string.support_faq_label))
                        .setIcon(R.drawable.ic_two_tone_bug_report, colorIcon)
                        .setOnClickListener(v -> NavUtil.startURL(this, getString(R.string.support_faq_url))))
                .addItem(new AboutPage.Item(getString(R.string.support_modules_label), getString(R.string.support_modules_description,
                        getString(R.string.module_support)))
                        .setIcon(R.drawable.ic_two_tone_info, colorIcon))
                .addItem(new AboutPage.Item(getString(R.string.support_framework_label))
                        .setIcon(R.drawable.ic_two_tone_help, colorIcon)
                        .setOnClickListener(v -> NavUtil.startURL(this, getString(R.string.support_material_xda))))
                .addTitle("开发参与", colorIcon)
                .addItem(new AboutPage.Item(getString(R.string.huanchengfly), getString(R.string.huanchengfly_summary), "https://ww1.sinaimg.cn/large/007rAy9hgy1g1cohwq5rsj30hs0hsmxs.jpg")
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.huanchengfly_coolapk)))
                                .setPackage("com.coolapk.market")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)))
                .addItem(new AboutPage.Item(getString(R.string.wearefun), getString(R.string.wearefun_summary), "https://ww1.sinaimg.cn/large/007rAy9hgy1g1cohx88l1j30dc0dcmyr.jpg")
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.wearefun_coolapk)))
                                .setPackage("com.coolapk.market")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)))
                .addTitle("支持我", colorIcon)
                .addItem(new AboutPage.Item("支付宝捐赠")
                        .setIcon(R.drawable.ic_alipay, colorIcon)
                        .setOnClickListener(v -> NavUtil.startURL(AboutActivity.this, "https://qr.alipay.com/FKX06385UK8W8T8X2MG827")))
                .addItem(new AboutPage.Item("支付宝领红包")
                        .setIcon(R.drawable.ic_archive, colorIcon)
                        .setOnClickListener(v -> NavUtil.startURL(AboutActivity.this, "https://qr.alipay.com/c1x06336wvvmfwjwlzbq4a5")));
        mAboutPage.into(container);
        checkUpdate();
    }

    private static final Handler sHandler = new Handler();

    private void checkUpdate() {
        new Thread(() -> {
            try {
                String originalJson = JSONUtils.getFileContent("https://huancheng65.github.io/api/edxp_update.json");

                final Update update = new Gson().fromJson(originalJson, Update.class);
                if (update.getVersionCode() > Config.VERSION_CODE) {
                    mUpdate = update;
                    mUpdateState = STATE_UPDATE;
                } else {
                    mUpdate = null;
                    mUpdateState = STATE_NO_UPDATE;
                }
                sHandler.post(this::refreshUpdateTip);
            } catch (Exception e) {
                e.printStackTrace();
                mUpdate = null;
                mUpdateState = STATE_ERROR;
                sHandler.post(this::refreshUpdateTip);
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_about, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.origin_about) {
            startActivity(new Intent(this, org.meowcat.edxposed.manager.AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_update_tip_button_download:
                if (mUpdate == null) {
                    if (mUpdateState != STATE_NO_UPDATE) {
                        checkUpdate();
                    }
                    return;
                }
                NavUtil.startURL(this, mUpdate.getDonwloadUrl());
                break;
            case R.id.header_update_tip_button_dismiss:
                mUpdate = null;
                refreshUpdateTip();
                break;
        }
    }

    private void refreshUpdateTip() {
        switch (mUpdateState) {
            case STATE_NO_UPDATE:
                downloadBtn.setText(R.string.button_check_update);
                updateTip.setVisibility(View.VISIBLE);
                dismissBtn.setVisibility(View.GONE);
                updateTipHeaderTv.setText(getString(R.string.update_tip_header));
                updateTipTitleTv.setText(getString(R.string.update_tip_no_title));
                updateTipContentTv.setText(getString(R.string.update_tip_no_content));
                break;
            case STATE_UPDATE:
                if (mUpdate != null) {
                    downloadBtn.setText(R.string.button_go_to_download);
                    updateTip.setVisibility(View.VISIBLE);
                    updateTipHeaderTv.setText(getString(R.string.update_tip_header));
                    updateTipTitleTv.setText(getString(R.string.update_tip_title, mUpdate.getEdxposedManagerVersionName() +
                                    "_" +
                                    mUpdate.getVersionName(),
                            String.valueOf(mUpdate.getVersionCode())));
                    updateTipContentTv.setText(mUpdate.getUpdateContent());
                    dismissBtn.setVisibility(View.VISIBLE);
                    break;
                }
            default:
                updateTip.setVisibility(View.GONE);
                break;
        }
    }

}
