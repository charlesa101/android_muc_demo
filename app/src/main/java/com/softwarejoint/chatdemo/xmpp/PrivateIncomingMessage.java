package com.softwarejoint.chatdemo.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

/**
 * Created by bhartisharma on 30/11/15.
 */
public class PrivateIncomingMessage implements StanzaListener{

	@Override
	public void processPacket(Stanza packet) throws SmackException.NotConnectedException
	{
		Message message = (Message) packet;
		String body = message.getBody();

		if(body == null || body.length() == 0){
			return;
		}

		String userId = XMPPUtils.getUsernamePriv(message.getFrom());
		XMPPUtils.XMPP_MESSAGE_TYPE bodyType = XMPPUtils.getMessageBodyType(message);
		XMPPUtils.onChatReceivedUpdateUI(null, userId, body, bodyType, message.getStanzaId());
	}
}
