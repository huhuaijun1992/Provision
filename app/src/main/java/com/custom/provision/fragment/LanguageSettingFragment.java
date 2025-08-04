package com.custom.provision.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.custom.provision.databinding.LanguageSettingFragmentBinding;
import com.custom.provision.utils.VerticalSpaceItemDecoration;


public class LanguageSettingFragment extends Fragment implements View.OnClickListener{
    LanguageSettingFragmentBinding binding;
    LanguageAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         binding =  LanguageSettingFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        initListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void init(){
     adapter = new LanguageAdapter(getContext());
     LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
     linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
     binding.rvLanguage.setLayoutManager(linearLayoutManager);
     binding.rvLanguage.addItemDecoration(new VerticalSpaceItemDecoration(40));
     binding.rvLanguage.setAdapter(adapter);

    }

    private void initListener(){
        binding.bottomView.tvBack.setOnClickListener(this::onClick);
        binding.bottomView.tvNext.setOnClickListener(this::onClick);
    }

    public static LanguageSettingFragment newInstance() {
        
        Bundle args = new Bundle();
        
        LanguageSettingFragment fragment = new LanguageSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {

    }
}
