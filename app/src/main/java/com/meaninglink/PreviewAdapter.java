package com.meaninglink;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {
    ArrayList<Note> notes;

    PreviewAdapter(ArrayList<Note> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public PreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);
        int width = parent.getMeasuredWidth();
        itemView.getLayoutParams().height = width/3;
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewAdapter.ViewHolder holder, int position) {
        final Note note = notes.get(position);
        holder.preview.setText(note.getInput());

        holder.preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ViewActivity.class);
                i.putExtra("input", note.getInput());
                i.putExtra("key", note.getKey());
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView preview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.row_layout_tv_preview);
        }
    }
}
