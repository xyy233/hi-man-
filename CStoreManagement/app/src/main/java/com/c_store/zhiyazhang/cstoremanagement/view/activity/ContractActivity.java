package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractTypeBean;
import com.c_store.zhiyazhang.cstoremanagement.presenter.contract.ContractAdapter;
import com.c_store.zhiyazhang.cstoremanagement.presenter.contract.ContractPresenter;
import com.c_store.zhiyazhang.cstoremanagement.utils.MySwipeRefresh;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;
import com.c_store.zhiyazhang.cstoremanagement.view.interfaceview.ContractView;

import java.util.List;

import butterknife.BindView;

/**
 * Created by zhiya.zhang
 * on 2017/5/10 11:19.
 */

public class ContractActivity extends MyActivity implements ContractView {
    @BindView(R.id.my_toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipe_recycler)
    RecyclerView recycler;
    @BindView(R.id.my_swipe)
    MySwipeRefresh swipe;
    Intent i;
    ContractPresenter presenter = new ContractPresenter(this, this);
    @BindView(R.id.type_name)
    TextView typeName;
    @BindView(R.id.type_in)
    TextView typeIn;
    @BindView(R.id.type_nc)
    TextView typeNc;
    @BindView(R.id.type_dc)
    TextView typeDc;
    @BindView(R.id.noMessage)
    TextView noMessage;
    ContractTypeBean upCTB;
    @BindView(R.id.tSort)
    CheckBox tSort;
    @BindView(R.id.cIdSort)
    CheckBox cIdSort;
    @BindView(R.id.cNameSort)
    CheckBox cNameSort;
    @BindView(R.id.cMoneySort)
    CheckBox cMoneySort;
    @BindView(R.id.type_desc)
    LinearLayout typeDesc;
    //实际订量排序
    private static final String TODAY_SORT = "act_ord_qty__desc";
    //品号排序
    private static final String COMMODIFY_ID_SORT = "item_id";
    //品名排序
    private static final String COMMODIFY_NAME_SORT = "item_name";
    //价格排序
    private static final String MONEY_SORT = "price__desc";
    private boolean isSearch;
    private int nowPage;
    private int lastVisibleItem;
    ContractAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initClick();
    }

    private void initView() {
        isSearch = isSearch();
        if (!isSearch) {
            upCTB = getContractType();
            toolbar.setTitle(upCTB.getTypeName());
            typeName.setText(upCTB.getTypeName());
            typeIn.setText(Integer.toString(upCTB.getInventory()));
            typeNc.setText(Integer.toString(upCTB.getTonightCount()));
            typeDc.setText(Integer.toString(upCTB.getTodayCount()));
        } else {
            typeDesc.setVisibility(View.GONE);
        }
        //标题栏
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        swipe.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.cstore_white));
        swipe.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipe.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.cstore_red),
                ContextCompat.getColor(this, R.color.yellow),
                ContextCompat.getColor(this, R.color.blue),
                ContextCompat.getColor(this, R.color.cstore_green));
        layoutManager = new LinearLayoutManager(ContractActivity.this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        cIdSort.setChecked(true);
    }
    private void initClick() {
        tSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cIdSort.setChecked(false);
                    cNameSort.setChecked(false);
                    cMoneySort.setChecked(false);
                }
                isSearchOrGetAll();
            }
        });
        cIdSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tSort.setChecked(false);
                    cNameSort.setChecked(false);
                    cMoneySort.setChecked(false);
                }
                isSearchOrGetAll();
            }
        });
        cNameSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cIdSort.setChecked(false);
                    tSort.setChecked(false);
                    cMoneySort.setChecked(false);
                }
                isSearchOrGetAll();
            }
        });
        cMoneySort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cIdSort.setChecked(false);
                    tSort.setChecked(false);
                    cNameSort.setChecked(false);
                }
                isSearchOrGetAll();
            }
        });
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isSearchOrGetAll();
            }
        });
        swipe.autoRefresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void isSearchOrGetAll() {
        if (isSearch) {
            presenter.searchAllContract();
        } else {
            presenter.getAllContract();
        }
    }


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

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contract;
    }


    @Override
    public boolean isSearch() {
        i = getIntent();
        return i.getBooleanExtra("is_search", false);
    }

    @Override
    public String getSearchMessage() {
        i = getIntent();
        return i.getStringExtra("search_message");
    }

    @Override
    public ContractTypeBean getContractType() {
        i = getIntent();
        return (ContractTypeBean) i.getSerializableExtra("ctb");
    }

    @Override
    public void toShortClick(View view, ContractBean cb) {
        Intent intent = new Intent(this, ContractDetailActivity.class);
        intent.putExtra("cb", cb);
        startActivityForResult(intent, 11);
    }

    @Override
    public void showLoading() {
        swipe.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        try {
            swipe.setRefreshing(false);
        } catch (Exception e) {
            finish();
        }
    }

    @Override
    public void showView(final ContractAdapter adapter) {
        try {
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
            recycler.setAdapter(adapter);
        } catch (Exception ignored) {
            finish();
        }
    }

    @Override
    public void showFailedError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        hideLoading();
    }

    @Override
    public void touchAdd(ContractBean cb, int positon, MotionEvent event) {

    }

    @Override
    public void touchLess(ContractBean cb, int positon, MotionEvent event) {

    }


    @Override
    public String getSort() {
        if (tSort.isChecked())
            return TODAY_SORT;
        else if (cIdSort.isChecked())
            return COMMODIFY_ID_SORT;
        else if (cNameSort.isChecked())
            return COMMODIFY_NAME_SORT;
        else if (cMoneySort.isChecked())
            return MONEY_SORT;
        else
            return COMMODIFY_ID_SORT;
    }

    @Override
    public int getPage() {
        return nowPage;
    }

    @Override
    public void setPage(int page) {
        nowPage = page;
    }

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

    @Override
    public void showNoMessage() {
        noMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public List<ContractBean> getContractList() {
        return null;
    }


    @Override
    public void updateDone() {

    }
}
