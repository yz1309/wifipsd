package top.slantech.wifipsd.ui.activity;

/*
public class MainActivity extends BaseActivity implements CommonNewAdapter.Callback {

    private static final int Key_Get_List = 1;
    private WifiManager wifiManager;
    private HomeWifiAdapter mAdapter;
    private BroadcastReceiver wifiConnectReceiver;
    private String curSSID = "";
    private List<ScanResult> mScanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);

        ButterKnife.inject(this);


        wifiConnectReceiver = new WifiConnectReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(wifiConnectReceiver, filter);

        int res = (int) SPUtils.get(this, "helpnew" + MyApplication.getVersionCode(this), 0);
        if (res == 1) {
            ivHelpNew.setVisibility(View.GONE);
        } else {
            ivHelpNew.setVisibility(View.VISIBLE);
        }

        switchBtn.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                wifiManager.setWifiEnabled(on);
                if (on) {
                    llNotOpenWifi.setVisibility(View.GONE);
                } else {
                    llNotOpenWifi.setVisibility(View.VISIBLE);
                }
                checkWifi(false);
            }
        });


        // 使用定时器,每隔5秒获得一次信号强度值
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = Key_Get_List;
                mHandler.sendMessage(msg);
            }
        }, 5000, 5000);


        initSwipe();


        if ((int) SPUtils.get(this, "shortcut", 0) != 1) {
            UIHelper.addShortcut(this, getString(R.string.app_name), WelComeActivity.class);
        }

        UIHelper.checkVersion(this, 1);
    }

    private void initSwipe() {
        // 设置下拉刷新时的颜色值,颜色值需要定义在xml中
        swipeLayout.setColorScheme(R.color.mainbg, R.color.blue, R.color.yellow);
        // 设置下拉刷新监听器
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                swipeLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        getWifiList();
                    }
                }, 1000);
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomeWifi item = mAdapter.getItem(position);
                // 如果单击的是当前已连接wifi不予处理
                if (!curSSID.equals(item.getSSID())) {
                    WifiAutoConnectManager manager = new WifiAutoConnectManager(wifiManager);
                    WifiConfiguration configuration = manager.isExsits(item.getSSID());
                    if (configuration == null) {
                        // 弹出输入密码框
                        showPopuWindow(item.getSSID());
                    } else {
                        curSSID = item.getSSID();
                        pbGress.setVisibility(View.VISIBLE);
                        tvCurWifi.setText(getString(R.string.connecting) + " " + item.getSSID());
                        wifiManager.enableNetwork(configuration.networkId, true);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        updateCurWifiInfo();
                    }
                }
            }
        });
    }

    private void showPopuWindow(final String ssid) {
        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.popu_pwd);


        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        //实现铺满全屏效果
        dialogWindow.getDecorView().setPadding(10, 10, 10, 10);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialogWindow.setAttributes(lp);
        *//**
         * 5.0以上默认有个.9图片作为背景，导致不能铺满横向屏幕
         *//*
        dialogWindow.setBackgroundDrawableResource(android.R.color.white);
        TextView tvWifiSSID = (TextView) mDialog.findViewById(R.id.tv_wifi_ssid);
        TextView tvCancel = (TextView) mDialog.findViewById(R.id.tv_cancel);
        final TextView tvOk = (TextView) mDialog.findViewById(R.id.tv_ok);
        final EditText etPwd = (EditText) mDialog.findViewById(R.id.et_pwd);
        final ImageView ivPwd = (ImageView) mDialog.findViewById(R.id.iv_pwd);
        final LinearLayout llConnect = (LinearLayout) mDialog.findViewById(R.id.ll_connect);
        final ProgressBar pbLoading = (ProgressBar) mDialog.findViewById(R.id.progress_loading);
        final TextView tvMes = (TextView) mDialog.findViewById(R.id.tv_mes);
        tvMes.setVisibility(View.GONE);
        etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        final Boolean[] isShow = {true};
        tvWifiSSID.setText(ssid);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        ivPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow[0]) {
                    //隐藏密码
                    etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivPwd.setImageResource(R.drawable.icon_pwd_hide);
                    isShow[0] = false;
                } else {
                    etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivPwd.setImageResource(R.drawable.icon_pwd_show);
                    isShow[0] = true;
                }
            }
        });
        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {
                String ext = etPwd.getText().toString().trim();
                if (ext.length() >= 8) {
                    tvMes.setVisibility(View.GONE);
                    llConnect.setEnabled(true);
                    llConnect.setBackground(getResources().getDrawable(R.drawable.shape_selector_r5_p10_mainbg));
                    tvOk.setTextColor(getResources().getColor(R.color.white));
                } else {
                    llConnect.setEnabled(false);
                    llConnect.setBackground(getResources().getDrawable(R.drawable.shape_selector_r5_p10_gray));
                    tvOk.setTextColor(getResources().getColor(R.color.text_666));
                }
            }
        });
        llConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llConnect.setEnabled(false);
                pbLoading.setVisibility(View.VISIBLE);
                tvOk.setText(getString(R.string.connecting));
                KeyBoardUtils.closeSoftInput(etPwd, MainActivity.this);
                WifiAutoConnectManager wifiAutoConnectManager = new WifiAutoConnectManager(wifiManager);
                wifiManager.disconnect();
                String netID = wifiAutoConnectManager.AddWifiConfig(mScanList, ssid, etPwd.getText().toString());

                ULog.e("链接情况-" + netID);

                if (netID.equals("-1")) {
                    tvMes.setVisibility(View.VISIBLE);

                } else if (netID.startsWith("异常")) {
                    tvMes.setText(netID);
                } else {
                    wifiManager.enableNetwork(Integer.parseInt(netID), true);
                    wifiManager.reconnect();
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Boolean aBoolean = updateCurWifiInfo();
                    if (aBoolean) {
                        mDialog.dismiss();
                    } else {
                        tvMes.setVisibility(View.VISIBLE);
                    }
                }
                pbLoading.setVisibility(View.GONE);
                tvOk.setText(getString(R.string.connect2));
                llConnect.setEnabled(true);
            }
        });
        mDialog.show();
    }

    class WifiConnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo notewokInfo = manager.getActiveNetworkInfo();
                if (notewokInfo != null) {
                    String tempSSID = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
                    if (tempSSID.equals(curSSID)) {
                        tvCurWifi.setText(getString(R.string.has_wifi_connect) + " " + tempSSID);
                    }
                } else {
                    ULog.e("network is null");
                    //tvCurWifi.setText(getString(R.string.connect_failed) + " " + curSSID);
                    //pbGress.setVisibility(View.GONE);
                }
            } else
                ULog.e("其他");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiConnectReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkWifi(true);
    }

    *//**
     * 检查wifi是否打开
     *//*
    private void checkWifi(Boolean bl) {
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            if (bl) {
                llNotOpenWifi.setVisibility(View.GONE);

                switchBtn.setToggleOn();
            }
            getWifiList();
        } else {
            if (bl) {
                llNotOpenWifi.setVisibility(View.VISIBLE);
                switchBtn.setToggleOff();
            }
        }
    }

    @OnClick({R.id.rl_view_pwd, R.id.ll_set, R.id.rl_help, R.id.tv_open_wifi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_view_pwd:
                goActivity(ViewPwdActivity.class);
                break;
            case R.id.ll_set:
                goActivity(SetActivity.class);
                break;
            case R.id.rl_help:
                goActivity(HelpActivity.class);
                SPUtils.put(this, "helpnew" + MyApplication.getVersionCode(this), 1);
                break;
            case R.id.tv_open_wifi:
                try {
                    wifiManager.setWifiEnabled(true);
                    switchBtn.setToggleOn();
                    llNotOpenWifi.setVisibility(View.GONE);
                    checkWifi(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    // 使用Handler实现UI线程与Timer线程之间的信息传递,每5秒刷新数据
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Key_Get_List) {
                getWifiList();
            }
        }
    };

    private void getWifiList() {
        mScanList = wifiManager.getScanResults();
        List<HomeWifi> mList = new ArrayList<>();
        for (int i = 0; i < mScanList.size(); i++) {
            ScanResult scanResult = mScanList.get(i);
            boolean b = checkIsCurrentWifiHasPassword(scanResult.SSID);
            HomeWifi model = new HomeWifi();
            model.setSSID(scanResult.SSID);
            model.setLevel(scanResult.level);
            model.setNeedPwd(b);
            mList.add(model);
        }
        mAdapter = new HomeWifiAdapter(this);
        listview.setAdapter(mAdapter);
        mAdapter.addItem(mList);

        swipeLayout.setRefreshing(false);

        updateCurWifiInfo();
    }

    *//**
     * 更新当前已连接wifi信息
     *//*
    private Boolean updateCurWifiInfo() {
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (isWifiConnected() && connectionInfo != null && wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            curSSID = connectionInfo.getSSID().replace("\"", "");
            ULog.e("curSSID-" + curSSID);
            // <unknown ssid> 魅族在未连接时返回的数据
            if (curSSID.trim().length() > 0 && !curSSID.equals("<unknown ssid>")) {
                tvCurWifi.setText(getString(R.string.has_wifi_connect) + " " + curSSID);
                pbGress.setVisibility(View.GONE);
                return true;
            } else
                tvCurWifi.setText(getString(R.string.not_wifi_connect));
        } else {
            tvCurWifi.setText(getString(R.string.not_wifi_connect));
        }

        pbGress.setVisibility(View.GONE);
        return false;
    }

    *//**
     * 是否连接WIFI
     *
     * @return
     *//*
    public boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Date getSystemTime() {
        return new Date();
    }

    *//**
     * 利用WifiConfiguration.KeyMgmt的管理机制，来判断当前wifi是否需要连接密码
     *
     * @return true：需要密码连接，false：不需要密码连接
     *//*
    public boolean checkIsCurrentWifiHasPassword(String currentWifiSSID) {
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            // 得到当前WifiConfiguration列表，此列表包含所有已经连过的wifi的热点信息，未连过的热点不包含在此表中
            List<WifiConfiguration> wifiConfiguration = wifiManager.getConfiguredNetworks();

            String currentSSID = currentWifiSSID;
            if (currentSSID != null && currentSSID.length() > 2) {
                if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
                    currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
                }

                if (wifiConfiguration != null && wifiConfiguration.size() > 0) {
                    for (WifiConfiguration configuration : wifiConfiguration) {
                        if (configuration != null && configuration.status == WifiConfiguration.Status.CURRENT) {
                            String ssid = null;
                            if (!TextUtils.isEmpty(configuration.SSID)) {
                                ssid = configuration.SSID;
                                if (configuration.SSID.startsWith("\"") && configuration.SSID.endsWith("\"")) {
                                    ssid = configuration.SSID.substring(1, configuration.SSID.length() - 1);
                                }
                            }
                            if (TextUtils.isEmpty(currentSSID) || currentSSID.equalsIgnoreCase(ssid)) {
                                //KeyMgmt.NONE表示无需密码
                                return (!configuration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        //默认为需要连接密码
        return true;
    }

    @InjectView(R.id.iv_help_new)
    ImageView ivHelpNew;
    @InjectView(R.id.ll_not_open_wifi)
    LinearLayout llNotOpenWifi;
    @InjectView(R.id.switch_btn)
    ToggleButton switchBtn;
    @InjectView(R.id.tv_cur_wifi)
    TextView tvCurWifi;
    @InjectView(R.id.listview)
    ListView listview;
    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @InjectView(R.id.pb_gress)
    ProgressBar pbGress;

}*/
