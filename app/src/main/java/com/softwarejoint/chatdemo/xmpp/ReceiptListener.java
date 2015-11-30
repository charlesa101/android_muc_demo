package com.softwarejoint.chatdemo.xmpp;

import com.softwarejoint.chatdemo.DBHelper.AppDbHelper;
import com.softwarejoint.chatdemo.MainApplication;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;

/**
 * Created by bhartisharma on 30/11/15.
 */
public class ReceiptListener implements StanzaListener{

	public static final String READ_ELEMENT = "read";
	public static final String TIMESTAMP = "ts";

	private String element;
	private int msgStatus;

	public ReceiptListener(String element, int msgStatus){
		this.element = element;
		this.msgStatus = msgStatus;
	}

	@Override
	public void processPacket(Stanza packet) throws SmackException.NotConnectedException
	{
		Message message = (Message) packet;
		DeliveryReceipt deliveryReceipt = message.getExtension(element, DeliveryReceipt.NAMESPACE);
		String messageId = deliveryReceipt.getId();
		String userId = XMPPUtils.getUsernamePriv(message.getFrom());
		String groupId = message.getSubject();
		groupId = (groupId != null && groupId.length() == 0) ? null : groupId;
		AppDbHelper appDbHelper = MainApplication.getInstance().getAppDBHelper();
		appDbHelper.updateMessageStatus(groupId, userId, messageId, msgStatus);
		XMPPUtils.onChatReceivedUpdateUI(groupId, userId, null, null, messageId);
	}
}
