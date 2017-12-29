package suoer.selectaddress;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import suoer.selectaddress.listener.OnAreaSelectedListener;


public class BottomDialog extends Dialog {
    private AreaSelector selector;

    public BottomDialog(Context context) {
        super(context, R.style.bottom_dialog);
        init(context);
    }
    public BottomDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }
    public BottomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }
    private void init(Context context) {
        selector = new AreaSelector(context);
        setContentView(selector.getView());

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        //此处设置显示高度
        params.height = dp2px(context, 400);
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
    }
    public void setOnAddressSelectedListener(OnAreaSelectedListener listener) {
        this.selector.setOnAddressSelectedListener(listener);
    }

    public static BottomDialog show(Context context) {
        return show(context, null);
    }

    public static BottomDialog show(Context context, OnAreaSelectedListener listener) {
        BottomDialog dialog = new BottomDialog(context, R.style.bottom_dialog);
        dialog.selector.setOnAddressSelectedListener(listener);
        dialog.show();

        return dialog;
    }

    private int dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
    public View getCustomView() {
    	return selector.getView();  
    	}  
}
