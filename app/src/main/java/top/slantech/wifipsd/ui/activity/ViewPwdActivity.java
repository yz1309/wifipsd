package top.slantech.wifipsd.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import top.slantech.wifipsd.BaseActivity;
import top.slantech.wifipsd.MyApplication;
import top.slantech.wifipsd.R;
import top.slantech.wifipsd.WelComeActivity2;
import top.slantech.wifipsd.adapter.CommonNewAdapter;
import top.slantech.wifipsd.adapter.WifiAdapter;
import top.slantech.wifipsd.api.ApiHelper;
import top.slantech.wifipsd.bean.Wifis;
import top.slantech.wifipsd.common.KeyConfig;
import top.slantech.wifipsd.common.UIHelper;
import top.slantech.wifipsd.utils.WifiManage;
import top.slantech.wifipsd.view.RefreshLayout;
import top.slantech.yzlibrary.interfaces.OnGetDataLinstener;
import top.slantech.yzlibrary.utils.EquipInfoUtils;
import top.slantech.yzlibrary.utils.SPUtils;
import top.slantech.yzlibrary.utils.SettingUtils;
import top.slantech.yzlibrary.utils.StringUtils;
import top.slantech.yzlibrary.view.ClearEditTextView;

/**
 * 先获取本地数据，设置adapter，然后获取是否root的数据
 */
public class ViewPwdActivity extends BaseActivity implements CommonNewAdapter.Callback {

    private WifiAdapter mAdapter;
    private WifiManage wifiManage;
    //private WifiInfo wifiInfo = null;
    //private WifiManager wifiManager = null;
    //private Handler handler;
    //信号强度值
    //private int level;
    private Dialog dialog;
    private Wifis mWifi;
    // 保存从root文件中获取的数据
    private List<Wifis> mRootFileList;
    // 保存从SP中获取的数据
    private List<Wifis> mSPList;
    // 保存总的数据
    private List<Wifis> mTotalList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_view_pwd);
        ButterKnife.inject(this);

        wifiManage = new WifiManage();
        mRootFileList = new ArrayList<>();
        mSPList = new ArrayList<>();

        initDialog();

        initSwipe();

        initListView();

        initData();

        initEvent();

        initMes();



        if ((int) SPUtils.get(this, "shortcut", 0) != 1) {
            UIHelper.addShortcut(this, getString(R.string.app_name), WelComeActivity2.class);
        }

        UIHelper.checkVersion(this, 1);
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Boolean isRoot = (Boolean) msg.obj;
                if (isRoot) {
                    showRooted();
                    getNewList();
                    getList();
                    //initWifi();
                } else {
                    if (mSPList != null && mSPList.size() > 0) {
                        getNewList();
                        //mRootFileList = mSPList;
                    } else
                        showRoot();
                }
            }
        }
    };

    /**
     * 混合SP和RootFile的数据
     */
    private void getNewList() {
        mTotalList = new ArrayList<>();
        if (mSPList != null && mSPList.size() > 0) {
            for (int j = 0; j < mSPList.size(); j++) {
                mTotalList.add(mSPList.get(j));
            }
        }
        if (mRootFileList != null && mRootFileList.size() > 0) {
            for (int j = 0; j < mRootFileList.size(); j++) {
                if (!checkIsExist(mRootFileList.get(j), mTotalList)) {
                    mTotalList.add(mRootFileList.get(j));
                }
            }
        }


        if (mTotalList != null && mTotalList.size() > 0) {
            mAdapter.clear();
            mAdapter.addItem(mTotalList);
        }
    }

    /**
     * 判断wifi名称以及密码是否已存在list中
     *
     * @param wifis
     * @param tempList
     * @return
     */
    private boolean checkIsExist(Wifis wifis, List<Wifis> tempList) {
        if (tempList != null && tempList.size() > 0) {
            for (int i = 0; i < tempList.size(); i++) {
                if (tempList.get(i).getSsid().equals(wifis.getSsid()) && tempList.get(i).getPassword().equals(wifis.getPassword())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return false;
            }
        });
        dialog.setContentView(R.layout.dialog_item);
        tvDialogTitle = (TextView) dialog.findViewById(R.id.tv_title);
        tvDialogCopyPwd = (TextView) dialog.findViewById(R.id.tv_copy_pwd);
        tvDialogCopyName = (TextView) dialog.findViewById(R.id.tv_copy_name);
        tvDialogShareFrd = (TextView) dialog.findViewById(R.id.tv_share_frd);
        tvDialogDelWifi = (TextView) dialog.findViewById(R.id.tv_del_wifi);
        tvDialogCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
    }

    private void initData() {
        // 获取本地数据
        String res = (String) SPUtils.get(this, KeyConfig.Key_WiFi_Data, "");
        List<Wifis> list =
                MyApplication.createGson().fromJson(res,
                        new TypeToken<List<Wifis>>() {
                        }.getType());
        if (list != null && list.size() > 0) {
            mSPList = list;
        }

        SettingUtils.isRootSystem2(new OnGetDataLinstener() {
            @Override
            public void onBack(Boolean isRoot) {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = isRoot;
                mHandler.sendMessage(msg);
            }
        });
    }

    private void initListView() {
        mAdapter = new WifiAdapter(this);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.show();
                mWifi = mAdapter.getItem(position);
                tvDialogTitle.setText(mWifi.getSsid());
            }
        });

    }

    private void initSwipe() {
        // 设置下拉刷新时的颜色值,颜色值需要定义在xml中
        swipeLayout.setColorScheme(R.color.mainbg, R.color.blue, R.color.yellow);
        swipeLayoutNoData.setColorScheme(R.color.blue, R.color.mainbg, R.color.yellow);
        //swipNoRoot.setColorScheme(R.color.blue, R.color.mainbg, R.color.yellow);
        // 设置下拉刷新监听器
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                swipeLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        getList();
                    }
                }, 1000);
            }
        });


        swipeLayoutNoData.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                swipeLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        etSearch.setText("");
                        getList();
                    }
                }, 1000);
            }
        });

