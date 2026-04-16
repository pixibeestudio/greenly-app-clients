package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    public interface OnAddressActionListener {
        void onAddressClick(Address address);
        void onEditClick(Address address);
        void onDeleteClick(Address address);
    }

    private List<Address> addressList = new ArrayList<>();
    private OnAddressActionListener listener;

    public AddressAdapter(OnAddressActionListener listener) {
        this.listener = listener;
    }

    public void setAddressList(List<Address> list) {
        this.addressList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.bind(address);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNamePhone;
        private final TextView tvFull;
        private final TextView tvEdit;
        private final TextView tvDelete;
        private final TextView tvLabelTag;
        private final TextView tvDefaultTag;

        AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamePhone = itemView.findViewById(R.id.tv_address_name_phone);
            tvFull = itemView.findViewById(R.id.tv_address_full);
            tvEdit = itemView.findViewById(R.id.tv_edit_address);
            tvDelete = itemView.findViewById(R.id.tv_delete_address);
            tvLabelTag = itemView.findViewById(R.id.tv_label_tag);
            tvDefaultTag = itemView.findViewById(R.id.tv_default_tag);
        }

        void bind(Address address) {
            tvNamePhone.setText(address.getReceiverName() + "  " + address.getPhone());
            tvFull.setText(address.getFullAddress());

            // Hiển thị nhãn loại địa chỉ
            tvLabelTag.setText(address.getLabelDisplay().toUpperCase());

            // Hiển thị tag mặc định
            tvDefaultTag.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);

            // Click card → chọn địa chỉ
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onAddressClick(address);
            });

            // Click Sửa
            tvEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(address);
            });

            // Click Xóa
            tvDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(address);
            });
        }
    }
}
