package com.example.testnavigation.ui.new_task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.testnavigation.databinding.FragmentNewTaskBinding;

public class NewTaskFragment extends Fragment {

    private FragmentNewTaskBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewTaskViewModel newTaskViewModel =
                new ViewModelProvider(this).get(NewTaskViewModel.class);

        binding = FragmentNewTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNewTask;
        newTaskViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}