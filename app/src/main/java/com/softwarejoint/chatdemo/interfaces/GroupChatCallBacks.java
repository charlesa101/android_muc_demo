package com.softwarejoint.chatdemo.interfaces;

import android.content.Context;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.muc.UserStatusListener;

/**
 * Created by bhartisharma on 29/11/15.
 */
public interface GroupChatCallBacks extends MessageListener, PresenceListener,
		ParticipantStatusListener, UserStatusListener, InvitationRejectionListener,
		SubjectUpdatedListener, ChatCallbacks{

	Context getContext();
}
