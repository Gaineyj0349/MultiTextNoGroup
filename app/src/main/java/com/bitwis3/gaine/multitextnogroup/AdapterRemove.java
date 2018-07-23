package com.bitwis3.gaine.multitextnogroup;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import spencerstudios.com.fab_toast.FabToast;

public class AdapterRemove extends RecyclerView.Adapter<AdapterRemove.MyViewHolder>{

    List<Contact> list;
    Context context;
    DBRoom db;

    public AdapterRemove(List<Contact> list, Context context, DBRoom db) {
        this.list = list;
        this.context = context;
        this.db = db;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rvremoveitem, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                }else{
                    builder = new AlertDialog.Builder(context);

                }
                builder.setTitle("Confirm this Removal")
                        .setMessage("This will remove this member from this group.")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.multiDOA().deleteWithId(list.get(position).getId());
                                list.remove(position);
                                notifyDataSetChanged();
                                FabToast.makeText(context, "Deleted!", Toast.LENGTH_SHORT, FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show();

                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.stat_sys_warning)
                        .show();
            }
        });

        holder.tv.setText(list.get(position).getName());
        holder.tvNum.setText(list.get(position).getNumber());
    }

    @Override
    public int getItemCount() {
        if(list!= null && list.size()>0){
            return list.size();
        }
        else{
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tv;
        CustomTextView tvNum;
        ImageView iv;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.removetv);
            tvNum = itemView.findViewById(R.id.removetv2);
            iv = itemView.findViewById(R.id.removeiv);
        }
    }
}
