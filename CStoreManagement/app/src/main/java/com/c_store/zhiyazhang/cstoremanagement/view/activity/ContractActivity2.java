package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractTypeBean;
import com.c_store.zhiyazhang.cstoremanagement.presenter.contract.ContractAdapter;
import com.c_store.zhiyazhang.cstoremanagement.presenter.contract.ContractPresenter;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyLinearlayoutManager;
import com.c_store.zhiyazhang.cstoremanagement.utils.MySwipeRefresh;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyToast;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;
import com.c_store.zhiyazhang.cstoremanagement.view.interfaceview.ContractView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhiya.zhang
 * on 2017/5/16 15:40.
 */

public class ContractActivity2 extends MyActivity implements ContractView {
    /* @BindView(R.id.lw_sales)
     TextView lwSales;
     @BindView(R.id.lw2_sales)
     TextView lw2Sales;
     @BindView(R.id.lw3_sales)
     TextView lw3Sales;
     @BindView(R.id.exp_sales)
     TextView expSales;
     @BindView(R.id.sug_sales)
     TextView sugSales;
     @BindView(R.id.tonightCount2)
     TextView tonightCount2;
     @BindView(R.id.real_count)
     TextView realCount;
     @BindView(R.id.min_count)
     TextView minCount;
     @BindView(R.id.max_count)
     TextView maxCount;
     @BindView(R.id.inv)
     TextView inv;*/
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mySpinner)
    Spinner sort;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.swipe_recycler)
    RecyclerView recycler;
    @BindView(R.id.my_swipe)
    MySwipeRefresh swipe;
    /*@BindView(R.id.typeDesc)
    LinearLayout typeDesc;*/
    @BindView(R.id.done)
    Button done;
    @BindView(R.id.noMessage)
    TextView noMessage;
    Intent i;
    ContractPresenter presenter = new ContractPresenter(this, this);
    private ContractTypeBean upCTB;
    //订量倒序
    private static final String TODAY_SORT_DESC = "act_ord_qty__desc";
    //品号倒序
    private static final String COMMODIFY_ID_SORT_DESC = "item_id__desc";
    //品名倒序
    private static final String COMMODIFY_NAME_SORT_DESC = "item_name__desc";
    //价格倒序
    private static final String MONEY_SORT_DESC = "price__desc";
    //订量正序
    private static final String TODAY_SORT = "act_ord_qty";
    //品号正序
    private static final String COMMODIFY_ID_SORT = "item_id";
    //品名正序
    private static final String COMMODIFY_NAME_SORT = "item_name";
    //价格正序
    private static final String MONEY_SORT = "price";
    private boolean isSearch;
    private int nowPage;
    private int lastVisibleItem;
    ContractAdapter adapter;
    MyLinearlayoutManager layoutManager;
    int total = 5201;
    MenuItem item;
    boolean ispeople = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //先去查询是否是通过搜索进入的，然后根据结果给页面赋值数据或隐藏
        initIsSearch();
        //设置下拉刷新和Recycler
        initSwipeAndRecycler();
        //设置排序
        initSort();
        //启动加载
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (swipe.isEnabled()) {
                    //根据是搜索或是得到全部去选择执行
                    isSearchOrGetAll();
                }
            }
        });
        swipe.setProgressViewEndTarget(true, 100);
        swipe.autoRefresh();
    }


    //设置排序
    private void initSort() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spiner_text_item, getResources().getStringArray(R.array.mySort));
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        sort.setAdapter(adapter);
        sort.setSelection(0, false);
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isSearchOrGetAll();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //根据是搜索或是得到全部去选择执行
    private void isSearchOrGetAll() {
        if (isSearch) {
            presenter.searchAllContract();
        } else {
            presenter.getAllContract();
        }
    }

    //设置下拉刷新和Recycler
    private void initSwipeAndRecycler() {
        swipe.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.cstore_white));
        swipe.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipe.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.cstore_red),
                ContextCompat.getColor(this, R.color.yellow),
                ContextCompat.getColor(this, R.color.blue),
                ContextCompat.getColor(this, R.color.cstore_green));
        //设置下拉刷新是否能用
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    if (ispeople) {
                        swipe.setEnabled(true);
                    } else {
                        swipe.setEnabled(false);
                    }
                } else {
                    swipe.setEnabled(false);
                }
            }
        });
        layoutManager = new MyLinearlayoutManager(ContractActivity2.this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
    }

    //查询是否是通过搜索进入的，然后根据结果给页面赋值数据或隐藏
    private void initIsSearch() {
        isSearch = isSearch();

        if (!isSearch) {
            upCTB = getContractType();
            toolbar.setTitle(upCTB.getTypeName());
/*            lwSales.setText(Integer.toString(upCTB.getWk1Sqty()));
            lw2Sales.setText(Integer.toString(upCTB.getWk2Sqty()));
            lw3Sales.setText(Integer.toString(upCTB.getWk3Sqty()));
            expSales.setText(Integer.toString(upCTB.getExpQty()));
            sugSales.setText(Integer.toString(upCTB.getSugQty()));
            inv.setText(Integer.toString(upCTB.getInventory()));
            tonightCount2.setText(Integer.toString(upCTB.getTonightCount()));
            realCount.setText(Integer.toString(upCTB.getTodayCount()));
            minCount.setText(Integer.toString(upCTB.getMinQty()));
            maxCount.setText(Integer.toString(upCTB.getMaxQty()));*/
        } /*else {
            typeDesc.setVisibility(View.GONE);
        }*/
        //标题栏
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
    }

   /* //给toolbar创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }*/

    //toolbar图标点击监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                judgmentUpdate();
                break;
