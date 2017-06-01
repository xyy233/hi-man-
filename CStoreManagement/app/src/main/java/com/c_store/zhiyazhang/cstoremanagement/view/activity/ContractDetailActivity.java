package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.presenter.contract.ContractDetailPresenter;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;
import com.c_store.zhiyazhang.cstoremanagement.view.interfaceview.ContractDetailView;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by zhiya.zhang
 * on 2017/5/11 16:23.
 */

public class ContractDetailActivity extends MyActivity implements ContractDetailView {

    @BindView(R.id.my_toolbar)
    Toolbar myToolbar;
    @BindView(R.id.cimg)
    ImageView cimg;
    @BindView(R.id.cid)
    TextView cid;
    @BindView(R.id.cname)
    TextView cname;
    @BindView(R.id.cprice)
    TextView cprice;
    @BindView(R.id.cin)
    TextView cin;
    @BindView(R.id.cnc)
    TextView cnc;
    @BindView(R.id.min_count)
    TextView minCount;
    @BindView(R.id.max_count)
    TextView maxCount;
    @BindView(R.id.each_add_count)
    TextView eachAddCount;
    @BindView(R.id.less_count)
    ImageButton lessCount;
    @BindView(R.id.order_count)
    TextView orderCount;
    @BindView(R.id.add_count)
    ImageButton addCount;
    @BindView(R.id.sure_btn)
    Button sureBtn;
    @BindView(R.id.loading)
    LinearLayout loading;
    Context mContext = this;
    ContractBean cb;
    int stepQty;
    ContractDetailPresenter cdp = new ContractDetailPresenter(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initClick();
    }

