package com.softwarejoint.chatdemo.xmpp;

import com.softwarejoint.chatdemo.BaseActivity;
import com.softwarejoint.chatdemo.MainApplication;
import com.softwarejoint.chatdemo.interfaces.GroupChatCallBacks;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;

/**
 * Created by bhartisharma on 29/11/15.
 */
public class GroupIncomingMessage implements StanzaListener{

	@Override
	public void processPacket(Stanza packet) throws SmackException.NotConnectedException
	{
		Message message = (Message) packet;
		String body = message.getBody();
		String roomJID = message.getFrom();

		ChatStateExtension chatStateExtension = (ChatStateExtension) message.getExtension(ChatStateExtension.NAMESPACE);
		if(chatStateExtension != null){
			ChatState chatState = chatStateExtension.getChatState();
			BaseActivity baseActivity = MainApplication.getInstance().getCurrentActivity();
			if(baseActivity instanceof GroupChatCallBacks){
				Chat chat = XMPPUtils
						.getOrCreateChat(roomJID, MainApplication.getInstance().getXMPPManager().getConnection());
				((ChatStateListener) baseActivity).stateChanged(chat, chatState);
			}
		}

		if(body == null || body.length() == 0){
			return;
		}


		String groupId = XMPPUtils.getGroupId(roomJID);
		String userId = XMPPUtils.getUsernameFromGroupJID(roomJID);

		XMPPUtils.XMPP_MESSAGE_TYPE bodyType = XMPPUtils.getMessageBodyType(message);
		XMPPUtils.onChatReceivedUpdateUI(groupId, userId, body, bodyType, message.getStanzaId());
	}
}
