package com.tony4men.tony4meninventory.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tony4men.tony4meninventory.Contents;
import com.tony4men.tony4meninventory.ProductActivity;
import com.tony4men.tony4meninventory.ProductListActivity;
import com.tony4men.tony4meninventory.R;
import com.tony4men.tony4meninventory.model.BillEntity;

import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.billViewHolder> {

    private List<BillEntity> billlEntities;
    private Context context;
    private SparseBooleanArray itemStateArray = new SparseBooleanArray();
    public  CallbackSelectAllItem callbackSelectAllItem;
    public BillAdapter(List<BillEntity> billlEntities) {
        this.billlEntities = billlEntities;

    }

    public SparseBooleanArray getItemStateArray() {
        return itemStateArray;
    }

    public void setItemStateArray(SparseBooleanArray itemStateArray) {
        this.itemStateArray = itemStateArray;
    }

    @NonNull
    @Override
    public billViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View productView = inflater.inflate(R.layout.item_bill_layout, parent,false);
        billViewHolder viewHolder = new billViewHolder(productView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull billViewHolder holder, int position) {
        final BillEntity billEntity = billlEntities.get(position);
        TextView productCodeTextView = holder.productCodeTextView;
        productCodeTextView.setText("" + billEntity.getBillId());
        TextView productNameTextView = holder.productNameTextView;
        productNameTextView.setText(billEntity.getBillName());
        TextView originAmountTextView = holder.originAmountTextView;
        originAmountTextView.setText("" + billEntity.getProductTotal());
         TextView realAmountTextView = holder.realAmountTextView;
        realAmountTextView.setText("" + billEntity.getDescription());
         TextView priceTextView = holder.priceTextView;
        priceTextView.setText("" +  billEntity.getUpdateDate());
         TextView updateDateTextView = holder.updateDateTextView;
        updateDateTextView.setText(billEntity.getUpdateDate().toString());
        holder.bind(position);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if( isLongClick) {
                    if(!itemStateArray.get(position,false)){
                        view.setBackgroundColor(Color.CYAN);
                        itemStateArray.put(position, true);
                        callbackSelectAllItem.selectAllOrDesSelected(false, position, true);
                    } else {

                    }

//                    Toast.makeText(context, "Long Click: " + billlEntities.get(position).getProductCode(), Toast.LENGTH_SHORT).show();
                } else{
                    if(!isSelectedItem()) {
                        Intent productListIntent = new Intent(context, ProductListActivity.class);
                        productListIntent.putExtra("position", position);
                        productListIntent.putExtra(Contents.INTENT_TO_PRODUCT_ACTIVITY_STRING, Contents.INTENT_FROM_BILL_LIST_ACTIVITY);
                        productListIntent.putExtra("billid",billEntity.getBillId());
                        productListIntent.putExtra("title",billEntity.getBillName());
                        ((Activity)context).startActivityForResult(productListIntent, Contents.INTENT_FROM_BILL_LIST_ACTIVITY);
                    } else {
                        if(!itemStateArray.get(position,false)){
                            view.setBackgroundColor(Color.CYAN);
                            itemStateArray.put(position, true);
                            callbackSelectAllItem.selectAllOrDesSelected(false,position, true);
                        } else {
                            view.setBackgroundColor(Color.WHITE);
                            itemStateArray.put(position, false);

                                callbackSelectAllItem.selectAllOrDesSelected(!isSelectedItem(), position, false);

                        }
                    }



                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return billlEntities.size();
    }
    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }
    public interface CallbackSelectAllItem {
        void selectAllOrDesSelected(boolean isSelectAll, int position, boolean isSelected);
    }
    public boolean isSelectedItem(){
        int size = billlEntities.size();
        for(int i = 0; i < size; i++){
            if(itemStateArray.get(i)){
                return true;
            }
        }
        return false;
    }
    public class billViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private View itemView;
        public TextView productCodeTextView;
        public TextView productNameTextView;
        public TextView originAmountTextView;
        public TextView realAmountTextView;
        public TextView priceTextView;
        public TextView updateDateTextView;
        private ItemClickListener itemClickListener;

        public billViewHolder(@NonNull View item_View) {
            super(item_View);
            itemView = item_View;
            productCodeTextView = (TextView)item_View.findViewById(R.id.maHangValue);
            productNameTextView = (TextView)item_View.findViewById(R.id.tenHangValue);
            originAmountTextView = (TextView)item_View.findViewById(R.id.soLuongGocValue);
            realAmountTextView = (TextView)item_View.findViewById(R.id.soLuongThucValue);
            priceTextView = (TextView)item_View.findViewById(R.id.giaValue);
            updateDateTextView = (TextView)item_View.findViewById(R.id.updateDateValue);
            item_View.setOnClickListener(this);
            item_View.setOnLongClickListener(this);
        }
        void bind(int position){
            itemView.setBackgroundColor(itemStateArray.get(position)? Color.CYAN : Color.WHITE);
        }
        @Override
        public void onClick(View v) {

                itemClickListener.onClick(v, getAdapterPosition(), false);

        }
        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),true);
            return true;
        }
    }

}
