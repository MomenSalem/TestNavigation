package com.example.testnavigation.ui.completed_task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.testnavigation.databinding.FragmentCompletedBinding;

public class CompletedTaskFragment extends Fragment {

    private FragmentCompletedBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CompletedTaskViewModel completedTaskViewModel =
                new ViewModelProvider(this).get(CompletedTaskViewModel.class);

        binding = FragmentCompletedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textCompleted;
        completedTaskViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}