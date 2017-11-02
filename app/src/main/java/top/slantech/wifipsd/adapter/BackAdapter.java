package top.slantech.wifipsd.adapter;

import android.app.Activity;
import android.view.View;

import java.io.File;

import top.slantech.wifipsd.MyApplication;
import top.slantech.wifipsd.R;
import top.slantech.yzlibrary.utils.StringUtils;

/**
 * Created by admin on 2016/7/10 0010.
 */
public class BackAdapter extends CommonNewAdapter<String> {
    public BackAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewNewHolder vh, final String item, final int position) {
        if (!StringUtils.isEmpty(item))
            vh.setText(R.id.tv_name, item);
        vh.setOnClick(R.id.tv_del, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = MyApplication.getBackUpFilePath() + "/" + item;

                File file = new File(path);
                if (file.exists())
                    file.delete();

                removeItem(position);
                MyApplication.cusToast((Activity) mCallback.getContext(), mCallback.getContext().getString(R.string.del_suc), 0);
            }
        });
    }

    @Override
    protected int getLayoutId(int position, String item) {
        return R.layout.list_item_back;
    }
}