/*            case R.id.edit_contract:
                if (adapter == null || adapter.getCbs() == null || adapter.getCbs().size() == 0) {
                    break;
                }
                if (this.item == null) {
                    this.item = item;
                }
                if (item.getTitleCondensed().toString().equals(ContractActivity2.this.getResources().getString(R.string.cancel))) {
                    judgmentUpdate();
                } else {
                    adapter.setIsEdit(true);
                    item.setTitleCondensed(ContractActivity2.this.getResources().getString(R.string.cancel));
                    adapter.notifyDataSetChanged();
                    done.setVisibility(View.VISIBLE);
                    sort.setVisibility(View.GONE);
                    ispeople = false;
                }
                break;*/
            default:
                break;
        }
        return true;
    }


    @OnClick(R.id.done)
    void done() {
        showLoading();
        if (adapter == null) {
            MyToast.getShortToast(ContractActivity2.this, getResources().getString(R.string.noMsg));
            hideLoading();
            return;
        }
        for (ContractBean cb : adapter.getCbs()) {
            if (cb.isChange()) {
                presenter.updateAllContract();
                break;
            } else {
                MyToast.getShortToast(ContractActivity2.this, getResources().getString(R.string.isSave));
                hideLoading();
            }
        }
    }

    private boolean is_back = false;

    //判断用户是否有对订量修改，如果修改过要提示
    private void judgmentUpdate() {
        boolean mark = false;
        if (adapter != null) {
            for (ContractBean cb : adapter.getCbs()) {
                if (cb.isChange()) {
                    mark = true;
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("您修改的订量尚未确认，是否放弃修改？")
                            .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    is_back = true;
                                    presenter.updateAllContract();
                                }
                            })
                            .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    //                                updateDone();
                                }
                            })
                            .show();
                    break;
                }
            /* else {
                done.setVisibility(View.GONE);
                adapter.setIsEdit(false);
                item.setTitleCondensed(ContractActivity2.this.getResources().getString(R.string.edit));
                adapter.notifyDataSetChanged();
                sort.setVisibility(View.VISIBLE);
                ispeople = true;
            }*/
            }
        }

        if (!mark) {
            finish();
        }
    }

    //根据上个activity返回数据判断是否要刷新
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (null != data) {
                if (data.getBooleanExtra("refresh", false)) {
                    swipe.autoRefresh();
                }
            }
        }
    }

    //实体返回键
    @Override
    public void onBackPressed() {
        judgmentUpdate();
    }


    //得到是否是从搜索页过来的
    @Override
    public boolean isSearch() {
        i = getIntent();
        return i.getBooleanExtra("is_search", false);
    }

    //得到搜索页搜索的信息
    @Override
    public String getSearchMessage() {
        i = getIntent();
        return i.getStringExtra("search_message");
    }

    //得到上页面选取的类型
    @Override
    public ContractTypeBean getContractType() {
        i = getIntent();
        ContractTypeBean ctb = (ContractTypeBean) i.getSerializableExtra("ctb");
        if (total == 5201) {
            total = ctb.getTodayCount();
        }
        return ctb;
    }

    //点击一下view
    @Override
    public void toShortClick(View view, ContractBean cb) {
        Intent intent = new Intent(this, ContractDetailActivity.class);
        intent.putExtra("cb", cb);
        startActivityForResult(intent, 11);
    }

    //显示loading
    @Override
    public void showLoading() {
        swipe.setRefreshing(true);
    }

    //隐藏loading
    @Override
    public void hideLoading() {
        try {
            swipe.setRefreshing(false);
        } catch (Exception e) {
            finish();
        }
    }

    //把数据显示出来
    @Override
    public void showView(final ContractAdapter adapter) {
        try {
            this.adapter = adapter;
            //上拉加载监听
            recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                        adapter.changeMoreStatus(ContractAdapter.LOADING_MORE);
                        pullLoading(adapter);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                }
            });
            if (adapter.getCbs() == null || adapter.getCbs().size() == 0) {
                noMessage.setVisibility(View.VISIBLE);
            }
            recycler.setAdapter(adapter);
        } catch (Exception ignored) {
            finish();
        }
    }

    //土司错误信息
    @Override
    public void showFailedError(String errorMessage) {
        MyToast.getShortToast(ContractActivity2.this, errorMessage);
        hideLoading();
    }

    //长按加
    @Override
    public void touchAdd(ContractBean cb, int positon, MotionEvent event) {
        ContractAdapter.ViewHolder view = (ContractAdapter.ViewHolder) recycler.findViewHolderForAdapterPosition(positon);
        onTouchChange("add", event.getAction(), view, cb);
    }

    //长按减
    @Override
    public void touchLess(ContractBean cb, int positon, MotionEvent event) {
        ContractAdapter.ViewHolder view = (ContractAdapter.ViewHolder) recycler.findViewHolderForAdapterPosition(positon);
        onTouchChange("less", event.getAction(), view, cb);
    }

    //得到当前排序
    @Override
    public String getSort() {
        switch (sort.getSelectedItemPosition()) {
            case 0:
                return TODAY_SORT_DESC;
            case 1:
                return TODAY_SORT;
            case 2:
                return COMMODIFY_ID_SORT_DESC;
            case 3:
                return COMMODIFY_ID_SORT;
            case 4:
                return COMMODIFY_NAME_SORT_DESC;
            case 5:
                return COMMODIFY_NAME_SORT;
            case 6:
                return MONEY_SORT_DESC;
            case 7:
                return MONEY_SORT;
            default:
                return COMMODIFY_ID_SORT_DESC;
        }
    }

    //得到当前页数
    @Override
    public int getPage() {
        return nowPage;
    }

    //写入当前页数
    @Override
    public void setPage(int page) {
        nowPage = page;
    }

    //上拉加载
    @Override
    public void pullLoading(ContractAdapter adapter) {
        this.adapter = adapter;
        //获得数据执行
        if (isSearch) {
            presenter.searchAllContract(this.adapter);
        } else {
            presenter.pullGetAllContract(this.adapter);
        }
    }

    //显示无数据
    @Override
    public void showNoMessage() {
        noMessage.setVisibility(View.VISIBLE);
    }

    //得到要更新的数据
    @Override
    public List<ContractBean> getContractList() {
        List<ContractBean> crs = new ArrayList<>();
        for (ContractBean cb : adapter.getCbs()) {
            if (cb.isChange()) {
                crs.add(cb);
            }
        }
        return crs;
    }

    //更新数据完成
    @Override
    public void updateDone() {
        if (!isSearch){
            saveType();
        }
/*        Intent intent = new Intent(this, ContractActivity2.class);
        intent.putExtra("ctb", getContractType());
        intent.putExtra("is_search", false);
        startActivity(intent);
        finish();*/
        for (ContractBean cb :
                adapter.getCbs()) {
            if (cb.isChange()) {
                cb.setChange(false);
            }
        }
        if (is_back) {
            finish();
        } else {
            MyToast.getShortToast(ContractActivity2.this, "保存成功");
            swipe.autoRefresh();
        }
    }

    //butterknife得到布局
    @Override
    protected int getLayoutId() {
        return R.layout.activity_contract2;
    }

    boolean isOnLongClick;
    Thread mt;
    Thread pt;
    Handler hd;

    //监听触摸事件
    private void onTouchChange(String methodName, int eventAction, final ContractAdapter.ViewHolder view, final ContractBean cb) {
        hd = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if (view.less.isEnabled()) {
                            lessCount(view, cb);
                        }
                        break;
                    case 2:
                        if (view.add.isEnabled()) {
                            addCount(view, cb);
                        }
                        break;
                }
            }
        };

        if ("less".equals(methodName)) {

            if (eventAction == MotionEvent.ACTION_DOWN) {
                mt = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isOnLongClick) {
                            try {
                                Thread.sleep(200);
                                hd.sendEmptyMessage(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                isOnLongClick = true;
                runOrStopEdit();
                mt.start();
            } else if (eventAction == MotionEvent.ACTION_UP) {
                if (mt != null) {
                    isOnLongClick = false;
                    runOrStopEdit();
                }
            } else if (eventAction == MotionEvent.ACTION_MOVE) {
                if (mt != null && Integer.parseInt(view.editCdc.getText().toString()) != 0) {
                    isOnLongClick = true;
                    runOrStopEdit();
                }
            }

        } else if ("add".equals(methodName)) {
            if (eventAction == MotionEvent.ACTION_DOWN) {
                pt = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isOnLongClick) {
                            try {
                                Thread.sleep(200);
                                hd.sendEmptyMessage(2);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                isOnLongClick = true;
                runOrStopEdit();
                pt.start();
            } else if (eventAction == MotionEvent.ACTION_UP) {
                if (pt != null) {
                    isOnLongClick = false;
                    runOrStopEdit();
                }
            } else if (eventAction == MotionEvent.ACTION_MOVE) {
                //当前商品订量<(总类最大量-总类当前量)
                if (!isSearch) {
                    if (pt != null && Integer.parseInt(view.editCdc.getText().toString()) < (upCTB.getMaxQty() - upCTB.getTodayCount())) {
                        isOnLongClick = true;
                        runOrStopEdit();
                    }
                } else {
                    if (pt != null && cb.getTodayCount() < cb.getMaxQty() - cb.getTodayCount()) {
                        isOnLongClick = true;
                        runOrStopEdit();
                    }
                }
            }
        }
    }

    //增加按键
    private void addCount(ContractAdapter.ViewHolder view, ContractBean cb) {
        if (!isSearch) {
            if (Integer.parseInt(view.editCdc.getText().toString()) == upCTB.getMaxQty()) {
                MyToast.getShortToast(this,"已到当前类别最大订量，不能添加");
                return;
            }
        } else {
            if (Integer.parseInt(view.editCdc.getText().toString()) == cb.getMaxQty()) {
                MyToast.getShortToast(this,"已到当前商品最大订量，不能添加");
                return;
            }
        }
        int nowContractCount = Integer.parseInt(view.editCdc.getText().toString()) + cb.getStepQty();

        //不是搜索的还需要去改类的属性
        if (!isSearch) {
            int nowTypeAllCount = upCTB.getTodayCount() + cb.getStepQty();
            if (nowTypeAllCount > upCTB.getMaxQty()) {
                return;
            }
            upCTB.setTodayCount(upCTB.getTodayCount() + cb.getStepQty());
        }

        cb.setTodayStore(cb.getTodayStore() + cb.getStepQty());
        cb.setTodayCount(nowContractCount);
        cb.setChange(true);
        view.editCdc.setText(Integer.toString(nowContractCount));
        if (!view.less.isEnabled()) {
            view.less.setEnabled(true);
        }

        if (cb.getTodayStore() != 0) {
            view.card.setBackgroundColor(ContextCompat.getColor(this, R.color.xinqiaose));
        } else {
            view.card.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_color));
        }

        if (isSearch) {
            if (nowContractCount >= cb.getMaxQty()) {
                view.add.setEnabled(false);
                isOnLongClick = false;
                runOrStopEdit();
            }
        } else {
            if (nowContractCount >= upCTB.getMaxQty()) {
                view.add.setEnabled(false);
                isOnLongClick = false;
                runOrStopEdit();
            }
        }
    }

    //减去按键
    private void lessCount(ContractAdapter.ViewHolder view, ContractBean cb) {

        if (Integer.parseInt(view.editCdc.getText().toString()) == 0) {
            return;
        }
        int nowContractCount = Integer.parseInt(view.editCdc.getText().toString()) - cb.getStepQty();
        if (!isSearch) {
            int nowTypeAllCount = upCTB.getTodayCount() + cb.getStepQty();
            if (nowTypeAllCount < upCTB.getMinQty()) {
                MyToast.getShortToast(ContractActivity2.this, getResources().getString(R.string.errorMin));
                return;
            }
            upCTB.setTodayCount(upCTB.getTodayCount() - cb.getStepQty());
        }

        cb.setTodayStore(cb.getTodayStore() - cb.getStepQty());
        cb.setTodayCount(nowContractCount);
        cb.setChange(true);
        view.editCdc.setText(Integer.toString(nowContractCount));
        if (!view.add.isEnabled()) {
            view.add.setEnabled(true);
        }


        if (cb.getTodayStore() != 0) {
            view.card.setBackgroundColor(ContextCompat.getColor(this, R.color.xinqiaose));
        } else {
            view.card.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_color));
        }

        if (isSearch) {
            if (nowContractCount <= 0) {
                view.less.setEnabled(false);
                isOnLongClick = false;
                runOrStopEdit();
            }
        } else {
            if (nowContractCount <= upCTB.getMinQty()) {
                view.less.setEnabled(false);
                isOnLongClick = false;
                runOrStopEdit();
            }
        }
    }

    //根据是否是在运行中来静止滑动或开启滑动
    private void runOrStopEdit() {
        //长按中禁止所有滑动
        if (isOnLongClick) {
            layoutManager.setScrollEnabled(false);
        } else {//停止长按开启所有滑动
            layoutManager.setScrollEnabled(true);
        }
    }

    private void saveType() {
        SharedPreferences preferences = getSharedPreferences("ct", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("typeId", upCTB.getTypeId());
        editor.putInt("todayGh", upCTB.getTodayGh());
        editor.putInt("todayStore", upCTB.getTodayStore());
        editor.putInt("todayCount", upCTB.getTodayCount());
        editor.apply();
    }

}
