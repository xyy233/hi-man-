package com.zhiyazhang.mykotlinapplication.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiya.zhang
 * on 2017/6/7 9:03.
 */

public class Test {
    private List<String> mlist=new ArrayList<>();
    public Test(){
        this.mlist.add("sdaf");
    }

    public List<String> getMyList(){
        return mlist;
    }
}
