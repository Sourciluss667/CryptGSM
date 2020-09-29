package fr.intech.cormand.cryptgsm;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ConversationsViewHolder> {
    private List<Conversation> items;

    public static class ConversationsViewHolder extends RecyclerView.ViewHolder {
        TextView snippet;
        TextView address;
        ImageView picture;
        View v;

        public ConversationsViewHolder(View v) {
            super(v);
            this.v = v;
            snippet = v.findViewById(R.id.conv_item_snippet);
            address = v.findViewById(R.id.conv_item_address);
            picture = v.findViewById(R.id.conv_item_picture);
        }
    }

    public ConversationsAdapter(List<Conversation> items) {
        this.items = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ConversationsAdapter.ConversationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_item, parent, false);

        ConversationsViewHolder vh = new ConversationsViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ConversationsViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.snippet.setText(items.get(position).getSnippet());
        if (items.get(position).getContactName() != "") {
            holder.address.setText(items.get(position).getContactName());
        } else {
            holder.address.setText(items.get(position).getAddress());
        }
        if (items.get(position).getContactPicture() != null) {
            holder.picture.setImageBitmap(items.get(position).getContactPicture());
        }

        if (position % 2 == 0) {
            holder.v.setBackgroundColor(Color.rgb(238, 227, 211));
        } else {
            holder.v.setBackgroundColor(Color.rgb(250, 235, 215));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<Conversation> getItems() {
        return items;
    }

    public void setItems(List<Conversation> items) {
        this.items = items;
    }
}
