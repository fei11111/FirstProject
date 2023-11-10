package com.fei.firstproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.AddressEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/9/6.
 */

public class MyAddressAdapter extends RecyclerView.Adapter<MyAddressAdapter.MyAddressViewHolder> {

    private Context mContext;
    private List<AddressEntity> addressEntities;
    private OnItemContentClickLstener onItemContentClickLstener;
    private int checkPosisition = -1;

    public void setOnItemContentClickLstener(OnItemContentClickLstener onItemContentClickLstener) {
        this.onItemContentClickLstener = onItemContentClickLstener;
    }

    public MyAddressAdapter(Context mContext, List<AddressEntity> addressEntities) {
        this.mContext = mContext;
        this.addressEntities = addressEntities;
    }

    public void setAddressEntities(List<AddressEntity> addressEntities) {
        this.addressEntities = addressEntities;
    }

    public List<AddressEntity> getAddressEntities() {
        return addressEntities;
    }

    @Override
    public MyAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_address, parent, false);
        return new MyAddressViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyAddressViewHolder holder,@SuppressLint("RecyclerView") final int position) {
        final AddressEntity addressEntity = addressEntities.get(position);
        holder.tvAddress.setText(addressEntity.getReceiptAddr());
        holder.tvContrat.setText(addressEntity.getReceiptUserName());
        holder.tvPhone.setText(addressEntity.getReceiptTel());
        if (addressEntity.getDefaultFlagId().equals("Y")) {
            holder.tvDefaultAddress.setChecked(true);
            checkPosisition = position;
        } else {
            holder.tvDefaultAddress.setChecked(false);
        }
        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemContentClickLstener != null) {
                    onItemContentClickLstener.onEditAddress(addressEntity);
                }
            }
        });

        holder.tvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemContentClickLstener != null) {
                    onItemContentClickLstener.onDelAddress(addressEntity);
                }
            }
        });

        holder.tvDefaultAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemContentClickLstener != null) {
                    onItemContentClickLstener.onDefaultAddress(addressEntity.getReceiptAddrId(), new OnCallBack() {
                        @Override
                        public void onCallBack() {
                            addressEntities.get(position).setDefaultFlagId("Y");
                            if (checkPosisition != -1) {
                                addressEntities.get(checkPosisition).setDefaultFlagId("N");
                            }
                            checkPosisition = position;
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressEntities.size();
    }

    class MyAddressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_address)
        TextView tvAddress;
        @BindView(R.id.tv_contrat)
        TextView tvContrat;
        @BindView(R.id.tv_phone)
        TextView tvPhone;
        @BindView(R.id.tv_default_address)
        AppCompatCheckedTextView tvDefaultAddress;
        @BindView(R.id.tv_edit)
        TextView tvEdit;
        @BindView(R.id.tv_del)
        TextView tvDel;

        public MyAddressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemContentClickLstener {
        void onEditAddress(AddressEntity addressEntity);

        void onDefaultAddress(String receiptAddrId, OnCallBack onCallBack);

        void onDelAddress(AddressEntity addressEntity);
    }

    public interface OnCallBack {
        void onCallBack();
    }
}
