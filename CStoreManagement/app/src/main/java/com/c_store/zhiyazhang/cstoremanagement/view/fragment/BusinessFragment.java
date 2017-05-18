package com.c_store.zhiyazhang.cstoremanagement.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.utils.image.ImageSlideshow;
import com.c_store.zhiyazhang.cstoremanagement.view.activity.ContractTypeActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by zhiya.zhang
 * on 2017/5/9 9:08.
 */

public class BusinessFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.is_gallery)
    ImageSlideshow isGallery;
    @BindView(R.id.gg1)
    LinearLayout gg1;
    @BindView(R.id.gg2)
    LinearLayout gg2;
    @BindView(R.id.gg3)
    LinearLayout gg3;
    @BindView(R.id.gg4)
    LinearLayout gg4;
    @BindView(R.id.gg5)
    LinearLayout gg5;
    @BindView(R.id.gg6)
    LinearLayout gg6;
    @BindView(R.id.gg7)
    LinearLayout gg7;
    @BindView(R.id.gg8)
    LinearLayout gg8;
    @BindView(R.id.gg9)
    LinearLayout gg9;
    Unbinder unbinder;
    List<String> imageUrlList;
    List<String> titleList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home1, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData(view);
        return view;
    }

    private void initData(View view) {
        imageUrlList = new ArrayList<>();
        titleList = new ArrayList<>();
        String[] imageUrls = {"http://pic3.zhimg.com/b5c5fc8e9141cb785ca3b0a1d037a9a2.jpg",
                "http://pic2.zhimg.com/551fac8833ec0f9e0a142aa2031b9b09.jpg",
                "http://pic2.zhimg.com/be6f444c9c8bc03baa8d79cecae40961.jpg",
                "http://pic1.zhimg.com/b6f59c017b43937bb85a81f9269b1ae8.jpg",
                "http://pic2.zhimg.com/a62f9985cae17fe535a99901db18eba9.jpg"};
        String[] titles = {"公告1",
                "公告2",
                "公告3",
                "公告4",
                "公告5"};
        for (int i = 0; i < 5; i++) {
            isGallery.addImageTitle(imageUrls[i], titles[i]);
        }

        //给isGallery设置数据
        isGallery.setDotSpace(16);
        isGallery.setDotSize(16);
        isGallery.setDelay(4000);
        isGallery.setOnItemClickListener(new ImageSlideshow.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        Toast.makeText(BusinessFragment.this.getActivity(), "clickGallery1", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(BusinessFragment.this.getActivity(), "clickGallery2", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(BusinessFragment.this.getActivity(), "clickGallery3", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(BusinessFragment.this.getActivity(), "clickGallery4", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(BusinessFragment.this.getActivity(), "clickGallery5", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
        isGallery.commit();
    }

    @Override
    public void onDestroyView() {
        // 释放资源
        isGallery.releaseResource();
        super.onDestroyView();
        //控件解绑
        unbinder.unbind();
    }

    @OnClick({R.id.gg1, R.id.gg2, R.id.gg3, R.id.gg4, R.id.gg5, R.id.gg6, R.id.gg7, R.id.gg8, R.id.gg9})
    public void Onclick(View view) {
        switch (view.getId()) {
            case R.id.gg1:
                startActivity(new Intent(BusinessFragment.this.getActivity(), ContractTypeActivity.class));
                break;
            case R.id.gg2:
                Toast.makeText(this.getActivity(), "未完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.gg3:
                Toast.makeText(this.getActivity(), "未完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.gg4:
                Toast.makeText(this.getActivity(), "未完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.gg5:
                Toast.makeText(this.getActivity(), "未完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.gg6:
                Toast.makeText(this.getActivity(), "未完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.gg7:
                Toast.makeText(this.getActivity(), "未完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.gg8:
                Toast.makeText(this.getActivity(), "未完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.gg9:
                Toast.makeText(this.getActivity(), "未完成", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
