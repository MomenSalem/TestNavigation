package com.example.testnavigation.ui.search_task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.testnavigation.databinding.FragmentSearchTaskBinding;

public class SearchTaskFragment extends Fragment {

    private FragmentSearchTaskBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchTaskViewModel newTaskViewModel =
                new ViewModelProvider(this).get(SearchTaskViewModel.class);

        binding = FragmentSearchTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSearch;
        newTaskViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}