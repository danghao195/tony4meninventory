package com.tony4men.tony4meninventory.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tony4men.tony4meninventory.ProductActivity;
import com.tony4men.tony4meninventory.R;
import com.tony4men.tony4meninventory.model.ProductEntity;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<ProductEntity> billlEntities;
    private Context context;
    int selectedPosition = 0;
    boolean isShowContextMenu = false;
    public ProductAdapter(List<ProductEntity> billlEntities) {
        this.billlEntities = billlEntities;

    }

    public boolean isShowContextMenu() {
        return isShowContextMenu;
    }

    public void setShowContextMenu(boolean showContextMenu) {
        isShowContextMenu = showContextMenu;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View productView = inflater.inflate(R.layout.item_product_layout, parent,false);
        ProductViewHolder viewHolder = new ProductViewHolder(productView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        final ProductEntity productEntity = billlEntities.get(position);
        TextView productCodeTextView = holder.productCodeTextView;
        productCodeTextView.setText(productEntity.getProductCode());
        TextView productNameTextView = holder.productNameTextView;
        productNameTextView.setText(productEntity.getProductName());
        TextView originAmountTextView = holder.originAmountTextView;
        originAmountTextView.setText("" + productEntity.getOriginAmount());
         TextView realAmountTextView = holder.realAmountTextView;
        realAmountTextView.setText("" + productEntity.getRealAmount());
         TextView priceTextView = holder.priceTextView;
        priceTextView.setText("" +  productEntity.getPrice());
         TextView updateDateTextView = holder.updateDateTextView;
        updateDateTextView.setText(productEntity.getUpdateDate().toString());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if( isLongClick) {
                    Toast.makeText(context, "Long Click: " + billlEntities.get(position).getProductCode(), Toast.LENGTH_SHORT).show();
                    isShowContextMenu = true;
                    view.showContextMenu();
                } else{
                    Toast.makeText(context, "Shot Click: "+billlEntities.get(position).getProductCode(), Toast.LENGTH_SHORT).show();
//                    Intent editProductIntent = new Intent(context, ProductActivity.class);
//                    editProductIntent.putExtra("position", position);
//                    editProductIntent.putExtra("data",productEntity);
//                    ((Activity)context).startActivityForResult(editProductIntent,2);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return billlEntities.size();
    }
    public interface ItemClickListener {
        void onClick(View view, int position,boolean isLongClick);
    }
    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private View itemView;
        public TextView productCodeTextView;
        public TextView productNameTextView;
        public TextView originAmountTextView;
        public TextView realAmountTextView;
        public TextView priceTextView;
        public TextView updateDateTextView;
        private ItemClickListener itemClickListener;

        public ProductViewHolder(@NonNull View item_View) {
            super(item_View);
            itemView = item_View;
            productCodeTextView = (TextView)item_View.findViewById(R.id.maHangValue);
            productNameTextView = (TextView)item_View.findViewById(R.id.tenHangValue);
            productNameTextView.setVisibility(View.GONE);
            originAmountTextView = (TextView)item_View.findViewById(R.id.soLuongGocValue);
            originAmountTextView.setVisibility(View.GONE);
            realAmountTextView = (TextView)item_View.findViewById(R.id.soLuongThucValue);
            priceTextView = (TextView)item_View.findViewById(R.id.giaValue);
            priceTextView.setVisibility(View.INVISIBLE);
            updateDateTextView = (TextView)item_View.findViewById(R.id.updateDateValue);
//            item_View.setOnClickListener(this);
            item_View.setOnLongClickListener(this);
//            item_View.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }
        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public boolean onLongClick(View v) {
            setSelectedPosition(getAdapterPosition());
            itemClickListener.onClick(v,getAdapterPosition(),true);
            return true;
        }

//        @Override
//        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
////            getMenuInflater().inflate(R.menu.contexts_menu, menu);
//            menu.setHeaderTitle("Select The Action");
//            menu.add(0, v.getId(), 0, "Call");//groupId, itemId, order, title
//            menu.add(0, v.getId(), 0, "SMS");
//        }
    }
}
