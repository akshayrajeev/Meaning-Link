package com.meaninglink;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {
    ArrayList<Note> notes;
    SaveLoad saveLoad;

    PreviewAdapter(ArrayList<Note> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public PreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);
        int width = parent.getMeasuredWidth();
        itemView.getLayoutParams().height = width/2;
        saveLoad = new SaveLoad(parent.getContext());
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewAdapter.ViewHolder holder, final int position) {
        final Note note = notes.get(position);
        holder.preview.setText(note.getInput());
        if(note.getDate().equals(saveLoad.getDate())) {
            holder.dateTime.setText(note.getTime());
        }
        else {
            holder.dateTime.setText(note.getDate());
        }

        holder.preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ViewActivity.class);
                i.putExtra("input", note.getInput());
                i.putExtra("key", note.getKey());
                v.getContext().startActivity(i);
            }
        });

        holder.preview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Do you want to delete this note?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notes.remove(position);
                        saveLoad.save(notes);
                        notifyDataSetChanged();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView preview;
        TextView dateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.row_layout_tv_preview);
            dateTime = itemView.findViewById(R.id.row_layout_datetime);
        }
    }
}
