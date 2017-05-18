package com.c_store.zhiyazhang.cstoremanagement.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.c_store.zhiyazhang.cstoremanagement.R;

/**
 * Created by zhiya.zhang
 * on 2017/5/9 9:09.
 */

public class TestFragment extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_null,container,false);
    }

}
