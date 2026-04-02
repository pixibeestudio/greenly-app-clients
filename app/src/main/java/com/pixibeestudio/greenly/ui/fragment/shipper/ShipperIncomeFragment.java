package com.pixibeestudio.greenly.ui.fragment.shipper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.WalletProfileResponse;
import com.pixibeestudio.greenly.ui.adapter.TransactionHistoryAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.ShipperDashboardViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ShipperIncomeFragment extends Fragment {

    private TextView tvTotalIncome;
    private TextView tvCodBalance;
    private Button btnSubmitCod;
    private BarChart barChartIncome;
    private RecyclerView rvIncomeHistory;

    private ShipperDashboardViewModel viewModel;
    private TransactionHistoryAdapter historyAdapter;
    private DecimalFormat currencyFormatter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shipper_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        tvCodBalance = view.findViewById(R.id.tvCodBalance);
        btnSubmitCod = view.findViewById(R.id.btnSubmitCod);
        barChartIncome = view.findViewById(R.id.barChartIncome);
        rvIncomeHistory = view.findViewById(R.id.rvIncomeHistory);

        currencyFormatter = new DecimalFormat("#,###");

        // Setup RecyclerView
        rvIncomeHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new TransactionHistoryAdapter();
        rvIncomeHistory.setAdapter(historyAdapter);

        // Khởi tạo BarChart (MPAndroidChart)
        setupBarChart();

        // Khởi tạo ViewModel (dùng chung với Activity nếu có)
        viewModel = new ViewModelProvider(requireActivity()).get(ShipperDashboardViewModel.class);

        // Lắng nghe dữ liệu
        viewModel.getWalletProfileLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    // Có thể show progress bar
                    break;
                case SUCCESS:
                    if (resource.data != null) {
                        updateUI(resource.data);
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // Nút nộp tiền COD
        btnSubmitCod.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng nộp COD đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // Gọi API lần đầu
        viewModel.fetchWalletProfile();
    }

    private void updateUI(WalletProfileResponse data) {
        if (data == null) return;

        tvTotalIncome.setText(currencyFormatter.format(data.getTotalIncome()) + "đ");
        tvCodBalance.setText(currencyFormatter.format(data.getCodBalance()) + "đ");

        // Cập nhật list lịch sử
        if (data.getHistory() != null) {
            historyAdapter.setHistoryList(data.getHistory());
        }

        // Cập nhật biểu đồ
        if (data.getChartData() != null && !data.getChartData().isEmpty()) {
            updateChartData(data.getChartData());
        } else {
            barChartIncome.clear();
        }
    }

    private void setupBarChart() {
        barChartIncome.getDescription().setEnabled(false);
        barChartIncome.getLegend().setEnabled(false);
        
        // Trục X (Hoành)
        XAxis xAxis = barChartIncome.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        
        // Trục Y (Tung - Trái & Phải)
        barChartIncome.getAxisLeft().setDrawGridLines(false);
        barChartIncome.getAxisRight().setEnabled(false);
        
        // Zoom & Touch
        barChartIncome.setTouchEnabled(false);
        barChartIncome.setDragEnabled(false);
        barChartIncome.setScaleEnabled(false);
    }

    private void updateChartData(List<WalletProfileResponse.ChartData> chartDataList) {
        if (chartDataList == null || chartDataList.isEmpty()) {
            barChartIncome.clear();
            return;
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < chartDataList.size(); i++) {
            WalletProfileResponse.ChartData item = chartDataList.get(i);
            entries.add(new BarEntry(i, (float) item.getIncome()));
            labels.add(item.getDate()); // Vd: "T2", "T3", "T4"
        }

        BarDataSet dataSet = new BarDataSet(entries, "Thu nhập");
        dataSet.setColor(getResources().getColor(R.color.green_primary)); // Màu xanh lá Greenly
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "";
                if (value >= 1000000) {
                    return currencyFormatter.format(value / 1000000) + "M"; // Vd 1.5M
                } else if (value >= 1000) {
                    return currencyFormatter.format(value / 1000) + "k"; // Vd 50k
                }
                return currencyFormatter.format(value);
            }
        });

        // Set nhãn trục X
        barChartIncome.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);
        
        barChartIncome.setData(barData);
        barChartIncome.invalidate(); // Refresh biểu đồ
    }
}
