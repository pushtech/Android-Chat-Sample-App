package com.pushtech.pushchat.androidapplicationexample.chat.chatscreens.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pushtech.pushchat.androidapplicationexample.chat.chatscreens.adapter.binder.CursorViewBinder;
import com.pushtech.pushchat.androidapplicationexample.chat.chatscreens.adapter.binder.incoming.ContactIncomingViewBinder;
import com.pushtech.pushchat.androidapplicationexample.chat.chatscreens.adapter.binder.incoming.LocationIncomingViewBinder;
import com.pushtech.pushchat.androidapplicationexample.chat.chatscreens.adapter.binder.incoming.PictureIncomingViewBinder;
import com.pushtech.pushchat.androidapplicationexample.chat.chatscreens.adapter.binder.incoming.TextIncomingViewBinder;
import com.pushtech.pushchat.androidapplicationexample.chat.chatscreens.adapter.binder.incoming.VideoIncomingViewBinder;
import com.pushtech.sdk.chat.db.contentvaluesop.ChatMessageContentValuesOp;
import com.pushtech.sdk.chat.model.message.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goda87
 */
public class ChatCursorAdapter extends CursorAdapter {
    private  static final String TAG = ChatCursorAdapter.class.getSimpleName();

    private Map<String, String> mUserNameCache = new ConcurrentHashMap<String, String>();
    private Context mContext;
    private boolean mIsGroupChat;

    private List<CursorViewBinder> mCursorViewBinders;

    public ChatCursorAdapter(Context context, Cursor c, boolean isGroupChat) {
        super(context, c, FLAG_AUTO_REQUERY);
        mContext = context;
        mIsGroupChat = isGroupChat;
        mCursorViewBinders = new ArrayList<CursorViewBinder>();

        registerViewBinders();
    }

    private void registerViewBinders() {
        mCursorViewBinders.add(new TextIncomingViewBinder(mIsGroupChat, mUserNameCache));
        mCursorViewBinders.add(new PictureIncomingViewBinder(mIsGroupChat, mUserNameCache));
        mCursorViewBinders.add(new VideoIncomingViewBinder(mIsGroupChat, mUserNameCache));
        mCursorViewBinders.add(new LocationIncomingViewBinder(mIsGroupChat, mUserNameCache));
        mCursorViewBinders.add(new ContactIncomingViewBinder(mIsGroupChat, mUserNameCache));

//        mCursorViewBinders.add(new OutgoingChatMessageViewBinder());
//        mCursorViewBinders.add(new OutgoingPictureViewBinder());
//        mCursorViewBinders.add(new OutgoingLocationViewBinder());
//        mCursorViewBinders.add(new OutgoingVideoViewBinder());
//        mCursorViewBinders.add(new OutgoingContactViewBinder());
    }

    @Override
    public int getViewTypeCount() {
        return mCursorViewBinders.size();
    }

    private CursorViewBinder getBinder(Cursor cursor) {
        for(CursorViewBinder cursorViewBinder : mCursorViewBinders) {
            if(cursorViewBinder.isBindableFrom(cursor)) {
                return cursorViewBinder;
            }
        }

        return null;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int viewResource = getBinder(cursor).getViewLayout();
        return  inflater.inflate(viewResource, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        getBinder(cursor).bindView(view, context, cursor);
    }

    public ChatMessage getChatMessage(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return getChatMessage(cursor);
    }

    public ChatMessage getChatMessage(Cursor cursor) {
        ChatMessageContentValuesOp chatMessageContentValuesOp = new ChatMessageContentValuesOp();
        return chatMessageContentValuesOp.buildFromCursor(cursor);
    }
}
