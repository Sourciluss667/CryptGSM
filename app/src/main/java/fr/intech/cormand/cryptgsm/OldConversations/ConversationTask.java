package fr.intech.cormand.cryptgsm.OldConversations;

import android.os.AsyncTask;

import fr.intech.cormand.cryptgsm.MainActivity;
import fr.intech.cormand.cryptgsm.OldConversations.Conversation;

public class ConversationTask extends AsyncTask<Conversation, Integer, Boolean> {
    MainActivity ctx;

    public ConversationTask (MainActivity ctx) {
        this.ctx = ctx;
    }

    @Override
    protected Boolean doInBackground(Conversation... conversations) {
        conversations[0].startMore();
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        // Reload RecyclerView
        ctx.getConversationsAdapter().notifyDataSetChanged();
    }

    public void setCtx(MainActivity ctx) {
        this.ctx = ctx;
    }
}
