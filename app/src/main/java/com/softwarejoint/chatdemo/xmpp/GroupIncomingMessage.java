package com.softwarejoint.chatdemo.xmpp;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.softwarejoint.chatdemo.DBHelper.AppDbHelper;
import com.softwarejoint.chatdemo.MainApplication;
import com.softwarejoint.chatdemo.constant.AppConstant;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

/**
 * Created by bhartisharma on 29/11/15.
 */
public class GroupIncomingMessage implements StanzaListener{

	@Override
	public void processPacket(Stanza packet) throws SmackException.NotConnectedException
	{
		Message message = (Message) packet;
		String body = message.getBody();
		if(body == null || body.length() == 0){
			//incoming read receipt or delivery receipt
			return;
		}

		String roomJID = message.getFrom();
		String groupId = XMPPUtils.getGroupId(roomJID);
		String userId = XMPPUtils.getUsernameFromGroupJID(roomJID);

		XMPPUtils.XMPP_MESSAGE_TYPE bodyType = XMPPUtils.getMessageBodyType(message);

		AppDbHelper appDbHelper = MainApplication.getInstance().getAppDBHelper();
		appDbHelper.saveNewGroupChatMessage(groupId, userId, body, bodyType, false, message.getStanzaId());

		onChatReceivedUpdatedUI(groupId, userId, body, bodyType, false);
	}

	public void onChatReceivedUpdatedUI(String groupId, String userId, String body,
	                                           XMPPUtils.XMPP_MESSAGE_TYPE bodyType, boolean isOutGoing) {
		MainApplication mainApplication = MainApplication.getInstance();
		if (mainApplication != null && mainApplication.isForeGround()) {
			Intent intent = new Intent(AppConstant.CHAT_EVENT);
			intent.putExtra(AppConstant.CHAT_EVENT, userId);
			LocalBroadcastManager.getInstance(mainApplication).sendBroadcast(intent);
		}
	}
}
