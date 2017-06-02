package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;
import com.c_store.zhiyazhang.cstoremanagement.view.fragment.BusinessFragment;
import com.c_store.zhiyazhang.cstoremanagement.view.fragment.TestFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.c_store.zhiyazhang.cstoremanagement.utils.static_variable.MyVariable.EXIT_APP_ACTION;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:40.
 * 虽然复杂但是没有数据请求就不做mvp模式
 */

public class HomeTableActivity extends MyActivity {

    @BindView(R.id.my_toolbar)
    Toolbar toolbar;
    List<TabItem> mTableItemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initTabHost();
    }

    private void initData() {
        mTableItemList = new ArrayList<>();
        //添加tab
        mTableItemList.add(new TabItem(R.mipmap.icon_home2, R.mipmap.icon_home1, R.string.business, BusinessFragment.class));
        mTableItemList.add(new TabItem(R.mipmap.icon_computer2, R.mipmap.icon_computer1, R.string.two, TestFragment.class));
        mTableItemList.add(new TabItem(R.mipmap.icon_com2, R.mipmap.icon_com1, R.string.three, TestFragment.class));
        mTableItemList.add(new TabItem(R.mipmap.icon_setting2, R.mipmap.icon_setting1, R.string.four, TestFragment.class));
    }

    private void initView() {
        UserBean user = UserBean.getUser();
        toolbar.setTitle(user.getStoreName() + ":" + user.getuName());
        toolbar.setLogo(R.mipmap.app_logo_sort);
        setSupportActionBar(toolbar);
    }

    //初始化主页选项卡视图
    private void initTabHost() {
        //实例化FragmentTabHost对象
        FragmentTabHost fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        //去掉分割线
        fragmentTabHost.getTabWidget().setDividerDrawable(null);

        for (int i = 0; i < mTableItemList.size(); i++) {
            TabItem tabItem = mTableItemList.get(i);
            //实例化一个TabSpec,设置tab的名称和视图
            TabHost.TabSpec tabSpec = fragmentTabHost.newTabSpec(tabItem.getTitleString()).setIndicator(tabItem.getView());
            fragmentTabHost.addTab(tabSpec, tabItem.getFragmentClass(), null);

            //给Tab按钮设置背景
            fragmentTabHost.getTabWidget().getChildAt(i).setBackgroundColor(ContextCompat.getColor(HomeTableActivity.this, R.color.cstore_red));

            //默认选中第一个tab
            if (i == 0) {
                tabItem.setChecked(true);
            }
        }

        fragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                //重置Tab样式
                for (int i = 0; i < mTableItemList.size(); i++) {
                    TabItem tabitem = mTableItemList.get(i);
                    if (tabId.equals(tabitem.getTitleString())) {
                        tabitem.setChecked(true);
                    } else {
                        tabitem.setChecked(false);
                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    //给toolbar创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    * 截取返回键做退出
    * */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("请确认退出系统？")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(EXIT_APP_ACTION);
                        sendBroadcast(intent);//发送退出系统广播  每个接收器都会收到 调动finish（）关闭activity
                        finish();
                    }
                })
                .setNegativeButton("按错了", null)
                .show();
    }

    /*
    * 下方tab栏用内部内，有空了优化做成控件
    * 2017.5.9  8:59
    * */
    class TabItem {
        //正常情况下显示的图片
        private int imageNormal;
        //选中情况下显示的图片
        private int imagePress;
        //tab的名字
        private int title;
        private String titleString;

        //tab对应的fragment
        public Class<? extends Fragment> fragmentClass;

        public View view;
        @BindView(R.id.tabimg)
        ImageView imageView;
        @BindView(R.id.tabtext)
        TextView textView;

        public TabItem(int imageNormal, int imagePress, int title, Class<? extends Fragment> fragmentClass) {
            this.imageNormal = imageNormal;
            this.imagePress = imagePress;
            this.title = title;
            this.fragmentClass = fragmentClass;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return fragmentClass;
        }

        public int getImageNormal() {
            return imageNormal;
        }

        public int getImagePress() {
            return imagePress;
        }

        public int getTitle() {
            return title;
        }

        public String getTitleString() {
            if (title == 0) {
                return "";
            }
            if (TextUtils.isEmpty(titleString)) {
                titleString = getString(title);
            }
            return titleString;
        }

        public View getView() {
            if (this.view == null) {
                this.view = getLayoutInflater().inflate(R.layout.tab_item, null);
                //这里试着用绑定框架做
                ButterKnife.bind(this, view);
                //                this.imageView = (ImageView) this.view.findViewById(R.id.tabimg);
                //                this.textView = (TextView) this.view.findViewById(R.id.tabtext);
                if (this.title == 0) {
                    this.textView.setVisibility(View.GONE);
                } else {
                    this.textView.setVisibility(View.VISIBLE);
                    this.textView.setText(getTitleString());
                }
                this.textView.setVisibility(View.VISIBLE);
                this.textView.setText(getTitleString());
                this.imageView.setImageResource(imageNormal);
            }
            return this.view;
        }

        //切换tab的方法
        public void setChecked(boolean isChecked) {
            if (imageView != null) {
                if (isChecked) {
                    imageView.setImageResource(imagePress);
                } else {
                    imageView.setImageResource(imageNormal);
                }
            }
            if (textView != null && title != 0) {
                if (isChecked) {
                    textView.setTextColor(ContextCompat.getColor(HomeTableActivity.this, R.color.cstore_white));
                } else {
                    textView.setTextColor(ContextCompat.getColor(HomeTableActivity.this, R.color.cstore_green));
                }
            }
        }
    }
}
