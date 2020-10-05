package fr.intech.cormand.cryptgsm.Conversations;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.intech.cormand.cryptgsm.Msg;
import fr.intech.cormand.cryptgsm.R;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgViewHolder> {

    private List<Msg> items;

    public List<Msg> getItems() {
        return items;
    }

    public void setItems(List<Msg> items) {
        this.items = items;
    }

    public static class MsgViewHolder extends RecyclerView.ViewHolder {
        View v;
        TextView msg;

        public MsgViewHolder(View v) {
            super(v);
            this.v = v;
            msg = v.findViewById(R.id.textViewMsg);
        }
    }

    public MsgAdapter(List<Msg> items) {
        this.items = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MsgAdapter.MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg_item, parent, false);

        MsgAdapter.MsgViewHolder vh = new MsgAdapter.MsgViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MsgAdapter.MsgViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.msg.setText(items.get(position).getBody());

        if (items.get(position).getIsUser()) {
            holder.v.setBackgroundColor(Color.rgb(0, 70, 150));
        } else {
            holder.v.setBackgroundColor(Color.rgb(250, 235, 215));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}
