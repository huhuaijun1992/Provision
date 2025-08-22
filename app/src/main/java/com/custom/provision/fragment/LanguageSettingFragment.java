package com.custom.provision.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.custom.provision.Operation;
import com.custom.provision.R;
import com.custom.provision.WelComeActivity;
import com.custom.provision.adapter.LanguageAdapter;
import com.custom.provision.databinding.LanguageSettingFragmentBinding;
import com.custom.provision.utils.VerticalSpaceItemDecoration;


public class LanguageSettingFragment extends BaseFragment implements View.OnClickListener {
    LanguageSettingFragmentBinding binding;
    LanguageAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LanguageSettingFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public View getContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = LanguageSettingFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void initView() {
        adapter = new LanguageAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvLanguage.setLayoutManager(linearLayoutManager);
        binding.rvLanguage.addItemDecoration(new VerticalSpaceItemDecoration(40));
        binding.rvLanguage.setAdapter(adapter);

    }

    @Override
    public void initListeners() {
        binding.bottomView.tvBack.setOnClickListener(this::onClick);
        binding.bottomView.tvNext.setOnClickListener(this::onClick);

    }

    @Override
    public void initData() {

    }

    @Override
    public void languageChange() {
        binding.tvTitle.setText(getString(R.string.language_setting));
        binding.bottomView.tvBack.setText(getString(R.string.bottom_back));
        binding.bottomView.tvSkip.setText(getString(R.string.bottom_skip));
        binding.bottomView.tvNext.setText(getString(R.string.bottom_next));
        adapter.notifyDataSetChanged();
    }


    public static LanguageSettingFragment newInstance() {

        Bundle args = new Bundle();

        LanguageSettingFragment fragment = new LanguageSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        WelComeActivity activity;
        activity = (WelComeActivity) getActivity();
        int id = v.getId();
        if (id == R.id.tv_next) {
            activity.showFragment(Operation.region);
        } else if (id == R.id.tv_back) {
            activity.showFragment(Operation.welcome);
        }

    }
}
