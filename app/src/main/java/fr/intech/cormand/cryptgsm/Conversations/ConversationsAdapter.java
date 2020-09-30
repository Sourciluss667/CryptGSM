package fr.intech.cormand.cryptgsm.Conversations;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.intech.cormand.cryptgsm.Conversations.Conversation;
import fr.intech.cormand.cryptgsm.R;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ConversationsViewHolder> {
    private List<Conversation> items;

    public static class ConversationsViewHolder extends RecyclerView.ViewHolder {
        TextView snippet;
        TextView address;
        TextView displayName;
        ImageView picture;
        String addressId;
        View v;

        public ConversationsViewHolder(View v) {
            super(v);
            this.v = v;
            snippet = v.findViewById(R.id.conv_item_snippet);
            address = v.findViewById(R.id.conv_item_address);
            picture = v.findViewById(R.id.conv_item_picture);
            displayName = v.findViewById(R.id.display_name_textview);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Click on 1 conversation
                    Intent intent = new Intent(v.getContext(), ConversationActivity.class);
                    intent.putExtra("address", addressId);
                    v.getContext().startActivity(intent);
                }
            });

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
        holder.address.setText(items.get(position).getAddress());
        holder.displayName.setText(items.get(position).getDisplayName());
        holder.addressId = items.get(position).getAddress();

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
