package top.slantech.wifipsd.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import top.slantech.wifipsd.BaseActivity;
import top.slantech.wifipsd.MyApplication;
import top.slantech.wifipsd.R;
import top.slantech.wifipsd.adapter.CommonNewAdapter;
import top.slantech.wifipsd.adapter.OtherAppsAdapter;
import top.slantech.wifipsd.api.ApiHelper;
import top.slantech.wifipsd.bean.Apps;
import top.slantech.wifipsd.utils.TitleUtils;
import top.slantech.wifipsd.view.RefreshLayout;
import top.slantech.yzlibrary.utils.NetUtils;

public class OtherAppsActivity extends BaseActivity implements CommonNewAdapter.Callback {

    private OtherAppsAdapter mAdapter;

    private int pageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_apps);
        ButterKnife.inject(this);
        TitleUtils.Init(this, getString(R.string.apps));

        initListView();

        initSwipe();
    }

    private void initListView() {
        mAdapter = new OtherAppsAdapter(OtherAppsActivity.this);
        listview.setAdapter(mAdapter);
    }

    private void initSwipe() {
        // 设置下拉刷新时的颜色值,颜色值需要定义在xml中
        swipeLayout.setColorScheme(R.color.mainbg, R.color.blue, R.color.yellow);
        swipeLayoutNoData.setColorScheme(R.color.mainbg, R.color.blue, R.color.yellow);
        // 设置下拉刷新监听器
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                swipeLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refresh();
                    }
                }, 1000);
            }
        });

        // 加载监听器
        swipeLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {

                swipeLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (NetUtils.isNetworkConnected(OtherAppsActivity.this)) {
                            pageIndex++;
                            getList();

                        } else {
                            MyApplication.cusToast(OtherAppsActivity.this, getString(R.string.no_net), 0);
                        }
                        // 加载完后调用该方法
                        swipeLayout.setLoading(false);
                    }
                }, 1500);
            }
        });

        swipeLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (NetUtils.isNetworkConnected(OtherAppsActivity.this)) {
                    swipeLayout.setRefreshing(true);
                    getList();
                } else {
                    MyApplication.cusToast(OtherAppsActivity.this, getString(R.string.no_net), 0);
                    changeUI();
                }
            }
        }, 200);

        swipeLayoutNoData.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                swipeLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refresh();
                    }
                }, 1000);
            }
        });
    }

    private void refresh() {
        if (NetUtils.isNetworkConnected(OtherAppsActivity.this)) {
            swipeLayout.setIsHasData(true);
            pageIndex = 1;
            getList();
        } else {
            MyApplication.cusToast(OtherAppsActivity.this, getString(R.string.no_net), 0);
            swipeLayout.setRefreshing(false);
            swipeLayoutNoData.setRefreshing(false);
        }
    }

    private void getList() {
        OkHttpUtils.get()
                .url(ApiHelper.getInstance().getOtherAppList(this))
                .addParams("pageSize", ApiHelper.Page_Size_20 + "")
                .addParams("pageIndex", pageIndex + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                swipeLayout.setRefreshing(false);
                swipeLayoutNoData.setRefreshing(false);
                changeUI();
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                List<Apps> news = gson.fromJson(response,
                        new TypeToken<List<Apps>>() {
                        }.getType());

                int curSize = 0;
                if (news != null && news.size() > 0) {
                    try {
                        if (pageIndex == 1) {
                            mAdapter.clear();
                        }
                        mAdapter.addItem(news);
                        mAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    curSize = news.size();
                }
                if (curSize < ApiHelper.Page_Size_20) {
                    swipeLayout.setIsHasData(false);
                }
                swipeLayout.setRefreshing(false);
                swipeLayoutNoData.setRefreshing(false);
                changeUI();
            }
        });

    }

    private void changeUI() {
        if (mAdapter != null) {
            if (mAdapter.getDatas() != null && mAdapter.getDatas().size() > 0) {
                swipeLayout.setVisibility(View.VISIBLE);
                swipeLayoutNoData.setVisibility(View.GONE);
            } else {
                swipeLayout.setVisibility(View.GONE);
                swipeLayoutNoData.setVisibility(View.VISIBLE);
            }
        }
    }


    @InjectView(R.id.listview)
    ListView listview;
    @InjectView(R.id.swipe_layout)
    RefreshLayout swipeLayout;
    @InjectView(R.id.swipe_layout_nodata)
    SwipeRefreshLayout swipeLayoutNoData;

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Date getSystemTime() {
        return new Date();
    }
}
