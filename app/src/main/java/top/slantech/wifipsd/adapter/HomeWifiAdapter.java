package top.slantech.wifipsd.adapter;

import top.slantech.wifipsd.R;
import top.slantech.wifipsd.bean.HomeWifi;

/**
 * Created by admin on 2016/7/10 0010.
 */
public class HomeWifiAdapter extends CommonNewAdapter<HomeWifi> {
    public HomeWifiAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewNewHolder vh, HomeWifi item, int position) {
        if (item != null) {
            vh.setText(R.id.tv_name, item.getSSID());
            int level = item.getLevel();

            //根据获得的信号强度发送信息
            if (level <= 0 && level >= -50) {
                if (item.getNeedPwd())
                    vh.setImage(R.id.iv_spot, R.mipmap.wifi4_lock);
                else
                    vh.setImage(R.id.iv_spot, R.mipmap.wifi4);
            } else if (level < -50 && level >= -70) {
                if (item.getNeedPwd())
                    vh.setImage(R.id.iv_spot, R.mipmap.wifi3_lock);
                else
                    vh.setImage(R.id.iv_spot, R.mipmap.wifi3);
            } else if (level < -70 && level >= -100) {
                if (item.getNeedPwd())
                    vh.setImage(R.id.iv_spot, R.mipmap.wifi2_lock);
                else
                    vh.setImage(R.id.iv_spot, R.mipmap.wifi2);
            } else {
                if (item.getNeedPwd())
                    vh.setImage(R.id.iv_spot, R.mipmap.wifi1_lock);
                else
                    vh.setImage(R.id.iv_spot, R.mipmap.wifi1);
            }
            vh.setGone(R.id.iv_choose);
        }
    }

    @Override
    protected int getLayoutId(int position, HomeWifi item) {
        return R.layout.list_item_home_wifi;
    }
}
