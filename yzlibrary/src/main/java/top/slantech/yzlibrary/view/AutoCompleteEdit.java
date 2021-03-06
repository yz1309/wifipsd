package top.slantech.yzlibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import top.slantech.yzlibrary.R;
import top.slantech.yzlibrary.utils.ULog;


/**
 * 自动列出相关数据
 * 通过setDatas设置数据源
 */
public class AutoCompleteEdit extends AutoCompleteTextView {
    private String[] emailSufixs;
    private boolean mDisableAuto = false;
    private EmailAutoCompleteAdapter adapter;


    public AutoCompleteEdit(Context context) {
        super(context);
        init(context, null);
    }

    public AutoCompleteEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoCompleteEdit(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        ULog.e("init");
        boolean closeAutoComplete = false;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AutoComplete, 0, 0);
        try {
            closeAutoComplete = a.getBoolean(R.styleable.AutoComplete_closeAutoComplete, true);
        } finally {
            a.recycle();
        }

        // 添加删除箭头
        int crossedRes = R.drawable.search_clear;
        drawable = getResources().getDrawable(crossedRes);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                displayDelete(s.length() > 0);
            }
        });

        emailSufixs = new String[]{};
        if (closeAutoComplete) {
            adapter = new EmailAutoCompleteAdapter(context, R.layout.login_auto_complete_item, emailSufixs);
            this.setAdapter(adapter);
            this.setThreshold(1);
        }
    }

    private Drawable drawable;

    private void displayDelete(boolean show) {
        if (show) {
            setDrawableRight(drawable);
        } else {
            setDrawableRight(null);
        }
    }

    private void setDrawableRight(Drawable drawable) {
        setCompoundDrawables(null, null, drawable, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void replaceText(CharSequence text) {
        String t = this.getText().toString();
        int index = t.indexOf("@");
        if (index != -1) {
            super.replaceText(t.substring(0, index) + text);
        } else {
            super.replaceText(text);
        }
    }

    public void setDisableAuto(boolean mDisableAuto) {
        this.mDisableAuto = mDisableAuto;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        //adapter中数据的前半部分，那么adapter中的这条数据将会在下拉框中出现
//        Log.i(TAG + " performFiltering", text.toString() + "   " + keyCode);
        if (mDisableAuto) {
            return;
        }

        String t = text.toString();

        int index = t.indexOf("@");
        if (index != -1) {
            super.performFiltering(t.substring(index), keyCode);
        } else {
            super.performFiltering(text, keyCode);
        }
    }

    private class EmailAutoCompleteAdapter extends ArrayAdapter<String> {

        public EmailAutoCompleteAdapter(Context context, int textViewResourceId, String[] email_s) {
            super(context, textViewResourceId, email_s);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null)
                v = LayoutInflater.from(getContext()).inflate(
                        R.layout.login_auto_complete_item, null);
            TextView tv = (TextView) v.findViewById(R.id.tv);

            String input = AutoCompleteEdit.this.getText().toString();
            int index = input.indexOf("@");
            if (index != -1) {
                input = input.substring(0, index);
                tv.setText(input + getItem(position));
            } else {
                tv.setText(getItem(position));
            }
            return v;
        }
    }

    public void setDatas(String[] str) {
        emailSufixs = str;
        // 重新设置数据源
        adapter = new EmailAutoCompleteAdapter(getContext(), R.layout.login_auto_complete_item, emailSufixs);
        this.setAdapter(adapter);
        this.setThreshold(1);
    }
}