    private void initClick() {
        lessCount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchChange("less", event.getAction());
                return true;
            }
        });
        addCount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchChange("add", event.getAction());
                return true;
            }
        });
    }

    private void initView() {
        cb = getContractBean();
        stepQty = cb.getStepQty();
        //标题栏
        myToolbar.setNavigationIcon(R.drawable.back);
        myToolbar.setTitle(cb.getcName());
        setSupportActionBar(myToolbar);
        cid.setText(cb.getcId());
        cname.setText(cb.getcName());
        cprice.setText(Double.toString(cb.getcPrice()));
        cin.setText(Integer.toString(cb.getInventory()));
        cnc.setText(Integer.toString(cb.getTonightCount()));
        orderCount.setText(Integer.toString(cb.getTodayCount()));
        minCount.setText(Integer.toString(cb.getMinQty()));
        maxCount.setText(Integer.toString(cb.getMaxQty()));
        eachAddCount.setText(Integer.toString(cb.getStepQty()));
        orderCount.setText(Integer.toString(cb.getTodayCount()));
        Glide.with(this).load(cb.getImg_url()).placeholder(R.color.yingshu).error(R.color.cstore_red).crossFade().centerCrop().into(cimg);
        if (Integer.parseInt(orderCount.getText().toString()) <= cb.getMinQty()) {
            lessCount.setEnabled(false);
        }
        if (Integer.parseInt(orderCount.getText().toString()) >= cb.getMaxQty()) {
            addCount.setEnabled(false);
        }
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


    void lessCount() {
        int nowCount = Integer.parseInt(orderCount.getText().toString()) - stepQty;
        if (nowCount < cb.getMinQty()) {
            Toast.makeText(mContext, getResources().getString(R.string.errorMin), Toast.LENGTH_SHORT).show();
        } else {
            orderCount.setText(Integer.toString(nowCount));
            addCount.setEnabled(true);
            cb.setTodayStore(cb.getTodayStore() + nowCount);
        }
        if (Integer.parseInt(orderCount.getText().toString()) <= cb.getMinQty()) {
            lessCount.setEnabled(false);
            isOnLongClick = false;
        }
    }

    void addCount() {
        int nowCount = Integer.parseInt(orderCount.getText().toString()) + stepQty;
        if (nowCount > cb.getMaxQty()) {
            Toast.makeText(mContext, getResources().getString(R.string.errorMax), Toast.LENGTH_SHORT).show();
        } else {
            orderCount.setText(Integer.toString(nowCount));
            lessCount.setEnabled(true);
        }
        if (Integer.parseInt(orderCount.getText().toString()) >= cb.getMaxQty()) {
            addCount.setEnabled(false);
            isOnLongClick = false;
        }
    }

    @OnClick(R.id.sure_btn)
    void sureBtn() {
        cdp.updateCB();
    }

    @Override
    public void onBackPressed() {
        if (cb.getTodayCount() != Integer.parseInt(orderCount.getText().toString())) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("您修改的订量尚未确认，是否放弃修改？")
                    .setPositiveButton("保存退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cdp.updateCB();
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contract_detail;
    }


    @Override
    public ContractBean getContractBean() {
        Intent i = getIntent();
        return (ContractBean) i.getSerializableExtra("cb");
    }

    @Override
    public ContractBean getNowContractBean() {
        ContractBean c = new ContractBean();
        c.setcId(cb.getcId());
        c.setcName(cb.getcName());
        c.setInventory(cb.getInventory());
        c.setTonightCount(cb.getTonightCount());
        c.setTodayGh(cb.getTodayGh());
        c.setTodayStore(Integer.parseInt(orderCount.getText().toString()));
        c.setTodayCount(Integer.parseInt(orderCount.getText().toString()));
        c.setStepQty(cb.getStepQty());
        c.setMinQty(cb.getMinQty());
        c.setMaxQty(cb.getMaxQty());
        c.setcPrice(cb.getcPrice());
        c.setImg_url(cb.getImg_url());
        c.setAction(cb.getAction());
        return c;
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }

    @Override
    public void toActivity() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("refresh", true);
        ContractDetailActivity.this.setResult(RESULT_OK, resultIntent);
        ContractDetailActivity.this.finish();
    }


    @Override
    public void showFailedError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }


    boolean isOnLongClick;
    MiusThread mt;
    PlusThread pt;

    private void onTouchChange(String methodName, int eventAction) {
        if ("less".equals(methodName)) {
            if (eventAction == MotionEvent.ACTION_DOWN) {
                mt = new MiusThread();
                isOnLongClick = true;
                mt.start();
            } else if (eventAction == MotionEvent.ACTION_UP) {
                if (mt != null) {
                    isOnLongClick = false;
                }
            } else if (eventAction == MotionEvent.ACTION_MOVE) {
                if (mt != null && Integer.parseInt(orderCount.getText().toString()) != cb.getMinQty()) {
                    isOnLongClick = true;
                }

            }
        } else if ("add".equals(methodName)) {
            if (eventAction == MotionEvent.ACTION_DOWN) {
                pt = new PlusThread();
                isOnLongClick = true;
                pt.start();
            } else if (eventAction == MotionEvent.ACTION_UP) {
                if (pt != null) {
                    isOnLongClick = false;
                }
            } else if (eventAction == MotionEvent.ACTION_MOVE) {
                if (pt != null && Integer.parseInt(orderCount.getText().toString()) != cb.getMaxQty()) {
                    isOnLongClick = true;
                }
            }
        }
    }

    //减操作
    private class MiusThread extends Thread {
        @Override
        public void run() {
            while (isOnLongClick) {
                try {
                    Thread.sleep(200);
                    myHandler.sendEmptyMessage(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }
    }

    //加操作
    private class PlusThread extends Thread {
        @Override
        public void run() {
            while (isOnLongClick) {
                try {
                    Thread.sleep(200);
                    myHandler.sendEmptyMessage(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }
    }

    //更新文本框的值
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (lessCount.isEnabled()) {
                        lessCount();
                    }
                    break;
                case 2:
                    if (addCount.isEnabled()) {
                        addCount();
                    }
                    break;
            }
        }
    };
}
