package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.presenter.signin.SignInPresenter;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;
import com.c_store.zhiyazhang.cstoremanagement.view.interfaceview.SignInView;

import butterknife.BindView;
import butterknife.OnClick;

public class SignInActivity extends MyActivity implements SignInView {
    @BindView(R.id.uid)
    EditText uid;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.save_id)
    CheckBox saveId;
    @BindView(R.id.save_pwd)
    CheckBox savePwd;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.login)
    Button login;
    SharedPreferences preferences;
    Context mContext = SignInActivity.this;
    private SignInPresenter mSigninPresenter=new SignInPresenter(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        preferences=getSharedPreferences("idpwd",Context.MODE_PRIVATE);
        uid.setText(preferences.getString("id",""));
        password.setText(preferences.getString("pwd",""));
        if(!uid.getText().toString().equals("")){
            saveId.setChecked(true);
            if(!password.getText().toString().equals("")){
                savePwd.setChecked(true);
            }
        }
        if(!saveId.isChecked()){
            savePwd.setEnabled(false);
        }
        saveId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    savePwd.setEnabled(true);
                }else {
                    savePwd.setChecked(false);
                    savePwd.setEnabled(false);
                }
            }
        });
    }

    @OnClick(R.id.login)
    void login(){
        if(getUID().equals("")||getPassword().equals("")){
            Toast.makeText(mContext,getResources().getString(R.string.edit_idpwd),Toast.LENGTH_SHORT).show();
        }else {
            mSigninPresenter.login();
            //将输入法隐藏
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_signin;
    }

    @Override
    public String getUID() {
        return uid.getText().toString();
    }

    @Override
    public String getPassword() {
        return password.getText().toString();
    }

    @Override
    public void showLoading() {
        progress.setVisibility(View.VISIBLE);
        login.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        progress.setVisibility(View.GONE);
        login.setEnabled(true);
    }

    @Override
    public void toActivity(UserBean user) {
        Toast.makeText(mContext, user.getuName() + "您好,登陆成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, HomeTableActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    @Override
    public void showFailedError(String errorMessage) {
        Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void saveUser() {
        SharedPreferences.Editor editor=preferences.edit();
        if (saveId.isChecked()) {

            editor.putString("id", uid.getText().toString());
            if (savePwd.isChecked()) {
                editor.putString("pwd", password.getText().toString());
            } else {
                editor.remove("pwd");
            }
        } else {
            editor.remove("id");
            editor.remove("pwd");
        }
        editor.apply();
    }
}
