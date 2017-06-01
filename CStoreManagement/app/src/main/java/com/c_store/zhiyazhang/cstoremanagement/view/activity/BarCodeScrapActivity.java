package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ScrapContractBean;
import com.c_store.zhiyazhang.cstoremanagement.presenter.scrap.BarCodeScrapAdapter;
import com.c_store.zhiyazhang.cstoremanagement.presenter.scrap.BarCodeScrapPresenter;
import com.c_store.zhiyazhang.cstoremanagement.sql.ScrapDao;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyLinearlayoutManager;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;
import com.c_store.zhiyazhang.cstoremanagement.utils.onclick.RecyclerAddLessClickListener;
import com.c_store.zhiyazhang.cstoremanagement.utils.recycler.ItemTouchHelperCallback;
import com.c_store.zhiyazhang.cstoremanagement.view.interfaceview.BarCodeScrapView;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhiya.zhang
 * on 2017/5/24 9:31.
 */

public class BarCodeScrapActivity extends MyActivity implements BarCodeScrapView {

    @BindView(R.id.scrap_container)
    FrameLayout flMyContainer; //扫码界面
    @BindView(R.id.scanBtn)
    ToggleButton scanBtn; //开始扫描的开关
    @BindView(R.id.sure_btn)
    Button sureBtn; //确认提交开关
    @BindView(R.id.loading)
    LinearLayout loading; //loading界面
    @BindView(R.id.scrap_recycler)
    RecyclerView scrapRecycler; //recycler

    CaptureFragment captureFragment;//识别器

    BarCodeScrapPresenter presenter = new BarCodeScrapPresenter(this);

    BarCodeScrapAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scrap_barcode;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        sureBtn.setVisibility(View.VISIBLE);
        captureFragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera2);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.scrap_container, captureFragment).commit();
        scanBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    refreshCamera();
                    scanBtn.setEnabled(false);
                    scanBtn.setBackground(ContextCompat.getDrawable(BarCodeScrapActivity.this, R.drawable.round_scan));
                } else {
                    scanBtn.setBackground(ContextCompat.getDrawable(BarCodeScrapActivity.this, R.drawable.round_noscan));
                }
            }
        });
        scrapRecycler.setLayoutManager(new MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //得到已有数据
        ScrapDao sd = new ScrapDao(this);
        ArrayList<ScrapContractBean> adapterList = sd.getAllDate();
        adapter = new BarCodeScrapAdapter(adapterList, 0);
        adapter.setOnItemClickLitener(onItemClickListener);
        scrapRecycler.setAdapter(adapter);
        //关联ItemTouchHelper和RecyclerView
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(scrapRecycler);
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            presenter.getScrap(result);
            scanBtn.setChecked(false);
            scanBtn.setEnabled(true);
        }

        @Override
        public void onAnalyzeFailed() {
            Toast.makeText(BarCodeScrapActivity.this, "扫描出错，请重新扫描", Toast.LENGTH_SHORT).show();
            refreshCamera();
        }
    };

    /**
     * 刷新相机重新启动识别
     */
    private void refreshCamera() {
        Handler handler = captureFragment.getHandler();
        Message msg = Message.obtain();
        msg.what = R.id.restart_preview;
        handler.sendMessageDelayed(msg, 500);
    }

    @OnClick(R.id.sure_btn)
    void sureBtn() {
        if (adapter == null || adapter.getList().size() == 0) {
            Toast.makeText(BarCodeScrapActivity.this, "无要保存的数据", Toast.LENGTH_SHORT).show();
            return;
        }
        //得到要去保存的数据
        final ArrayList<ScrapContractBean> insertList = new ArrayList<ScrapContractBean>();
        final ArrayList<ScrapContractBean> updateList = new ArrayList<ScrapContractBean>();
        for (ScrapContractBean scb :
                adapter.getList()) {
            if (scb.getIsScrap() == 0) {
                if (scb.getIsNew() == 1 || scb.getIsNew() == 0) {
                    insertList.add(scb);
                } else {
                    updateList.add(scb);
                }
            }
        }
        //去保存
        if (insertList.size() != 0 || updateList.size() != 0) {
            showLoading();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ScrapDao sd = new ScrapDao(BarCodeScrapActivity.this);
                    if (insertList.size() != 0) {
                        sd.editSQL(insertList, "insert");
                    }
                    if (updateList.size() != 0) {
                        sd.editSQL(updateList, "update");
                    }
                    finish();
                }
            }).start();
        } else {
            Toast.makeText(BarCodeScrapActivity.this, "无要保存的数据", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (adapter == null || adapter.getList().size() == 0) {
            finish();
        } else {
            //得到要去保存的数据
            final ArrayList<ScrapContractBean> insertList = new ArrayList<ScrapContractBean>();
            final ArrayList<ScrapContractBean> updateList = new ArrayList<ScrapContractBean>();
            for (ScrapContractBean scb :
                    adapter.getList()) {
                //未报废过的
                if (scb.getIsScrap() == 0) {
                    if (scb.getIsNew() == 1 || scb.getIsNew() == 0) {
                        insertList.add(scb);
                    } else {
                        updateList.add(scb);
                    }
                }
            }

            //如果有要保存的数据就跳提示，否则直接退出
            if (insertList.size() != 0 || updateList.size() != 0) {
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("您添加的报废尚未保存，是否放弃？")
                        .setPositiveButton("保存退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showLoading();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ScrapDao sd = new ScrapDao(BarCodeScrapActivity.this);
                                        if (insertList.size() != 0) {
                                            sd.editSQL(insertList, "insert");
                                        }
                                        if (updateList.size() != 0) {
                                            sd.editSQL(updateList, "update");
                                        }
                                        finish();
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            } else {
                finish();
            }
        }
    }

    @Override
    public void showView(ScrapContractBean scb) {
        for (ScrapContractBean s : adapter.getList()) {
            if (scb.getScrapId().equals(s.getScrapId())) {
                if (s.getIsScrap() == 1) {
                    Toast.makeText(BarCodeScrapActivity.this, "不能添加已提交的商品！", Toast.LENGTH_SHORT).show();
                    return;
                }
                s.setNowMrkCount(s.getNowMrkCount() + 1);
                adapter.notifyDataSetChanged();
                return;
            }
        }
        adapter.addItem(scb);
        adapter.notifyDataSetChanged();
    }

    RecyclerAddLessClickListener onItemClickListener = new RecyclerAddLessClickListener() {
        @Override
        public void addItemOnClick(TextView tv, int position) {
            adapter.getList().get(position).setNowMrkCount(adapter.getList().get(position).getNowMrkCount() + 1);
            tv.setText(Integer.toString(adapter.getList().get(position).getNowMrkCount()));
        }

        @Override
        public void lessItemOnClick(TextView tv, int position) {
            if (adapter.getList().get(position).getNowMrkCount() == 1) {
                Toast.makeText(BarCodeScrapActivity.this, "报废数必须大于0,如要删除请左滑或右滑", Toast.LENGTH_SHORT).show();
            } else {
                adapter.getList().get(position).setNowMrkCount(adapter.getList().get(position).getNowMrkCount() - 1);
                tv.setText(Integer.toString(adapter.getList().get(position).getNowMrkCount()));
            }
        }
    };

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }

    @Override
    public void showFailedError(String errorMessage) {
        Toast.makeText(BarCodeScrapActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }


}
