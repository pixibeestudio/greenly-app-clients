package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.pixibeestudio.greenly.R;

public class SearchResultFragment extends Fragment {

    private ImageButton btnBack;
    private EditText edtSearch;
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = view.findViewById(R.id.btnBack);
        edtSearch = view.findViewById(R.id.edtSearch);

        // Lay argument tu Bundle
        if (getArguments() != null) {
            searchQuery = getArguments().getString("searchQuery", "");
            edtSearch.setText(searchQuery);
        }

        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack(); // Lui ve SearchFragment
        });

        // Xy ly onClick cac tab Filter...
    }
}
