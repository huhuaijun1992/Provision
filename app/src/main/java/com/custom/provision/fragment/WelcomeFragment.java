package com.custom.provision.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.custom.provision.Operation;
import com.custom.provision.R;
import com.custom.provision.WelComeActivity;
import com.custom.provision.databinding.WelcomeFragmentBinding;
import com.custom.provision.utils.LogUtils;

public class WelcomeFragment extends BaseFragment{
    private static final int[] CORNER_ORDER = {0, 1, 2, 3}; // 左上, 右上, 右下, 左下
    private int currentStep = 0;
    private long lastTouchTime = 0;
    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;
    private int[] screenSize = new int[2];
    WelcomeFragmentBinding binding;

    @Override
    public View getContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding =WelcomeFragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initListeners() {
        binding.welcomeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((WelComeActivity)getActivity()).showFragment(Operation.languge);
            }
        });

        binding.rootView.post(() -> {
            screenSize[0] =binding.rootView.getWidth();
            screenSize[1] = binding.rootView.getHeight();
        });

        // 设置触摸监听
        binding.rootView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(event.getX(), event.getY());
                return true;
            }
            return false;
        });


    }



    @Override
    public void initData() {

    }

    private void handleTouch(float x, float y) {
        long currentTime = System.currentTimeMillis();

        // 检查是否超时（超过3秒）
        if (currentStep > 0 && (currentTime - lastTouchTime) > 3000) {
            resetSequence("点击间隔超过3秒，已重置");
            return;
        }

        lastTouchTime = currentTime;

        // 取消之前的超时检查
        if (timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }

        // 设置新的超时检查
        timeoutRunnable = this::checkTimeout;
        timeoutHandler.postDelayed(timeoutRunnable, 3000);

        // 确定触摸的是哪个角落
        int corner = detectCorner(x, y);
        if (corner == -1) {
            Log.d(TAG, "handleTouch: 不是角落区域");
            return;
        }

        // 检查触摸顺序是否正确
        if (corner == CORNER_ORDER[currentStep]) {
            currentStep++;

            // 完成一个完整序列
            if (currentStep == CORNER_ORDER.length) {
                onSequenceCompleted();
                currentStep = 0; // 重置计数
            }
        } else {
            resetSequence("点击顺序错误，已重置");
        }
    }

    private int detectCorner(float x, float y) {
        int cornerSize = 300; // 角落区域大小（像素）

        // 左上角 (0)
        if (x <= cornerSize && y <= cornerSize) {
            return 0;
        }
        // 右上角 (1)
        else if (x >= screenSize[0] - cornerSize && y <= cornerSize) {
            return 1;
        }
        // 右下角 (2)
        else if (x >= screenSize[0] - cornerSize && y >= screenSize[1] - cornerSize) {
            return 2;
        }
        // 左下角 (3)
        else if (x <= cornerSize && y >= screenSize[1] - cornerSize) {
            return 3;
        }

        return -1; // 不是角落区域
    }

    private void checkTimeout() {
        if (currentStep > 0) {
            resetSequence("点击超时，已重置");
        }
    }

    private void resetSequence(String message) {
        currentStep = 0;
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void onSequenceCompleted() {
//        Toast.makeText(getContext(), "顺序点击成功！", Toast.LENGTH_SHORT).show();
        // 这里可以添加成功后的逻辑
        try {
            Intent intent = new Intent();
            intent.setClassName("com.weibu.factorytest","com.weibu.factorytest.FactoryTest");
            startActivity(intent);
            LogUtils.d( "onSequenceCompleted: 顺序点击成功");
        }catch (Exception e){
            // 处理应用未安装或Activity不存在的情况
            Toast.makeText(getContext(), "无法打开应用", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 移除所有回调，防止内存泄漏
        timeoutHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void languageChange() {
        binding.tvWelcome.setText(getString(R.string.welcome));
    }

    public static WelcomeFragment newInstance() {
        
        Bundle args = new Bundle();
        
        WelcomeFragment fragment = new WelcomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
