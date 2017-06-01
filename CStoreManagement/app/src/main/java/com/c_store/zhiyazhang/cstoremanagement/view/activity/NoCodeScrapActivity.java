package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.CategoryBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.presenter.scrap.NoCodeAdapter;
import com.c_store.zhiyazhang.cstoremanagement.sql.MySql;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyApplication;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyLinearlayoutManager;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;
import com.c_store.zhiyazhang.cstoremanagement.utils.onclick.RecyclerOnItemClickListener;
import com.c_store.zhiyazhang.cstoremanagement.utils.socket.SocketUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by zhiya.zhang
 * on 2017/5/24 9:32.
 * <p>
 * 因为比较单一，因此不用mvp，直接全部写到activity中
 */

public class NoCodeScrapActivity extends MyActivity {

    @BindView(R.id.my_toolbar)
    Toolbar myToolbar;
    @BindView(R.id.recyclerView2)
    RecyclerView recyc;
    @BindView(R.id.sure_btn)
    Button sureBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sureBtn.setVisibility(View.GONE);
        initView();
        initData();
    }

    private void initData() {
        String ip = MyApplication.getIP();
        if (ip.equals(MyApplication.getContext().getResources().getString(R.string.notFindIP))) {
            return;
        }
        //得到线程内网络请求数据进行处理
        SocketUtil.getSocketUtil(ip).inquire(MySql.getScrapCategory(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (((String) msg.obj).equals("") || ((String) msg.obj).equals("[]")) {
                            Toast.makeText(NoCodeScrapActivity.this, "无category", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        final ArrayList<CategoryBean> cbs = new Gson().fromJson((String) msg.obj, new TypeToken<List<CategoryBean>>() {
                        }.getType());
                        final NoCodeAdapter adapter = new NoCodeAdapter(cbs, new RecyclerOnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int positon) {
                                Intent i = new Intent(NoCodeScrapActivity.this, NCScrapActivity.class);
                                i.putExtra("categorynumber", cbs.get(positon).getCategorynumber());
                                i.putExtra("categoryname", cbs.get(positon).getCategoryname());
                                startActivity(i);
                            }

                            @Override
                            public void onItemLongClick(View view, int positon) {

                            }

                            @Override
                            public void onTouchAddListener(ContractBean cb, int positon, MotionEvent event) {

                            }

                            @Override
                            public void onTouchLessListener(ContractBean cb, int positon, MotionEvent event) {

                            }
                        });
                        recyc.setAdapter(adapter);
                        break;
                    case 1:
                        Toast.makeText(NoCodeScrapActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(NoCodeScrapActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void initView() {
        myToolbar.setTitle(getResources().getString(R.string.noBarCodeScrap));
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        recyc.setLayoutManager(new MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scrap_nobarcode;
    }

}
