package com.bitwis3.gaine.multitextnogroup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import spencerstudios.com.fab_toast.FabToast;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

    Context context;
    List<Contact> records;
    Seed seed;
    DBRoom db;
    boolean missed = false;
    SmsManager smsManager;

    public LogAdapter(Context context, List<Contact> records, DBRoom db, boolean missed) {
        this.context = context;
        this.db = db;
        this.records = records;
        seed = new Seed(context);
        this.missed = missed;
        smsManager = SmsManager.getDefault();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            if(!missed){
               holder.ll.setVisibility(View.GONE);
            }else{

                holder.deleteB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder;
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                        }else{
                            builder = new AlertDialog.Builder(context);

                        }
                        builder.setTitle("Confirm this Record Delete")
                                .setMessage("This record fill be erased from the logs. If this is a Timed Text message that has not sent yet, you will need to recreate this.")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.multiDOA().deleteTransactionWithID(records.get(position).getId());
                                        records.remove(position);
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

                holder.sendB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if(Seed.isTelephonyMobileConnected(context)) {


                                if (records.get(position).getMessage().length() > 155) {
                                    ArrayList<String> parts = smsManager.divideMessage(records.get(position).getMessage());
                                    smsManager.sendMultipartTextMessage(records.get(position).getNumber(), null,
                                            parts, null, null);
                                } else {
                                    smsManager.sendTextMessage(records.get(position).getNumber(), null,
                                            records.get(position).getMessage(), null, null);
                                }
                                db.multiDOA().updateTypeEntryWithID(records.get(position).getId(), "notmissed");
                                db.multiDOA().updateDateWithID(records.get(position).getId(), records.get(position).getTimeInMillis());
                                records.remove(position);
                                notifyDataSetChanged();
                                FabToast.makeText(context, "SENT!", Toast.LENGTH_SHORT, FabToast.SUCCESS, FabToast.POSITION_DEFAULT).show();
                            }else{
                                FabToast.makeText(context, "Unable to send, Check your connection..", Toast.LENGTH_SHORT, FabToast.ERROR, FabToast.POSITION_DEFAULT).show();

                            }
                            }catch (Exception e){
                            FabToast.makeText(context, "We do apologize, but your device is not supported currently.", Toast.LENGTH_LONG, FabToast.ERROR, FabToast.POSITION_DEFAULT).show();

                        }
                    }
                });
            }


        holder.message.setText(records.get(position).getMessage());
        holder.name.setText(records.get(position).getName());
        if(records.get(position).getTypeEntry().equals("timed_text_not_sent")){
            holder.date.setText("Planned for " + seed.getLocaleDateString(records.get(position).getTimeInMillis()));
        }else{
            holder.date.setText(seed.getLocaleDateString(records.get(position).getTimeInMillis()));
        }

        switch (records.get(position).getTypeEntry()){

            case "auto_text":
               holder.bg.setBackground(context.getResources().getDrawable(R.drawable.gradient1));
                break;
            case "multi_text":
                holder.bg.setBackground(context.getResources().getDrawable(R.drawable.gradient2));
                break;
            case "timed_text_not_sent":
                holder.bg.setBackground(context.getResources().getDrawable(R.drawable.gradient3));
                break;
            case "timed_text_sent":
                holder.bg.setBackground(context.getResources().getDrawable(R.drawable.gradient3));
                break;
            case "missed":
                holder.bg.setBackground(context.getResources().getDrawable(R.drawable.gradient6));
                break;
            case "notmissed":
                holder.bg.setBackground(context.getResources().getDrawable(R.drawable.gradient7));
                break;
            case "EMERGENCY_TEXT_SENT":
                holder.bg.setBackground(context.getResources().getDrawable(R.drawable.gradient5));
                break;
            default:
        }


        holder.number.setText(records.get(position).getNumber());
        holder.type.setText(getStringOfType(records.get(position).getTypeEntry()));

       holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
           @Override
           public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

               menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem item) {
                       AlertDialog.Builder builder;
                       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                           builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                       }else{
                           builder = new AlertDialog.Builder(context);

                       }
                       builder.setTitle("Confirm this Record Delete")
                               .setMessage("This record fill be erased from the logs. If this is a Timed Text message that has not sent yet, you will need to recreate this.")
                               .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       db.multiDOA().deleteTransactionWithID(records.get(position).getId());                                       //  ((Activity)context).recreate();
                                       records.remove(position);
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


                       return true;
                   }
               });

               menu.add("Change Message").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem item) {
                       if(records.get(position).getTypeEntry().equals("timed_text_not_sent")) {


                           Intent i = new Intent(context, Main2Activity.class);
                           i.putExtra("edit", records.get(position).getId());
                           i.putExtra("editMessage", records.get(position).getMessage());
                           String type = records.get(position).getTypeEntry();
                           if (type.equals("timed_text_not_sent") || type.equals("timed_text_sent")) {
                               i.putExtra("timed", "timed");
                           }
                           context.startActivity(i);
                       }else{
                           FabToast.makeText(context, "Only pending messages can be edited.", FabToast.LENGTH_LONG
                           , FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show();
                       }
                       return true;
                   }
               });

               menu.add("Change Date/Time").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem item) {

                       if(records.get(position).getTypeEntry().equals("timed_text_not_sent")) {
                           Intent i = new Intent(context, Main3Activity.class);
                           i.putExtra("edit", records.get(position).getId());
                           String type = records.get(position).getTypeEntry();
                           if (type.equals("timed_text_not_sent") || type.equals("timed_text_sent")) {
                               i.putExtra("timed", "timed");
                           }
                           context.startActivity(i);
                       }else{
                           FabToast.makeText(context, "Only pending messages can be edited.", FabToast.LENGTH_LONG
                                   , FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show();
                       }
                       return true;
                   }
               });
           }
       });
    }

    private String getStringOfType(String typeEntry) {
        String s;

        switch (typeEntry){

            case "auto_text":
                s = "Message Sent In Auto Text Reply Mode:";
                break;
            case "multi_text":
                s = "Message Sent in a Multi Text.";
                break;
            case "timed_text_not_sent":
                s = "Message To Send On A Scheduled Time:";
                break;
            case "timed_text_sent":
                s = "Message Sent On A Scheduled Time:";
                break;
            case "missed":
                s = "This message did not send, connection issue?";
                break;
            case "notmissed":
                s = "This message was sent";
                break;
            case "EMERGENCY_TEXT_SENT":
                s = "Preset Text Sent";
                break;
                default:
                    s = "";
        }
        return s;
    }

    @Override
    public int getItemCount() {
        if(records == null){
            return 0;
        }else {
            return records.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CustomTextView type;
        public CustomTextView name;
        public CustomTextView number;
        public CustomTextView message;
        public CustomTextView date;
        public LinearLayout ll;
        public Button sendB;
        public Button deleteB;
        public LinearLayout bg;



        public ViewHolder(View itemView) {

            super(itemView);
            deleteB = (Button)itemView.findViewById(R.id.DeleteButtonLog);
            sendB = (Button)itemView.findViewById(R.id.SendButtonLog);
            ll = (LinearLayout)itemView.findViewById(R.id.llLog);
            type = (CustomTextView) itemView.findViewById(R.id.log_item_type);
            name = (CustomTextView) itemView.findViewById(R.id.log_item_name);
            number = (CustomTextView) itemView.findViewById(R.id.log_item_number);
            message = (CustomTextView) itemView.findViewById(R.id.log_item_message);
            date = (CustomTextView) itemView.findViewById(R.id.log_item_date);
            bg = (LinearLayout)itemView.findViewById(R.id.logbg);
        }
    }

}
