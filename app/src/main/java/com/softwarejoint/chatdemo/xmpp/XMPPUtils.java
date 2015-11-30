package com.softwarejoint.chatdemo.xmpp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;

import com.softwarejoint.chatdemo.Activity.ChatActivity;
import com.softwarejoint.chatdemo.Activity.GroupListActivity;
import com.softwarejoint.chatdemo.AppPrefs.AppPreferences;
import com.softwarejoint.chatdemo.DBHelper.AppDbHelper;
import com.softwarejoint.chatdemo.MainApplication;
import com.softwarejoint.chatdemo.constant.AppConstant;
import com.softwarejoint.chatdemo.gcm.MyGcmListenerService;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.ping.provider.PingProvider;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.pubsub.provider.EventProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.time.provider.TimeProvider;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;

import java.util.UUID;

/**
 * Created by bhartisharma on 29/11/15.
 */
public class XMPPUtils
{
	public static final String EXTENSION_PROPERTIES = "properties";
	public static final String EXTENSION_PROPERTIES_NS = "urn:xmpp:softwarejoint:properties:1";
	public static final String EXTENSION_PROPERTIES_TAG_TYPE = "bodyType";

	public enum XMPP_MESSAGE_TYPE {
		DELETED("deleted"),
		READ("read"),
		TEXT("text"),
		AUDIO("audio"),
		VIDEO("video"),
		OTHER("other");

		private final String msgType;

		XMPP_MESSAGE_TYPE(String f) {
			msgType = f;
		}

		public static XMPP_MESSAGE_TYPE getMsgTypeByVal(String type) {
			for (XMPP_MESSAGE_TYPE t : XMPP_MESSAGE_TYPE.values()) {
				if (t.msgType.equalsIgnoreCase(type)) {
					return t;
				}
			}

			return XMPP_MESSAGE_TYPE.OTHER;
		}

		public String toString() {
			return msgType;
		}
	}

	public static StanzaFilter INCOMING_DELIVERY_RECEIPT =
			new AndFilter(StanzaTypeFilter.MESSAGE,
					new AndFilter(MessageTypeFilter.CHAT,
							new StanzaExtensionFilter(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE)));

	public static StanzaFilter INCOMING_READ_RECEIPT =
			new AndFilter(StanzaTypeFilter.MESSAGE,
					new AndFilter(MessageTypeFilter.CHAT,
							new StanzaExtensionFilter(ReceiptListener.READ_ELEMENT, DeliveryReceipt.NAMESPACE)));

	public static StanzaFilter INCOMING_CHAT_MESSAGE =
			new AndFilter(StanzaTypeFilter.MESSAGE,
					new AndFilter(MessageTypeFilter.CHAT,
							new StanzaExtensionFilter(DeliveryReceiptRequest.ELEMENT, DeliveryReceipt.NAMESPACE)));

	public static StanzaFilter INCOMING_GROUP_MESSAGE =
			new AndFilter(StanzaTypeFilter.MESSAGE, MessageTypeFilter.GROUPCHAT);

	public static String getUsernamePriv(String jid) {
		return jid.split("@")[0].trim();
	}

	public static String getUsernameFromGroupJID(String roomJID) {
		return roomJID.split("/")[1].trim();
	}

	public static String getGroupId(String roomJID) {
		return roomJID.split("@")[0].trim();
	}

	public static String getRoomJID(String roomName) {
		return roomName + AppConstant.XMPP_MUC_DOMAIN_SUFFIX;
	}

	public static String getUserJID(String username) {
		return username + AppConstant.XMPP_USER_SUFFIX;
	}

	public static void addMessageBodyType(Message message, XMPP_MESSAGE_TYPE bodyType)
	{
		DefaultExtensionElement extensionElement = new DefaultExtensionElement(EXTENSION_PROPERTIES, EXTENSION_PROPERTIES_NS);
		extensionElement.setValue(EXTENSION_PROPERTIES_TAG_TYPE, bodyType.toString());
		message.addExtension(extensionElement);
	}

	public static XMPP_MESSAGE_TYPE getMessageBodyType(Message message)
	{
		DefaultExtensionElement extensionElement =
				message.getExtension(XMPPUtils.EXTENSION_PROPERTIES, XMPPUtils.EXTENSION_PROPERTIES_NS);

		String bodyType = extensionElement.getValue(XMPPUtils.EXTENSION_PROPERTIES_TAG_TYPE);
		return XMPP_MESSAGE_TYPE.getMsgTypeByVal(bodyType);
	}

	public static String getThreadId(String userId){
		String myUserId = MainApplication.getInstance().getAppPreferences().getUserName();
		String myJID = XMPPUtils.getUserJID(myUserId);
		String userJID = XMPPUtils.getUserJID(userId);
		return (myJID.compareTo(userJID) > 0) ? (userJID + "-" + myJID) : (myJID + "-" + userJID);
	}

	public static Chat getOrCreateChat(String userId, XMPPTCPConnection sXmppConnection){
		String jid = userId.contains("@") ?  userId : XMPPUtils.getUserJID(userId);
		String threadId = XMPPUtils.getThreadId(userId);
		ChatManager chatManager = ChatManager.getInstanceFor(sXmppConnection);
		Chat chat = chatManager.getThreadChat(threadId);
		if(chat == null){
			chat = chatManager.createChat(jid, threadId, null);
		}
		return chat;
	}

