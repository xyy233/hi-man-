package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ScrapContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.presenter.scrap.SelectScrapAdapter;
import com.c_store.zhiyazhang.cstoremanagement.sql.MySql;
import com.c_store.zhiyazhang.cstoremanagement.sql.ScrapDao;
import com.c_store.zhiyazhang.cstoremanagement.utils.ConnectionDetector;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyApplication;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyLinearlayoutManager;
import com.c_store.zhiyazhang.cstoremanagement.utils.Util;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;
import com.c_store.zhiyazhang.cstoremanagement.utils.socket.SocketUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.c_store.zhiyazhang.cstoremanagement.R.string.scrap;
import static com.c_store.zhiyazhang.cstoremanagement.utils.static_variable.MyVariable.REQUEST_CODE_QRCODE_PERMISSIONS;

/**
 * Created by zhiya.zhang
 * on 2017/5/24 9:23.
 */

public class ScrapSelectActivity extends MyActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.scrap_select_toolbar)
    Toolbar myToolbar;
    @BindView(R.id.select_recycler)
    RecyclerView selectRecycler;
    ArrayList<ScrapContractBean> adapterList;
    ScrapDao sd = new ScrapDao(this);
    ArrayList<ScrapContractBean> deleteList = new ArrayList<>();
    @BindView(R.id.loading)
    LinearLayout loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        myToolbar.setTitle(getResources().getString(scrap));
        //myToolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        selectRecycler.setLayoutManager(new MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //得到要显示的数据
        adapterList = sd.getAllDate();
        if (adapterList.size() != 0) {
            //进行删除检查
            int today = Util.getTodayDay();
            for (ScrapContractBean scb :
                    adapterList) {
                if (scb.getCreateDay() != today) {
                    deleteList.add(scb);
                }
            }
            //进行删除操作
            if (deleteList.size() != 0) {
                adapterList.removeAll(deleteList);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sd.editSQL(deleteList, "delete");
                        deleteList.clear();
                    }
                }).start();
            }
        }
        SelectScrapAdapter adapter = new SelectScrapAdapter(adapterList);
        selectRecycler.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SelectScrapAdapter adapter = new SelectScrapAdapter(sd.getAllDate());
        selectRecycler.setAdapter(adapter);
    }

    @OnClick(R.id.haveBarCodeBtn)
    void haveBarCodeBtn() {
        if (judgmentCarmer()) {
            startActivity(new Intent(this, BarCodeScrapActivity.class));
        }
    }

    @OnClick(R.id.submit_scrap)
    void submitScrap() {
        final ArrayList<ScrapContractBean> submitList = new ArrayList<ScrapContractBean>();
        for (ScrapContractBean scb :
                sd.getAllDate()) {
            if (scb.getIsScrap() == 0) {
                submitList.add(scb);
                scb.setIsScrap(1);
            }
        }
        if (submitList.size() != 0) {
            if (!ConnectionDetector.getConnectionDetector().isOnline()){
                Toast.makeText(ScrapSelectActivity.this,MyApplication.getContext().getResources().getString(R.string.noInternet),Toast.LENGTH_SHORT).show();
                return;
            }
            String ip = MyApplication.getIP();
            if (ip.equals(MyApplication.getContext().getResources().getString(R.string.notFindIP))) {
                Toast.makeText(ScrapSelectActivity.this,MyApplication.getContext().getResources().getString(R.string.notFindIP),Toast.LENGTH_SHORT).show();
                return;
            }
            showLoading();
            SocketUtil.getSocketUtil(ip).inquire(MySql.getScrapSql(UserBean.getUser(), sd.getAllDate()), new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 0:
                            if (msg.obj.toString().equals("Syntax Error!")) {
                                Toast.makeText(ScrapSelectActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                                hideLoading();
                                break;
                            }
                            sd.editSQL(submitList, "update");
                            SelectScrapAdapter adapter = new SelectScrapAdapter(sd.getAllDate());
                            selectRecycler.setAdapter(adapter);
                            hideLoading();
                            break;
                        case 1:
                            Toast.makeText(ScrapSelectActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(ScrapSelectActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

        } else {
            Toast.makeText(ScrapSelectActivity.this, "无要报废的商品", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.noBarCodeBtn)
    void noBarCodeBtn() {
        startActivity(new Intent(this, NoCodeScrapActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scrap_select;
    }

    private void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loading.setVisibility(View.GONE);
    }

    /**
     * 相机权限
     *
     * @return 确定是否权限开启
     */
    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private boolean judgmentCarmer() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //成功
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        startActivity(new Intent(ScrapSelectActivity.this, BarCodeScrapActivity.class));
    }

    //失败
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
