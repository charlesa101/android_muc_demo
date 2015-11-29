package com.softwarejoint.chatdemo.xmpp;

import com.softwarejoint.chatdemo.MainApplication;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Created by bhartisharma on 29/11/15.
 */
public class GroupInvitationListener implements InvitationListener{

	@Override
	public void invitationReceived(XMPPConnection conn, MultiUserChat room,
	                               String inviter, String reason, String password, Message message)
	{
		MainApplication mainApplication = MainApplication.getInstance();
		if(mainApplication != null && mainApplication.isForeGround()){
			mainApplication.getCurrentActivity()
					.onInvitationReceivedForMuc(room, inviter, reason, password, message);
		}
	}
}
