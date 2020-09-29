package fr.intech.cormand.cryptgsm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
