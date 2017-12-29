package suoer.selectaddress;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import suoer.selectaddress.adapter.AreaAdapter;
import suoer.selectaddress.bean.Area;
import suoer.selectaddress.bean.ListsUtils;
import suoer.selectaddress.listener.OnAreaSelectedListener;

/***/
public class AreaSelector {
    //index从1开始
    private Map<Integer,Integer> INDEX_TAB = new HashMap<Integer,Integer>();//tab 下标
    private Map<Integer,Integer> SELETC_INDEX = new HashMap<Integer,Integer>();//所选中下标
    private Map<Integer,TextView> tvmap = new HashMap<Integer,TextView>(); //tab TextView集合
    private Map<Integer,List<Area>> addressmap = new HashMap<Integer,List<Area>>(); //数据集合
    private static final int INDEX_INVALID = -1;
    private final Context context;
    private OnAreaSelectedListener listener;
    private View view;
    private View indicator;
    private ProgressBar progressBar;
    private ListView listView;
    private AreaAdapter adapter;
    private RelativeLayout rltv;
    private int tabIndex = 1;//从1开始
    public AreaSelector(Context context) {
        this.context = context;
        SELETC_INDEX.put(tabIndex,INDEX_INVALID);
        initViews();
        retrieveArea("",1);//获取第一级数据
    }
    private void initViews() {
        view = LayoutInflater.from(context).inflate(R.layout.area_selector, null);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        this.listView = (ListView) view.findViewById(R.id.listView);
        this.indicator = view.findViewById(R.id.indicator);
        this.rltv = (RelativeLayout)view.findViewById(R.id.rl_tv);
        addView(tabIndex);
        this.listView.setOnItemClickListener(new OnItemClick());
    }
    private void addView(int i){
        //动态新增TextView 设置一行3个Tab
        TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.simple_text_view, rltv, false);
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
             350, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int t = i-1;
        textView.setId(1000+t/3*10+t%3);
        if (t==0){
            //第一个
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);  //第一个 左上
        }else
        if (t%3==0){
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_LEFT, 1000+(t-1)/3*10+t%3);
            relativeLayoutParams.addRule(RelativeLayout.BELOW, 1000+(t-1)/3*10+t%3); //下面
        }else{
            relativeLayoutParams.addRule(RelativeLayout.RIGHT_OF, 1000+t/3*10+t%3-1); //右面
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_TOP, 1000+t/3*10+t%3-1);
        }
        textView.setLayoutParams(relativeLayoutParams);
        rltv.addView(textView);
        tvmap.put(i,textView);
        textView.setOnClickListener(new OnTabClickListener(i));
        updateIndicator(textView);
    }
    public View getView() {
        return view;
    }
    private void updateIndicator(final TextView tv){
        view.post(new Runnable() {
            @Override
            public void run() {
                buildIndicatorAnimatorTowards(tv).start();
            }
        });
    }
    private AnimatorSet buildIndicatorAnimatorTowards(TextView tab) {
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(indicator, "X", indicator.getX(), tab.getX());
        final ViewGroup.LayoutParams params = indicator.getLayoutParams();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(params.width, tab.getMeasuredWidth());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.width = (Integer) animation.getAnimatedValue();
                indicator.setLayoutParams(params);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.playTogether(xAnimator, widthAnimator);
        return set;
    }
    private class OnTabClickListener implements  View.OnClickListener{
        private int index;
        OnTabClickListener(int index){
            this.index = index;
        }
        @Override
        public void onClick(View view) {
            INDEX_TAB.put(index,index);
            tabIndex = index;
            adapter = new AreaAdapter(addressmap.get(index),SELETC_INDEX.get(index));
            listView.setAdapter(adapter);
            if (SELETC_INDEX.get(index) != INDEX_INVALID) {
                listView.setSelection(SELETC_INDEX.get(index));
            }
            updateTabsVisibility();
            updateIndicator(tvmap.get(index));
        }
    }
    private void updateTabsVisibility() {
        for (int i=1;i<tvmap.size()+1;i++){
            tvmap.get(i).setVisibility(ListsUtils.notEmpty(addressmap.get(i))? View.VISIBLE: View.GONE);
            tvmap.get(i).setEnabled(tabIndex != i);
        }
    }
    private class OnItemClick  implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Area organizationInfor = adapter.getItem(position);
            tvmap.get(tabIndex).setText(organizationInfor.getName());
            for (int i=tabIndex+1;i<tvmap.size();i++){
                tvmap.get(i).setText("");//清空已有显示
            }
            for (int j=tabIndex+1;j<addressmap.size();j++){
                addressmap.put(j,null);
            }
            // 更新已选中项
            SELETC_INDEX.put(tabIndex,position);
            for (int x=tabIndex+1;x<SELETC_INDEX.size();x++){
                SELETC_INDEX.put(x,INDEX_INVALID);
            }
            adapter.setIndex(position);
            adapter.notifyDataSetChanged();
            //获取下一级
            retrieveArea(organizationInfor.getId(), organizationInfor.getLevel()+1);
            updateTabsVisibility();
        }
    }
    private void callbackInternal() {
        try{
        if (listener != null) {
            //获取当前选择的最后一级别数据
            Area organizationInfor = addressmap.get(tabIndex) == null ||SELETC_INDEX.get(tabIndex) == INDEX_INVALID ? null : addressmap.get(tabIndex).get(SELETC_INDEX.get(tabIndex));
            if (null!=organizationInfor){
                Log.e("回调",organizationInfor.getName());
                listener.GetLastAreaSelected(organizationInfor);
            }
            //获取所有选择级别的数据（不需要可屏蔽）
            ArrayList<Area> resultdata = new ArrayList<Area>();
            for (int i=1;i<=tabIndex;i++){
                Area area = addressmap.get(i) == null ||SELETC_INDEX.get(i) == INDEX_INVALID ? null : addressmap.get(i).get(SELETC_INDEX.get(i));
                resultdata.add(area);
            }
            listener.GetAllAreaSelected(resultdata);

        }
        }catch (Exception e){
            e.printStackTrace();
            e.printStackTrace();
        }
    }
    private void updateProgressVisibility() {
        ListAdapter adapter = listView.getAdapter();
        int itemCount = adapter.getCount();
        progressBar.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
    }
    //获取区域
    private void retrieveArea(String pid, int level) {
        progressBar.setVisibility(View.VISIBLE);
        getAreaByLevelParent(pid, level);
    }
    public void setOnAddressSelectedListener(OnAreaSelectedListener listener) {
        this.listener = listener;
    }
    private void getAreaByLevelParent(final String pid, final int Level){
        ArrayList<Area> datas = initData();
        ArrayList<Area> result = new ArrayList<Area>();
        //根据父节点Id和所处级别level获取数据
        for (int i=0;i<datas.size();i++){
            if (pid.equals(datas.get(i).getPid())&&Level==datas.get(i).getLevel()){
                result.add(datas.get(i));
            }
        }
        progressBar.setVisibility(View.GONE);
        addressmap.put(Level, result);
        if (ListsUtils.notEmpty(result)) {
            // 以次级内容更新列表
            SELETC_INDEX.put(Level, -1);
            adapter = new AreaAdapter(result, SELETC_INDEX.get(Level));
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
            // 更新索引为次级
            tabIndex = Level;
            addView(tabIndex);
            updateTabsVisibility();
            updateProgressVisibility();
            updateIndicator(tvmap.get(Level));
        } else {
            callbackInternal();
        }
    }

    private ArrayList<Area> initData(){
        //该数据可以从服务器中获取
        ArrayList<Area> datalist = new ArrayList<Area>();
        datalist.add(new Area("id1","北京",1,"",0));
        datalist.add(new Area("id1-1","北京市",2,"id1",1));
        datalist.add(new Area("id1-1-1","海淀",3,"id1-1",2));
        datalist.add(new Area("id1-1-1-1","海淀区",4,"id1-1-1",3));
        datalist.add(new Area("id1-1-1-1-1","海淀区-1",5,"id1-1-1-1",4));
        datalist.add(new Area("id1-1-1-1-1-1","海淀区-1-1",6,"id1-1-1-1-1",5));
        datalist.add(new Area("id1-1-1-1-1-1-1","海淀区-1-1-1",7,"id1-1-1-1-1-1",6));
        datalist.add(new Area("id1-1-2","朝阳",3,"id1-1",2));
        datalist.add(new Area("id1-1-2-1","朝阳区",4,"id1-1-1",3));
        datalist.add(new Area("id1-1-3","昌平",3,"id1-1",2));
        datalist.add(new Area("id1-1-3-1","昌平区",4,"id1-1-1",3));
        datalist.add(new Area("id1-1-4","丰台",3,"id1-1",2));
        datalist.add(new Area("id1-1-4-1","丰台区",4,"id1-1-1",3));

        datalist.add(new Area("id2","天津",1,"",0));
        datalist.add(new Area("id2-1","天津市",2,"id2",1));
        datalist.add(new Area("id2-1-1","天津1",3,"id2-1",2));
        datalist.add(new Area("id2-1-2","天津2",3,"id2-1",2));
        datalist.add(new Area("id2-1-3","天津3",3,"id2-1",2));
        datalist.add(new Area("id2-1-1-1","天津1-1",4,"id2-1-1",3));
        datalist.add(new Area("id2-1-2-1","天津2-1",4,"id2-1-2",3));


        datalist.add(new Area("id3","上海",1,"",0));
        datalist.add(new Area("id3-1","上海市",2,"id3",1));
        datalist.add(new Area("id3-1-1","上海1",3,"id3-1",2));
        datalist.add(new Area("id3-1-2","上海2",3,"id3-1",2));
        datalist.add(new Area("id3-1-3","上海3",3,"id3-1",2));
        datalist.add(new Area("id3-1-1-1","上海1-1",4,"id3-1-1",3));
        datalist.add(new Area("id3-1-2-1","上海2-1",4,"id3-1-2",3));
        datalist.add(new Area("id3-1-2-1-1","上海2-1-1",5,"id3-1-2-1",4));
        datalist.add(new Area("id3-1-2-1-1-1","上海2-1-1-1",6,"id3-1-2-1-1",5));
        datalist.add(new Area("id3-1-2-1-1-1-1","上海2-1-1-1-1",7,"id3-1-2-1-1-1",6));
        datalist.add(new Area("id3-1-2-1-1-1-1-1","上海2-1-1-1-1-1",8,"id3-1-2-1-1-1-1",7));



        datalist.add(new Area("id4","西安",1,"",0));
        datalist.add(new Area("id5","河南",1,"",0));
        datalist.add(new Area("id6","广东",1,"",0));
        datalist.add(new Area("id7","安徽",1,"",0));

        return datalist;



    }


}
