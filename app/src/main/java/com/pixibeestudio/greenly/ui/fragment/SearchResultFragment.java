package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SearchHistoryManager;
import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.ui.adapter.ProductGridAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment implements ProductGridAdapter.OnProductAddCartListener {

    private ImageButton btnBack;
    private EditText edtSearch;
    private RecyclerView rvSearchResults;
    private String searchQuery = "";

    private SearchViewModel viewModel;
    private ProductGridAdapter adapter;
    private SearchHistoryManager searchHistoryManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khoi tao ViewModel va SearchHistoryManager
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        searchHistoryManager = new SearchHistoryManager(requireContext());

        // Anh xa views
        btnBack = view.findViewById(R.id.btnBack);
        edtSearch = view.findViewById(R.id.edtSearch);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);

        // Cau hinh RecyclerView Grid 2 cot
        rvSearchResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ProductGridAdapter(new ArrayList<>(), this);
        rvSearchResults.setAdapter(adapter);

        // Lay argument tu Bundle va hien len EditText
        if (getArguments() != null) {
            searchQuery = getArguments().getString("searchQuery", "");
            edtSearch.setText(searchQuery);
        }

        // Nut Back -> lui ve SearchFragment
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });

        // Bat su kien tim kiem lai tu EditText
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = edtSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchQuery = query;
                    searchHistoryManager.addSearchQuery(query);
                    performSearch(query);

                    // An ban phim
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                    }
                }
                return true;
            }
            return false;
        });

        // Goi API tim kiem voi tu khoa ban dau
        if (!searchQuery.isEmpty()) {
            performSearch(searchQuery);
        }
    }

    /**
     * Goi API tim kiem va observe ket qua
     */
    private void performSearch(String keyword) {
        viewModel.searchProducts(keyword);
        viewModel.getSearchResultsLiveData().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                // Co ket qua -> cap nhat adapter
                adapter = new ProductGridAdapter(products, this);
                rvSearchResults.setAdapter(adapter);
            } else {
                // Khong co ket qua
                adapter = new ProductGridAdapter(new ArrayList<>(), this);
                rvSearchResults.setAdapter(adapter);
                Toast.makeText(getContext(), "Kh\u00f4ng t\u00ecm th\u1ea5y s\u1ea3n ph\u1ea9m n\u00e0o", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAddCartClick(Product product) {
        // Xu ly them vao gio hang (se bo sung logic sau)
        Toast.makeText(getContext(), "\u0110\u00e3 th\u00eam v\u00e0o gi\u1ecf h\u00e0ng", Toast.LENGTH_SHORT).show();
    }
}
