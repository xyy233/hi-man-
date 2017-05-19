package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractTypeBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.presenter.contract.ContractTypeAdapter;
import com.c_store.zhiyazhang.cstoremanagement.presenter.contract.ContractTypePresenter;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;
import com.c_store.zhiyazhang.cstoremanagement.view.interfaceview.ContractTypeView;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.c_store.zhiyazhang.cstoremanagement.utils.static_variable.MyVariable.REQUEST_CODE_QRCODE_PERMISSIONS;

/**
 * Created by zhiya.zhang
 * on 2017/5/9 15:57.
 */

public class ContractTypeActivity extends MyActivity implements ContractTypeView, EasyPermissions.PermissionCallbacks {

    Context mContext = this;

    @BindView(R.id.my_toolbar)
    Toolbar toolbar;
    @BindView(R.id.contract_loding)
    LinearLayout contractLoding;
    @BindView(R.id.type_list)
    RecyclerView typeList;
    @BindView(R.id.retry)
    Button retry;
    ContractTypeBean ctb;
    ContractTypeAdapter adapter;

    ContractTypePresenter presenter = new ContractTypePresenter(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        showLoading();
        //标题栏
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle(getResources().getString(R.string.contract_buy));
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        //recycler列表
        typeList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                retry.setVisibility(View.GONE);
                presenter.getAllContractType();
            }
        });
        presenter.getAllContractType();
    }

    //给toolbar创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contract, menu);
        return true;
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

    //toolbar监听
    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_search:
                    if (judgmentCarmer()) {
                        startActivity(new Intent(mContext, SearchContractActivity.class));
                    }
                default:
                    break;
            }
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStart() {
        getPreviousType();
        if (ctb != null && !ctb.getTypeId().equals("")) {
            if (adapter != null) {
                for (ContractTypeBean ct : adapter.ctbs) {
                    if (Objects.equals(ct.getTypeId(), ctb.getTypeId())) {
                        ct.setTodayStore(ctb.getTodayStore());
                        ct.setTodayCount(ctb.getTodayCount());
                        ct.setTodayGh(ctb.getTodayGh());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
        super.onStart();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contract_type;
    }

    @Override
    public UserBean getUser() {
        UserBean user = new UserBean();
        SharedPreferences preferences = getSharedPreferences("idpwd", Context.MODE_PRIVATE);
        user.setUid(preferences.getString("id", ""));
        return user;
    }

    View colorView;

    @Override
    public void toShortClick(View view, ContractTypeBean ctb) {
        if (colorView != null) {
            colorView.setBackgroundColor(Color.WHITE);
        }
        for (ContractTypeBean ct:adapter.ctbs){
            if (!Objects.equals(ct.getTypeId(), ctb.getTypeId())){
                ct.setChangeColor(false);
            }else {
                ct.setChangeColor(true);
                ctb.setChangeColor(true);
            }
        }
        colorView = view;
        Intent i = new Intent(this, ContractActivity2.class);
        i.putExtra("ctb", ctb);
        i.putExtra("is_search", false);
        startActivity(i);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void toLongClick(View view) {

    }

    @Override
    public void hideLoading() {
        contractLoding.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        contractLoding.setVisibility(View.VISIBLE);
    }

    @Override
    public void showView(ContractTypeAdapter adapter) {
        typeList.setAdapter(adapter);
        this.adapter = adapter;
    }

    @Override
    public void showFailedError(String errorMessage) {
        Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
        hideLoading();
        retry.setVisibility(View.VISIBLE);
    }

    //相机权限
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
        startActivity(new Intent(mContext, SearchContractActivity.class));
    }

    //失败
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void getPreviousType() {
        SharedPreferences preferences = getSharedPreferences("ct", Context.MODE_PRIVATE);
        ctb = new ContractTypeBean();
        ctb.setTypeId(preferences.getString("typeId", ""));
        if (!ctb.getTypeId().equals("")) {
            ctb.setTodayGh(preferences.getInt("todayGh", 0));
            ctb.setTodayStore(preferences.getInt("todayStore", 0));
            ctb.setTodayCount(preferences.getInt("todayCount", 0));
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
