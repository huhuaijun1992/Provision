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

public class WelcomeFragment extends Fragment{
    WelcomeFragmentBinding binding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding =WelcomeFragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.welcomeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((WelComeActivity)getActivity()).showFragment(Operation.languge);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static WelcomeFragment newInstance() {
        
        Bundle args = new Bundle();
        
        WelcomeFragment fragment = new WelcomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
