package com.custom.provision.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.custom.provision.Operation;
import com.custom.provision.WelComeActivity;
import com.custom.provision.databinding.WelcomeFragmentBinding;

public class WelcomeFragment extends BaseFragment{
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
    }

    @Override
    public void initData() {

    }

    public static WelcomeFragment newInstance() {
        
        Bundle args = new Bundle();
        
        WelcomeFragment fragment = new WelcomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
