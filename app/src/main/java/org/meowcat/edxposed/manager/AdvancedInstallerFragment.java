package org.meowcat.edxposed.manager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Stream;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.huanchengfly.edxp.interfaces.ButtonCallback;

import org.meowcat.edxposed.manager.util.AssetUtil;
import org.meowcat.edxposed.manager.util.NavUtil;
import org.meowcat.edxposed.manager.util.RootUtil;
import org.meowcat.edxposed.manager.util.json.JSONUtils;
import org.meowcat.edxposed.manager.util.json.XposedTab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class AdvancedInstallerFragment extends Fragment {

    //private static ViewPager mPager;
    private TabLayout mTabLayout;
    private RootUtil mRootUtil = new RootUtil();
    private TabsAdapter tabsAdapter;

    //public static void gotoPage(int page) {mPager.setCurrentItem(page);}

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_advanced_installer, container, false);
        ViewPager mPager = view.findViewById(R.id.pager);
        mTabLayout = view.findViewById(R.id.tab_layout);

        tabsAdapter = new TabsAdapter(getChildFragmentManager());
        mPager.setAdapter(tabsAdapter);
        tabsAdapter.notifyDataSetChanged();
        mTabLayout.setupWithViewPager(mPager);

        mTabLayout.setElevation(0);

        setHasOptionsMenu(true);
        sHandler.postDelayed(() -> {
            new JSONParser().execute();
        }, 100);

        if (!XposedApp.getPreferences().getBoolean("hide_install_warning", false)) {
            @SuppressLint("InflateParams") final View dontShowAgainView = inflater.inflate(R.layout.dialog_install_warning, null);

            new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setTitle(R.string.install_warning_title)
                    .setView(dontShowAgainView)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        CheckBox checkBox = dontShowAgainView.findViewById(android.R.id.checkbox);
                        if (checkBox.isChecked())
                            XposedApp.getPreferences().edit().putBoolean("hide_install_warning", true).apply();
                    })
                    .setCancelable(false)
                    .show();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTabLayout.setSelectedTabIndicatorColor(XposedApp.getColor(Objects.requireNonNull(getContext())));
        mTabLayout.setTabTextColors(XposedApp.getColorByAttr(Objects.requireNonNull(getContext()), R.attr.color_unselected, R.color.color_unselected_light), XposedApp.getColor(Objects.requireNonNull(getContext())));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_installer, menu);
        if (Build.VERSION.SDK_INT < 26) {
            menu.findItem(R.id.dexopt_all).setVisible(false);
            menu.findItem(R.id.speed_all).setVisible(false);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dexopt_all:
                areYouSure(R.string.take_while_cannot_resore, new ButtonCallback() {
                    @Override
                    public void onPositive(DialogInterface mDialog) {
                        super.onPositive(mDialog);
                        //TODO: 用AlertDialog替换
                        new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                                .title(R.string.dexopt_now)
                                .content(R.string.this_may_take_a_while)
                                .progress(true, 0)
                                .cancelable(false)
                                .showListener(dialog -> new Thread("dexopt") {
                                    @Override
                                    public void run() {
                                        RootUtil rootUtil = new RootUtil();
                                        if (!rootUtil.startShell()) {
                                            dialog.dismiss();
                                            NavUtil.showMessage(Objects.requireNonNull(getActivity()), getString(R.string.root_failed));
                                            return;
                                        }

                                        rootUtil.execute("cmd package bg-dexopt-job", new ArrayList<>());

                                        dialog.dismiss();
                                        XposedApp.runOnUiThread(() -> Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_LONG).show());
                                    }
                                }.start()).show();
                    }
                });
                break;
            case R.id.speed_all:
                areYouSure(R.string.take_while_cannot_resore, new ButtonCallback() {
                    @Override
                    public void onPositive(DialogInterface mDialog) {
                        super.onPositive(mDialog);
                        //TODO: 用AlertDialog替換
                        new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                                .title(R.string.speed_now)
                                .content(R.string.this_may_take_a_while)
                                .progress(true, 0)
                                .cancelable(false)
                                .showListener(dialog -> new Thread("dex2oat") {
                                    @Override
                                    public void run() {
                                        RootUtil rootUtil = new RootUtil();
                                        if (!rootUtil.startShell()) {
                                            dialog.dismiss();
                                            NavUtil.showMessage(Objects.requireNonNull(getActivity()), getString(R.string.root_failed));
                                            return;
                                        }

                                        rootUtil.execute("cmd package compile -m speed -a", new ArrayList<>());

                                        dialog.dismiss();
                                        XposedApp.runOnUiThread(() -> Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_LONG).show());
                                    }
                                }.start()).show();
                    }
                });
                break;
            case R.id.reboot:
                if (XposedApp.getPreferences().getBoolean("confirm_reboots", true)) {
                    areYouSure(R.string.reboot, new ButtonCallback() {
                        @Override
                        public void onPositive(DialogInterface dialog) {
                            reboot(null);
                        }
                    });
                } else {
                    reboot(null);
                }
                break;
            case R.id.soft_reboot:
                if (XposedApp.getPreferences().getBoolean("confirm_reboots", true)) {
                    areYouSure(R.string.soft_reboot, new ButtonCallback() {
                        @Override
                        public void onPositive(DialogInterface dialog) {
                            softReboot();
                        }
                    });
                } else {
                    softReboot();
                }
                break;
            case R.id.reboot_recovery:
                if (XposedApp.getPreferences().getBoolean("confirm_reboots", true)) {
                    areYouSure(R.string.reboot_recovery, new ButtonCallback() {
                        @Override
                        public void onPositive(DialogInterface dialog) {
                            reboot("recovery");
                        }
                    });
                } else {
                    reboot("recovery");
                }
                break;
            case R.id.reboot_bootloader:
                if (XposedApp.getPreferences().getBoolean("confirm_reboots", true)) {
                    areYouSure(R.string.reboot_bootloader, new ButtonCallback() {
                        @Override
                        public void onPositive(DialogInterface dialog) {
                            reboot("bootloader");
                        }
                    });
                } else {
                    reboot("bootloader");
                }
                break;
            case R.id.reboot_download:
                if (XposedApp.getPreferences().getBoolean("confirm_reboots", true)) {
                    areYouSure(R.string.reboot_download, new ButtonCallback() {
                        @Override
                        public void onPositive(DialogInterface dialog) {
                            reboot("download");
                        }
                    });
                } else {
                    reboot("download");
                }
                break;
            case R.id.reboot_edl:
                if (XposedApp.getPreferences().getBoolean("confirm_reboots", true)) {
                    areYouSure(R.string.reboot_download, new ButtonCallback() {
                        @Override
                        public void onPositive(DialogInterface dialog) {
                            reboot("edl");
                        }
                    });
                } else {
                    reboot("edl");
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean startShell() {
        if (mRootUtil.startShell())
            return false;

        showAlert(getString(R.string.root_failed));
        return true;
    }

    private void areYouSure(int contentTextId, ButtonCallback yesHandler) {
        new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setTitle(R.string.areyousure)
                .setMessage(contentTextId)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> yesHandler.onPositive(dialog))
                .setNegativeButton(android.R.string.no, (dialog, which) -> yesHandler.onNegative(dialog))
                .show();
    }

    private void showAlert(final String result) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> showAlert(result));
            return;
        }

        new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setMessage(result)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void softReboot() {
        if (startShell())
            return;

        List<String> messages = new LinkedList<>();
        if (mRootUtil.execute("setprop ctl.restart surfaceflinger; setprop ctl.restart zygote", messages) != 0) {
            messages.add("");
            messages.add(getString(R.string.reboot_failed));
            showAlert(TextUtils.join("\n", messages).trim());
        }
    }

    private void reboot(String mode) {
        if (startShell())
            return;

        List<String> messages = new LinkedList<>();

        String command = "/system/bin/svc power reboot";
        if (mode != null) {
            command += " " + mode;
            if (mode.equals("recovery"))
                // create a flag used by some kernels to boot into recovery
                mRootUtil.executeWithBusybox("touch /cache/recovery/boot", messages);
        }

        if (mRootUtil.execute(command, messages) != 0) {
            messages.add("");
            messages.add(getString(R.string.reboot_failed));
            showAlert(TextUtils.join("\n", messages).trim());
        }
        AssetUtil.removeBusybox();
    }

    @SuppressLint("StaticFieldLeak")
    private class JSONParser extends AsyncTask<Void, Void, Boolean> {

        private String newApkVersion = null;
        private String newApkLink = null;
        private String newApkChangelog = null;
        private boolean noZips = false;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String originalJson = JSONUtils.getFileContent(JSONUtils.JSON_LINK);

                final JSONUtils.XposedJson xposedJson = new Gson().fromJson(originalJson, JSONUtils.XposedJson.class);

                List<XposedTab> tabs = Stream.of(xposedJson.tabs)
                        .filter(value -> value.sdks.contains(Build.VERSION.SDK_INT)).toList();

                noZips = tabs.isEmpty();

                for (XposedTab tab : tabs) {
                    tabsAdapter.addFragment(tab.name, BaseAdvancedInstaller.newInstance(tab));
                    sHandler.post(tabsAdapter::notifyDataSetChanged);
                }

                newApkVersion = xposedJson.apk.version;
                newApkLink = xposedJson.apk.link;
                newApkChangelog = xposedJson.apk.changelog;

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(XposedApp.TAG, "AdvancedInstallerFragment -> " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            try {
                tabsAdapter.notifyDataSetChanged();

                if (!result) {
                    StatusInstallerFragment.setError(true/* connection failed */, true /* so no sdks available*/);
                } else {
                    StatusInstallerFragment.setError(false /*connection ok*/, noZips /*if counter is 0 there aren't sdks*/);
                }

                if (newApkVersion == null) return;

                SharedPreferences prefs;
                try {
                    prefs = Objects.requireNonNull(getContext()).getSharedPreferences(Objects.requireNonNull(getContext()).getPackageName() + "_preferences", MODE_PRIVATE);

                    prefs.edit().putString("changelog", newApkChangelog).apply();
                } catch (NullPointerException ignored) {
                }

                Integer a = BuildConfig.VERSION_CODE;
                Integer b = Integer.valueOf(newApkVersion);

                if (a.compareTo(b) < 0) {
                    StatusInstallerFragment.setUpdate(newApkLink, newApkChangelog, getContext());
                }

            } catch (Exception ignored) {
            }

        }
    }

    public static final Handler sHandler = new Handler();

    private class TabsAdapter extends FragmentPagerAdapter {

        private final ArrayList<String> titles = new ArrayList<>();
        private final ArrayList<Fragment> listFragment = new ArrayList<>();

        @SuppressWarnings("deprecation")
        TabsAdapter(FragmentManager mgr) {
            super(mgr);
            addFragment(getString(R.string.status), new StatusInstallerFragment());
        }

        void addFragment(String title, Fragment fragment) {
            titles.add(title);
            listFragment.add(fragment);
            sHandler.post(this::notifyDataSetChanged);
        }

        @Override
        public int getCount() {
            return listFragment.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return listFragment.get(position);
        }

        @Override
        public String getPageTitle(int position) {
            return titles.get(position);
        }
    }

}
