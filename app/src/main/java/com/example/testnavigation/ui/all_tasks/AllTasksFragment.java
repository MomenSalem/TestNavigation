package com.example.testnavigation.ui.all_tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.testnavigation.databinding.FragmentAllTasksBinding;

public class AllTasksFragment extends Fragment {

    private FragmentAllTasksBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AllTasksViewModel allTasksViewModel =
                new ViewModelProvider(this).get(AllTasksViewModel.class);

        binding = FragmentAllTasksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAllTasks;
        allTasksViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}