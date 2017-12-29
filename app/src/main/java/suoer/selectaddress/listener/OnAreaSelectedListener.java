package suoer.selectaddress.listener;


import java.util.List;

import suoer.selectaddress.bean.Area;

public interface OnAreaSelectedListener {
    void GetLastAreaSelected(Area area);
    void GetAllAreaSelected(List<Area> list);
}
