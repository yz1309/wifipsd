package top.slantech.wifipsd.adapter;

import android.widget.LinearLayout;

import top.slantech.wifipsd.R;
import top.slantech.wifipsd.bean.Wifis;
import top.slantech.yzlibrary.utils.StringUtils;

/**
 * Created by admin on 2016/7/10 0010.
 */
public class WifiAdapter extends CommonNewAdapter<Wifis> {

    int[] imgarrs = new int[]{
            R.drawable.icon_bg1, R.drawable.icon_bg2, R.drawable.icon_bg3, R.drawable.icon_bg4, R.drawable.icon_bg5, R.drawable.icon_bg6, R.drawable.icon_bg7, R.drawable.icon_bg8,
            R.drawable.icon_bg9, R.drawable.icon_bg10, R.drawable.icon_bg11, R.drawable.icon_bg12, R.drawable.icon_bg13, R.drawable.icon_bg14, R.drawable.icon_bg15, R.drawable.icon_bg16,
            R.drawable.icon_bg17, R.drawable.icon_bg18, R.drawable.icon_bg19
    };

    public WifiAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewNewHolder vh, Wifis item, int position) {
        if (item != null) {
            vh.setText(R.id.tv_name, item.getSsid());
            vh.setText(R.id.tv_pwd, item.getPassword().equals("无密码") ? item.getPassword() : item.getPassword());
            LinearLayout llPriority = vh.getView(R.id.ll_priority);

            if (position - 1 > imgarrs.length)
                llPriority.setBackgroundResource(imgarrs[position / imgarrs.length]);
            else
                llPriority.setBackgroundResource(imgarrs[position]);
            if (!StringUtils.isEmpty(item.getPriority()))
                vh.setText(R.id.tv_priority, item.getPriority().replace(" ", "").replace("\t", "") + "");
            else
                vh.setText(R.id.tv_priority, "2");
            vh.setText(R.id.tv_frequency, "热点频率:" + item.getFrequency());
            vh.setText(R.id.tv_key_mgmt, "加密方式:" + item.getKey_mgmt());
        }
    }

    @Override
    protected int getLayoutId(int position, Wifis item) {
        return R.layout.list_item_wifi;
    }
}
