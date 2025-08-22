package com.custom.provision.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.custom.provision.utils.LogUtils;

/**
 * Author: created by huhuaijun on 2025/8/5 17:06
 * Function:
 */
public abstract class BaseFragment extends Fragment {
    public static final String TAG = BaseFragment.class.getSimpleName();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        LogUtils.d("onAttach:"+ this.getClass().getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("onCreate:"+ this.getClass().getSimpleName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getContentView(inflater, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListeners();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("onResume:"+ this.getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("onDestroy:"+ this.getClass().getSimpleName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.d("onDetach:"+ this.getClass().getSimpleName());
    }

    public abstract View getContentView(@NonNull LayoutInflater inflater, @Nullable  ViewGroup container);

    public abstract void initView();

    public abstract void initListeners();

    public abstract void initData();

    public abstract void languageChange();
}
