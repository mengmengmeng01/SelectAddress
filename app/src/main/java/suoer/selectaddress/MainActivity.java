package suoer.selectaddress;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import suoer.selectaddress.bean.Area;
import suoer.selectaddress.listener.OnAreaSelectedListener;

public class MainActivity extends Activity implements OnAreaSelectedListener {

    private EditText et_address;
    private BottomDialog dialog;
    private TextView tv_show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_address = (EditText) findViewById(R.id.et_address);
        et_address.setFocusable(false);
        tv_show = (TextView)findViewById(R.id.tv_show);

    }

    public void openSelectorAddressWindow(View v) {
        if(dialog == null){
            dialog = new BottomDialog(MainActivity.this);
            dialog.setOnAddressSelectedListener(this);
        }
        dialog.show();
    }
    @Override
    public void GetLastAreaSelected(Area area) {
        if(dialog != null){
            dialog.dismiss();
        }
        et_address.setText(area.getName());
    }
    @Override
    public void GetAllAreaSelected(List<Area> list) {
        String str = "您获取了\n";
        for (int i=0;i<list.size();i++){
            str=str+"第"+(i+1)+"级是："+list.get(i).toString()+"\n";
        }
        tv_show.setText(str);
    }
}
