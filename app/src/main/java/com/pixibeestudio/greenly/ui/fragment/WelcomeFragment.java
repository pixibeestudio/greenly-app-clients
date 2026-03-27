package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;

public class WelcomeFragment extends Fragment {

    private MaterialButton btnWelcomeLogin;
    private MaterialButton btnWelcomeGuest;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        initViews(view);
        setupListeners();
    }

    private void initViews(View view) {
        btnWelcomeLogin = view.findViewById(R.id.btnWelcomeLogin);
        btnWelcomeGuest = view.findViewById(R.id.btnWelcomeGuest);
    }

    private void setupListeners() {
        btnWelcomeLogin.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_welcomeFragment_to_loginFragment);
        });

        btnWelcomeGuest.setOnClickListener(v -> {
            sessionManager.setGuestMode(true);
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_welcomeFragment_to_homeFragment);
        });
    }
}