	public static MultiUserChat getMultiUserChat(XMPPTCPConnection xmpptcpConnection, String groupId){
		String roomJID = XMPPUtils.getRoomJID(groupId);
		MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(xmpptcpConnection);
		return multiUserChatManager.getMultiUserChat(roomJID);
	}

	public static String getResource(Context context) {
		AppPreferences appPreferences = AppPreferences.getInstance(context);
		String resourceName = appPreferences.getResourceName();

		if(resourceName == null){
			final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			final String tmDevice, tmSerial, androidId;
			tmDevice = "" + tm.getDeviceId();
			tmSerial = "" + tm.getSimSerialNumber();
			androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

			UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
			resourceName = deviceUuid.toString();
			appPreferences.setResourceName(resourceName);
		}

		return resourceName;
	}

	public static void onChatReceivedUpdateUI(String groupId, String userId, String body,
	                                          XMPPUtils.XMPP_MESSAGE_TYPE bodyType, String messageId) {

		if(bodyType != null && body != null){
			AppDbHelper appDbHelper = MainApplication.getInstance().getAppDBHelper();
			appDbHelper.saveNewChatMessage(groupId, userId, body, bodyType, false, messageId);

			if(MainApplication.getInstance().getXMPPManager().sendDeliveryReceipt(groupId, userId, messageId)){
				appDbHelper.updateMessageStatus(groupId, userId, messageId, AppDbHelper.MSG_READ_STATUS_DELIVERY_REPORT_SENT);
			}
		}

		MainApplication mainApplication = MainApplication.getInstance();

		if(body != null
				&& (!mainApplication.isForeGround() || mainApplication.getCurrentActivity().isShowNotification(groupId, userId))){

			if(groupId == null){
				MyGcmListenerService.sendNotification(MainApplication.getInstance(), ChatActivity.class, groupId, userId, body);
			}else{
				MyGcmListenerService.sendNotification(MainApplication.getInstance(), GroupListActivity.class, groupId, userId, body);
			}

		}

		if (mainApplication.isForeGround()) {
			Intent intent = new Intent(AppConstant.CHAT_EVENT);
			if(groupId != null){
				intent.putExtra(AppConstant.GROUP_ID, groupId);
			}

			if(userId != null){
				intent.putExtra(AppConstant.USER_ID, userId);
			}

			if(body != null && body.length() > 0){
				intent.putExtra(AppConstant.MSG_DATA, body);
			}

			if(bodyType != null){
				intent.putExtra(AppConstant.BODY_TYPE, bodyType);
			}

			LocalBroadcastManager.getInstance(mainApplication).sendBroadcast(intent);
		}
	}

	public static void configure() {

		// Private Data Storage
		ProviderManager.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

		ProviderManager.addIQProvider("ping", "urn:xmpp:ping",
				new PingProvider());

		ProviderManager.addExtensionProvider("request", "urn:xmpp:receipts",
				new DeliveryReceiptRequest.Provider());

		ProviderManager.addIQProvider("query", "jabber:iq:time", new TimeProvider());

		// Message Events
		ProviderManager.addExtensionProvider("x", "jabber:x:event",
				new EventProvider());

		// Chat State
		ProviderManager.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		ProviderManager.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		ProviderManager.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		ProviderManager.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		ProviderManager.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// Group Chat Invitations
		ProviderManager.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());

		// Service Discovery # Items
		ProviderManager.addIQProvider("query",
				"http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());

		// Service Discovery # Info
		ProviderManager.addIQProvider("query",
				"http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());

		// Data Forms
		ProviderManager.addExtensionProvider("x", "jabber:x:data",
				new DataFormProvider());

		// MUC User
		ProviderManager.addExtensionProvider("x",
				"http://jabber.org/protocol/muc#user", new MUCUserProvider());

		// MUC Admin
		ProviderManager.addIQProvider("query",
				"http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

		// MUC Owner
		ProviderManager.addIQProvider("query",
				"http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

		// Version
		try {
			ProviderManager.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}

		// VCard
		ProviderManager.addIQProvider("vCard", "vcard-temp",
				new VCardProvider());

		// Offline Message Requests
		ProviderManager.addIQProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		ProviderManager.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());

		// Last Activity
		ProviderManager.addIQProvider("query", "jabber:iq:last",
				new LastActivity.Provider());

		// User Search
		ProviderManager.addIQProvider("query", "jabber:iq:search",
				new UserSearch.Provider());

		// SharedGroupsInfo
		ProviderManager.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		ProviderManager.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

		// Privacy
		ProviderManager.addIQProvider("query", "jabber:iq:privacy",
				new PrivacyProvider());

		ProviderManager.addIQProvider("command",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider());
		ProviderManager.addExtensionProvider("malformed-action",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		ProviderManager.addExtensionProvider("bad-locale",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		ProviderManager.addExtensionProvider("bad-payload",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		ProviderManager.addExtensionProvider("bad-sessionid",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		ProviderManager.addExtensionProvider("session-expired",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}
}
