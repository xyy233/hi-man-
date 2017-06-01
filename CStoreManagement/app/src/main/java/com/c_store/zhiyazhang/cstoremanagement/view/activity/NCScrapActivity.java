package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ScrapContractBean;
import com.c_store.zhiyazhang.cstoremanagement.presenter.scrap.BarCodeScrapAdapter;
import com.c_store.zhiyazhang.cstoremanagement.sql.MySql;
import com.c_store.zhiyazhang.cstoremanagement.sql.ScrapDao;
import com.c_store.zhiyazhang.cstoremanagement.utils.ConnectionDetector;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyApplication;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyLinearlayoutManager;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;
import com.c_store.zhiyazhang.cstoremanagement.utils.onclick.RecyclerAddLessClickListener;
import com.c_store.zhiyazhang.cstoremanagement.utils.recycler.ItemTouchHelperCallback;
import com.c_store.zhiyazhang.cstoremanagement.utils.socket.SocketUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhiya.zhang
 * on 2017/5/27 15:54.
 * 选完类后的具体商品
 * 不复杂所以也不用mvc了
 */

public class NCScrapActivity extends MyActivity {
    @BindView(R.id.my_toolbar)
    Toolbar myToolbar;
    @BindView(R.id.recyclerView2)
    RecyclerView recycler;
    BarCodeScrapAdapter adapter;
    Context mContext;
    @BindView(R.id.sure_btn)
    Button sureBtn;
    ScrapDao sd = new ScrapDao(this);
    @BindView(R.id.loading)
    LinearLayout loading;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sureBtn.setVisibility(View.VISIBLE);
        initView();
        initDate();
    }

    private void initDate() {
        if (!ConnectionDetector.getConnectionDetector().isOnline()){
            Toast.makeText(NCScrapActivity.this,MyApplication.getContext().getResources().getString(R.string.noInternet),Toast.LENGTH_SHORT).show();
            return;
        }
        String ip = MyApplication.getIP();
        if (ip.equals(MyApplication.getContext().getResources().getString(R.string.notFindIP))) {
            Toast.makeText(NCScrapActivity.this,MyApplication.getContext().getResources().getString(R.string.notFindIP),Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        showLoading();
        SocketUtil.getSocketUtil(ip).inquire(MySql.getAllScrapByCategory(Integer.toString(getIntent().getIntExtra("categorynumber", 0))), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (((String) msg.obj).equals("") || ((String) msg.obj).equals("[]")) {
                            Toast.makeText(mContext, "无category", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        ArrayList<ScrapContractBean> scbs = new Gson().fromJson((String) msg.obj, new TypeToken<List<ScrapContractBean>>() {
                        }.getType());
                        //检查已添加数据中是否有相同id的数据，有的话已添加数据覆盖当前数据
                        for (ScrapContractBean scb :
                                sd.getAllDate()) {
                            for (ScrapContractBean s :
                                    scbs) {
                                if (scb.getScrapId().equals(s.getScrapId())) {
                                    s.setNowMrkCount(scb.getNowMrkCount());
                                    s.setIsScrap(scb.getIsScrap());
                                    s.setIsNew(scb.getIsNew());
                                }
                            }
                        }
                        //得到adapter关联ItemTouchHelper和RecyclerView
                        adapter = new BarCodeScrapAdapter(scbs, 1);
                        adapter.setOnItemClickLitener(onItemClickListener);
                        recycler.setAdapter(adapter);
                        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
                        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
                        mItemTouchHelper.attachToRecyclerView(recycler);
                        hideLoading();
                        break;
                    case 1:
                        Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        hideLoading();
                        break;
                    case 2:
                        Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        hideLoading();
                        break;
                }
            }
        });
    }

    private void initView() {
        mContext = NCScrapActivity.this;
        myToolbar.setTitle(getIntent().getStringExtra("categoryname"));
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        recycler.setLayoutManager(new MyLinearlayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    /**
     * recycler的监听
     */
    RecyclerAddLessClickListener onItemClickListener = new RecyclerAddLessClickListener() {
        @Override
        public void addItemOnClick(TextView tv, int position) {
            adapter.getList().get(position).setNowMrkCount(adapter.getList().get(position).getNowMrkCount() + 1);
            tv.setText(Integer.toString(adapter.getList().get(position).getNowMrkCount()));
        }

        @Override
        public void lessItemOnClick(TextView tv, int position) {
            if (adapter.getList().get(position).getNowMrkCount() != 0) {
                adapter.getList().get(position).setNowMrkCount(adapter.getList().get(position).getNowMrkCount() - 1);
                tv.setText(Integer.toString(adapter.getList().get(position).getNowMrkCount()));
            }
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 528:
                    hideLoading();
                    finish();
                    break;
            }
        }
    };

    Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                ArrayList<ScrapContractBean> insertList = new ArrayList<ScrapContractBean>();
                ArrayList<ScrapContractBean> updateList = new ArrayList<ScrapContractBean>();

                for (ScrapContractBean scb :
                        adapter.getList()) {
                    //要修改过的，要不是已提交过的
                    if (scb.getNowMrkCount() > 0 && scb.getIsScrap() == 0) {
                        //如果是新的就创建，如果是老的就去修改
                        if (scb.getIsNew() != 2) {
                            insertList.add(scb);
                        } else {
                            updateList.add(scb);
                        }
                    }
                }
                if (insertList.size() != 0 || updateList.size() != 0) {
                    if (insertList.size() != 0) {
                        sd.editSQL(insertList, "insert");
                    }
                    if (updateList.size() != 0) {
                        sd.editSQL(updateList, "update");
                    }
                    Message msg = new Message();
                    msg.what = 528;
                    mHandler.sendMessage(msg);
                } else {
                    Toast.makeText(mContext, "无要保存的数据", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("NCScrapActivity", "", e);
            } finally {
                Looper.loop();
            }

        }
    });

    @OnClick(R.id.sure_btn)
    void sureBtn() {
        showLoading();
        mThread.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scrap_nobarcode;
    }

    private void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loading.setVisibility(View.GONE);
    }
}
