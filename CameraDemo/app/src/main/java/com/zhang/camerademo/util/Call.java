/*
 *
 * Call.java
 * 
 * Created by Wuwang on 2016/12/6
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.zhang.camerademo.util;

/**
 * Description:
 */
public interface Call<T> {

    void onCall(int code, T t);

}