//        swipNoRoot.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//
//                swipeLayout.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        etSearch.setText("");
//                        getList();
//                    }
//                }, 1000);
//            }
//        });

    }

    private void getList() {
        try {
            swipeLayout.setRefreshing(true);
            List<Wifis> tempList = wifiManage.getWifiList();
            if (tempList != null && tempList.size() > 0) {
                mRootFileList = tempList;
                getNewList();
                // 保存数据至 SP 中
                String s = MyApplication.createGson().toJson(tempList);
                SPUtils.put(this, KeyConfig.Key_WiFi_Data, s);
            }
            swipeLayout.setIsHasData(false);
            swipeLayout.setRefreshing(false);
            swipeLayoutNoData.setRefreshing(false);
            changeUI();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeUI() {
        if (mAdapter != null) {
            if (mAdapter.getDatas() != null && mAdapter.getDatas().size() > 0) {
                swipeLayout.setVisibility(View.VISIBLE);
                swipeLayoutNoData.setVisibility(View.GONE);
                showRooted();
            } else {
                swipeLayout.setVisibility(View.GONE);
                swipeLayoutNoData.setVisibility(View.VISIBLE);
                showRoot();
            }
            if (swipeLayout.isRefreshing())
                swipeLayout.setRefreshing(false);
            if (swipeLayoutNoData.isRefreshing())
                swipeLayoutNoData.setRefreshing(false);
        }
    }

    private void showRooted() {
        llSearch.setVisibility(View.VISIBLE);
        swipeLayout.setVisibility(View.VISIBLE);
        swipeLayoutNoData.setVisibility(View.GONE);
        llNotRoot.setVisibility(View.GONE);
    }

    private void showRoot() {
        llSearch.setVisibility(View.GONE);
        swipeLayout.setVisibility(View.GONE);
        swipeLayoutNoData.setVisibility(View.GONE);
        llNotRoot.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.tv_kingroot, R.id.tv_root_baidu,
            R.id.tv_root_jingling, R.id.tv_root_dashi,
            R.id.tv_yijian_root, R.id.tv_360_root,
            R.id.rl_help, R.id.ll_set
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_kingroot:
                UIHelper.gotoMarket(this, ApiHelper.Root_King_Pck_Name);
                break;
            case R.id.tv_root_baidu:
                UIHelper.gotoMarket(this, ApiHelper.Root_BaiDu_Pck_Name);
                break;
            case R.id.tv_root_jingling:
                UIHelper.gotoMarket(this, ApiHelper.Root_JingLing_Pck_Name);
                break;
            case R.id.tv_root_dashi:
                UIHelper.gotoMarket(this, ApiHelper.Root_DaShi_Pck_Name);
                break;
            case R.id.tv_yijian_root:
                UIHelper.gotoMarket(this, ApiHelper.Root_YiJian_Pck_Name);
                break;
            case R.id.tv_360_root:
                UIHelper.gotoMarket(this, ApiHelper.Root_360_Pck_Name);
                break;
            case R.id.rl_help:
                goActivity(HelpActivity.class);
                SPUtils.put(this, "helpnew" + MyApplication.getVersionCode(this), 1);
                break;
            case R.id.ll_set:
                goActivity(SetActivity.class);
                break;
        }
    }

    /**
     * 获取当前连接wifi 暂时 pass
     */
    private void initWifi() {
        // 获得WifiManager
//        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        // 使用定时器,每隔5秒获得一次信号强度值
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                wifiInfo = wifiManager.getConnectionInfo();
//                //根据获得的信号强度发送信息
//                Message msg = new Message();
//                if (wifiInfo == null || StringUtils.isEmpty(wifiInfo.getBSSID())) {
//                    msg.obj = null;
//                } else {
//                    //获得信号强度值
//                    level = wifiInfo.getRssi();
//                    msg.obj = wifiInfo.getSSID().toString();
//                    msg.what = level;
//                }
//                handler.sendMessage(msg);
//            }
//        }, 5000, 10000);
        // 使用Handler实现UI线程与Timer线程之间的信息传递,每10秒告诉UI线程获得wifiInto
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                List<Wifis> datas = mAdapter.getDatas();
//                if (msg.obj == null) {
//                    if (datas != null && datas.size() > 0) {
//                        for (int i = 0; i < datas.size(); i++) {
//                            if (datas.get(i).getChecked()) {
//                                mAdapter.getDatas().get(i).setChecked(false);
//                                mAdapter.notifyDataSetChanged();
//                                break;
//                            }
//                        }
//                    }
//                } else {
//                    if (datas != null && datas.size() > 0) {
//                        for (int i = 0; i < datas.size(); i++) {
//                            if (datas.get(i).getSsid().equals(msg.obj.toString().replace("\"", ""))) {
//                                Wifis wifis = mAdapter.getDatas().get(i);
//                                mAdapter.getDatas().remove(wifis);
//                                wifis.setRssi(msg.what);
//                                wifis.setChecked(true);
//                                mAdapter.addItem(0, wifis);
//                                mAdapter.notifyDataSetChanged();
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//        };
    }

    private void initEvent() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String search = s.toString();
                    // 搜索基于当前选择区域 下的所有数据
                    if (!TextUtils.isEmpty(search.trim())) {
                        List<Wifis> tempList = new ArrayList<Wifis>();
                        if (mTotalList != null && mTotalList.size() > 0) {
                            for (int i = 0; i < mTotalList.size(); i++) {
                                if (mTotalList.get(i).getSsid().contains(s) || mTotalList.get(i).getPassword().contains(s)) {
                                    tempList.add(mTotalList.get(i));
                                }
                            }
                        }

                        if (tempList != null && tempList.size() > 0) {
                            mAdapter.clear();
                            mAdapter.addItem(tempList);
                        }
                    } else
                        getList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        tvDialogCopyPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringUtils.copyTxt(ViewPwdActivity.this, mWifi.getPassword());
                MyApplication.cusToast(ViewPwdActivity.this, mWifi.getSsid() + " " + getString(R.string.pwd_is_copy), 0);
                dialog.dismiss();
            }
        });

        tvDialogCopyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringUtils.copyTxt(ViewPwdActivity.this, mWifi.getSsid());
                MyApplication.cusToast(ViewPwdActivity.this, mWifi.getSsid() + " " + getString(R.string.wifi_is_copy), 0);
                dialog.dismiss();
            }
        });
        tvDialogShareFrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = "热点：" + mWifi.getSsid() + " 密码：" + mWifi.getPassword();
                SettingUtils.systemShareText(ViewPwdActivity.this, content, getString(R.string.app_name));
                dialog.dismiss();
            }
        });
        tvDialogDelWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.getDatas().remove(mWifi);
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
                MyApplication.cusToast(ViewPwdActivity.this, getString(R.string.del_suc), 0);
            }
        });
        tvDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void initMes() {
        int res = (int) SPUtils.get(this, "helpnew" + MyApplication.getVersionCode(this), 0);
        if (res == 1) {
            ivHelpNew.setVisibility(View.GONE);
        } else {
            ivHelpNew.setVisibility(View.VISIBLE);
        }

        int old = (int) SPUtils.get(this, "old" + MyApplication.getVersionCode(this), 0);
        if (old == 0) {
            String mes = getString(R.string.ques);
            Boolean bl = false;

            String[] versions = new String[0];
            try {
                versions = EquipInfoUtils.getVersion();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (versions.length == 5) {
                if (versions[4].toLowerCase().contains("xiaomi")) {
                    bl = true;
                }
            }

            if (bl) {
                mes = getString(R.string.xiaomi_user) + "\r\n" + getString(R.string.xiaomi_root1) + "\r\n" +
                        getString(R.string.xiaomi_root2) + "\r\n" + getString(R.string.xiaomi_root3) + "\r\n" + getString(R.string.xiaomi_root4)
                        + "\r\n" + getString(R.string.xiaomi_root5) + "\r\n" + getString(R.string.xiaomi_root6)
                        + "\r\n" + getString(R.string.xiaomi_root7) + "\r\n" + getString(R.string.xiaomi_root8) + "\r\n" + getString(R.string.ques);
            } else {
                mes = getString(R.string.ques);
            }

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.mes))
                    .setMessage(mes)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SPUtils.put(ViewPwdActivity.this, "old" + MyApplication.getVersionCode(ViewPwdActivity.this), 1);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Date getSystemTime() {
        return new Date();
    }

    TextView tvDialogTitle;
    TextView tvDialogCopyPwd;
    TextView tvDialogCopyName;
    TextView tvDialogShareFrd;
    TextView tvDialogDelWifi;
    TextView tvDialogCancel;
    @InjectView(R.id.ll_search)
    LinearLayout llSearch;
    @InjectView(R.id.et_search)
    ClearEditTextView etSearch;
    @InjectView(R.id.listview)
    ListView listview;
    @InjectView(R.id.swipe_layout)
    RefreshLayout swipeLayout;
    @InjectView(R.id.swipe_layout_nodata)
    SwipeRefreshLayout swipeLayoutNoData;
    @InjectView(R.id.ll_not_root)
    LinearLayout llNotRoot;

    @InjectView(R.id.iv_help_new)
    ImageView ivHelpNew;


}
