package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.ContactsEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/12/18.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    private Context mContext;
    private List<ContactsEntity> contactsEntities;

    public ContactsAdapter(Context mContext, List<ContactsEntity> contactsEntities) {
        this.mContext = mContext;
        this.contactsEntities = contactsEntities;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contacts, parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        ContactsEntity contactsEntity = contactsEntities.get(position);
        holder.tvName.setText(contactsEntity.getName());
        int selectionFromPosition = getSelectionFromPosition(position);
        int absolutePositionFromSelection = getAbsolutePositionFromSelection(selectionFromPosition);
        if (absolutePositionFromSelection == position) {
            //如果首字母位置就是该行位置，则显示首字母
            holder.tvFirstLetter.setText(contactsEntity.getFirstLetter());
            holder.tvFirstLetter.setVisibility(View.VISIBLE);
        } else {
            holder.tvFirstLetter.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return contactsEntities.size();
    }

    //通过位置获得首字母
    public int getSelectionFromPosition(int position) {
        return contactsEntities.get(position).getFirstLetter().charAt(0);
    }

    //通过首字母获得该首字母列表的位置
    public int getAbsolutePositionFromSelection(int selection) {
        for (int i = 0; i < contactsEntities.size(); i++) {
            if (contactsEntities.get(i).getFirstLetter().charAt(0) == selection) {
                return i;
            }
        }
        return -1;
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_first_letter)
        TextView tvFirstLetter;
        @BindView(R.id.tv_name)
        TextView tvName;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
