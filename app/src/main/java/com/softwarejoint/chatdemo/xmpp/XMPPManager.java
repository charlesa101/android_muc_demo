package com.softwarejoint.chatdemo.xmpp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.softwarejoint.chatdemo.AppPrefs.AppPreferences;
import com.softwarejoint.chatdemo.DBHelper.AppDbHelper;
import com.softwarejoint.chatdemo.MainApplication;
import com.softwarejoint.chatdemo.constant.AppConstant;
import com.softwarejoint.chatdemo.interfaces.ChatCallbacks;
import com.softwarejoint.chatdemo.interfaces.GroupChatCallBacks;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.ping.provider.PingProvider;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.pubsub.provider.EventProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.time.provider.TimeProvider;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

/**
 * @author Pankaj Soni <pankajsoni@softwarejoint.com>
 *         This is xmpp manager class use to perform all the operation regarding xmpp
 *         like sending message creating connection sending file etc.
 */

public class XMPPManager {

    private static final String TAG = "XMPPManager";
    private static XMPPManager sXmppManager;
    private XMPPTCPConnection sXmppConnection;
    private Context mContext;
    private Thread mXMPPConnectionThread;

    private AppPreferences mAppPreferences;
	private XMPPConnectionListener mXMPPConnectionListener;

    private XMPPManager(final Context context) {
        mContext = context;
        mAppPreferences = AppPreferences.getInstance(mContext);
        initXMPPConnection();
    }

    public synchronized static XMPPManager getInstance(Context context) {

        if (sXmppManager == null) {
            sXmppManager = new XMPPManager(context);
            Log.d(TAG, "XMPP Manager creating new connection");
        }

        sXmppManager.initXMPPConnection();
        return sXmppManager;
    }

    public XMPPTCPConnection getConnection() {
        return sXmppConnection;
    }

    public void initXMPPConnection() {
        Log.d(TAG, "connecction ->" + sXmppConnection);
        if (!isAlive()) {

            mXMPPConnectionThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (sXmppConnection == null
                            || !sXmppConnection.isSocketClosed()) {
                        createNewConnection();
                        registerReceiver();
                        try {
                            sXmppConnection.connect();
                            sXmppConnection.login();
                        } catch (Exception e) {
                            e.printStackTrace();
	                        mXMPPConnectionListener.onError(e);
                        }
                    }
                }
            });

            mXMPPConnectionThread.start();
        }
    }

    /**
     * Parses the current preferences and returns an new unconnected
     * XMPPConnection
     *
     * @return
     * @throws XMPPException
     */
    private XMPPConnection createNewConnection() {
        AppPreferences prefs = AppPreferences.getInstance(mContext);

        XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                .setPort(AppConstant.XMPP_PORT)
                .setUsernameAndPassword(prefs.getUserName(), prefs.getPassword())
                .setDebuggerEnabled(true)
                .setCompressionEnabled(false)
                .setServiceName(AppConstant.XMPP_DOMAIN)
                .setHost(AppConstant.SERVER_IP)
                .setResource(XMPPUtils.getResource(mContext))
                .setSendPresence(mAppPreferences.isPresenceVisible())
                .build();

	    mXMPPConnectionListener = new XMPPConnectionListener();
        sXmppConnection = new XMPPTCPConnection(conf);
	    sXmppConnection.addConnectionListener(mXMPPConnectionListener);
        PingManager.setDefaultPingInterval(10);
        PingManager.getInstanceFor(sXmppConnection);
        DeliveryReceiptManager mDeliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(sXmppConnection);
        mDeliveryReceiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.disabled);
        mDeliveryReceiptManager.dontAutoAddDeliveryReceiptRequests();
        Roster.getInstanceFor(sXmppConnection).setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        ChatStateManager.getInstance(sXmppConnection);
	    MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(sXmppConnection);
	    multiUserChatManager.addInvitationListener(new GroupInvitationListener());
	    sXmppConnection.setUseStreamManagement(true);
	    sXmppConnection.setUseStreamManagementResumption(true);
	    XMPPUtils.configure();
        return sXmppConnection;
    }

    private void registerReceiver() {

	    sXmppConnection.addStanzaAcknowledgedListener(new StanzaListener() {
		    @Override
		    public void processPacket(Stanza packet) throws SmackException.NotConnectedException
		    {
			    if(packet instanceof Message){
				    Message message = (Message) packet;

				    if(message.getType() == Message.Type.groupchat){
					    String groupId = XMPPUtils.getGroupId(message.getTo());
					    String userId = MainApplication.getInstance().getAppPreferences().getUserName();
					    AppDbHelper appDbHelper = MainApplication.getInstance().getAppDBHelper();
					    appDbHelper.updateMessageStatus(groupId, userId, message.getStanzaId(),
							    AppDbHelper.MSG_STATUS_SENT_TO_SERVER);
					    XMPPUtils.onChatReceivedUpdateUI(groupId, userId, null, null, message.getStanzaId());
				    }else if(message.getType() == Message.Type.chat){
					    String userId = XMPPUtils.getUserJID(message.getTo());
					    AppDbHelper appDbHelper = MainApplication.getInstance().getAppDBHelper();
					    appDbHelper.updateMessageStatus(null, userId, message.getStanzaId(),
							    AppDbHelper.MSG_STATUS_SENT_TO_SERVER);
					    XMPPUtils.onChatReceivedUpdateUI(null, userId, null, null, message.getStanzaId());
				    }
			    }
		    }
	    });

	    sXmppConnection.addSyncStanzaListener(
			    new ReceiptListener(DeliveryReceipt.ELEMENT, AppDbHelper.MSG_STATUS_SENT_TO_DEVICE),
			    XMPPUtils.INCOMING_DELIVERY_RECEIPT);

	    sXmppConnection.addSyncStanzaListener(
			    new ReceiptListener(ReceiptListener.READ_ELEMENT, AppDbHelper.MSG_STATUS_READ_BY_DEVICE),
			    XMPPUtils.INCOMING_READ_RECEIPT);

	    sXmppConnection.addAsyncStanzaListener(new PrivateIncomingMessage(), XMPPUtils.INCOMING_CHAT_MESSAGE);

	    sXmppConnection.addAsyncStanzaListener(new GroupIncomingMessage(), XMPPUtils.INCOMING_GROUP_MESSAGE);
    }

    public void cleanUpConnection() {
        if (sXmppConnection != null && sXmppConnection.isConnected()) {
            sXmppConnection.instantShutdown();
            sXmppConnection = null;
        } else {
            sXmppConnection = null;
        }
    }

	//utility functions start

	public boolean sendDeliveryReceipt(@Nullable String groupId, @NonNull String userId, @NonNull String messageId){
		Message message = new Message();
		message.addExtension(new DeliveryReceipt(messageId));
		message.setSubject(groupId);
		Chat chat = XMPPUtils.getOrCreateChat(userId, sXmppConnection);
		try
		{
			chat.sendMessage(message);
		}catch (SmackException.NotConnectedException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean sendReadReceipt(@Nullable String groupId, @NonNull String userId, @NonNull String messageId){
		Message message = new Message();
		DefaultExtensionElement readReceiptElement = new DefaultExtensionElement(ReceiptListener.READ_ELEMENT, DeliveryReceipt.NAMESPACE);
		readReceiptElement.setValue(ReceiptListener.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
		message.addExtension(readReceiptElement);
		message.setSubject(groupId);
		Chat chat = XMPPUtils.getOrCreateChat(userId, sXmppConnection);
		try
		{
			chat.sendMessage(message);
		}catch (SmackException.NotConnectedException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void sendTypingNotification(String id, ChatState chatState, boolean isGroup)
			throws SmackException.NotConnectedException{

		if(isGroup){
			Message typingNotification = new Message();
			typingNotification.addExtension(new ChatStateExtension(chatState));
			sendMucStanza(id, typingNotification);
		}else{
			Chat chat = XMPPUtils.getOrCreateChat(id, sXmppConnection);
			ChatStateManager chatStateManager = ChatStateManager.getInstance(sXmppConnection);
			chatStateManager.setCurrentState(chatState, chat);
		}

	}

	public boolean sendChatStanza(String id, String body, XMPPUtils.XMPP_MESSAGE_TYPE bodyType, boolean isGroup){

		Message message = new Message();
		message.setBody(body);
		message.addExtension(new DeliveryReceiptRequest());
		XMPPUtils.addMessageBodyType(message, bodyType);
		AppDbHelper appDbHelper = MainApplication.getInstance().getAppDBHelper();

		try{
			if(isGroup){
				appDbHelper.saveNewChatMessage(id, null, body, bodyType, true, message.getStanzaId());
				return sendMucStanza(id, message);
			}else{
				appDbHelper.saveNewChatMessage(null, id, body, bodyType, true, message.getStanzaId());
				Chat chat = XMPPUtils.getOrCreateChat(id, sXmppConnection);
				chat.sendMessage(message);
				return true;
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}

		appDbHelper.addOfflineMessage(id, body, bodyType, false);
		return false;
	}

	//utility functions end

	//private chat functions start

	public Chat startChat(String userId, ChatCallbacks chatCallbacks){
		Chat chat = XMPPUtils.getOrCreateChat(userId, sXmppConnection);
		for(ChatMessageListener chatMessageListener: chat.getListeners()){
			if(chatMessageListener instanceof ChatCallbacks){
				chat.removeMessageListener(chatMessageListener);
			}
		}

		chat.addMessageListener(chatCallbacks);
		try
		{
			sendTypingNotification(userId, ChatState.active, false);
		}
		catch (SmackException.NotConnectedException e)
		{
			e.printStackTrace();
			return null;
		}
		return chat;
	}

	public void closeChat(String userId){
		Chat chat = XMPPUtils.getOrCreateChat(userId, sXmppConnection);
		try
		{
			sendTypingNotification(userId, ChatState.gone, false);
		}
		catch (SmackException.NotConnectedException e)
		{
			e.printStackTrace();
		}
		chat.close();
	}



	// private chat functions end

	// group chat functions start

	/** call this when a group activity is launched
	 *
	 * */

 	public void addGroupCallBacks(String groupId, GroupChatCallBacks groupChatCallBacks){
	    MultiUserChat multiUserChat = XMPPUtils.getMultiUserChat(sXmppConnection, groupId);
	    multiUserChat.addMessageListener(groupChatCallBacks); //cursordapater.reload
	    multiUserChat.addParticipantListener(groupChatCallBacks);
	    multiUserChat.addParticipantStatusListener(groupChatCallBacks);
	    multiUserChat.addUserStatusListener(groupChatCallBacks);
	    multiUserChat.addInvitationRejectionListener(groupChatCallBacks);
	    multiUserChat.addSubjectUpdatedListener(groupChatCallBacks);
	}

	public void removeGroupCallBacks(String groupId, GroupChatCallBacks groupChatCallBacks){
		MultiUserChat multiUserChat = XMPPUtils.getMultiUserChat(sXmppConnection, groupId);
		multiUserChat.removeMessageListener(groupChatCallBacks);
		multiUserChat.removeParticipantListener(groupChatCallBacks);
		multiUserChat.removeParticipantStatusListener(groupChatCallBacks);
		multiUserChat.removeUserStatusListener(groupChatCallBacks);
		multiUserChat.removeInvitationRejectionListener(groupChatCallBacks);
		multiUserChat.removeSubjectUpdatedListener(groupChatCallBacks);
	}

	public MultiUserChat createGroupChat(@Nullable String groupId, @NonNull String groupTitle){
		groupId = (groupId == null) ? UUID.randomUUID().toString() : groupId;
		MultiUserChat multiUserChat = XMPPUtils.getMultiUserChat(sXmppConnection, groupId);
		try
		{
			if(multiUserChat.createOrJoin(mAppPreferences.getUserName())
					&& setGroupTitle(groupId, groupTitle)){
				return multiUserChat;
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return null;
	}

	public boolean setGroupTitle(String groupId, String groupTitle){
		Form form = new Form(DataForm.Type.submit);
		form.setAnswer("muc#roomconfig_roomname", groupTitle.trim());
		MultiUserChat multiUserChat = XMPPUtils.getMultiUserChat(sXmppConnection, groupId);
		try{
			multiUserChat.sendConfigurationForm(form);
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public MultiUserChat joinGroupChat(@NonNull String groupId){

		MultiUserChat multiUserChat = XMPPUtils.getMultiUserChat(sXmppConnection, groupId);

		if(!multiUserChat.isJoined()){
			String username = mAppPreferences.getUserName();
			try
			{
				multiUserChat.join(username);
				return multiUserChat;
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		return null;
	}

	public boolean leaveRoom(@NonNull String groupId){
		MultiUserChat multiUserChat = XMPPUtils.getMultiUserChat(sXmppConnection, groupId);
		try
		{
			List<Affiliate> members = multiUserChat.getMembers();

			if(members.size() == 1){
				return destroyRoom(groupId, null);
			}

			String username = mAppPreferences.getUserName();
			List<Affiliate> admins = multiUserChat.getAdmins();
			ListIterator<Affiliate> itr = admins.listIterator();
			boolean isAdmin = false;

			while(itr.hasNext() && !isAdmin){
				isAdmin = itr.next().getJid().startsWith(username);
			}

			if(isAdmin && admins.size() == 1){
				ListIterator<Affiliate> membersItr = members.listIterator();
				while(membersItr.hasNext()){
					if(membersItr.next().getJid().startsWith(username)){
						membersItr.remove();
					}
				}

				makeGroupAdmin(groupId, members.get(0).getJid());
			}

			multiUserChat.leave();

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public boolean makeGroupAdmin(String groupId, String userId){
		MultiUserChat multiUserChat = XMPPUtils.getMultiUserChat(sXmppConnection, groupId);
		try{
			multiUserChat.grantAdmin(userId);
			return true;
		}catch (Exception e){
			e.printStackTrace();
		}

		return false;
	}

	public boolean destroyRoom(@NonNull String groupId, String reason){
		MultiUserChat multiUserChat = XMPPUtils.getMultiUserChat(sXmppConnection, groupId);
		reason = (reason == null) ? "destroyed" : reason;
		try{
			multiUserChat.destroy(reason, null);
			return true;
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public boolean addUserToRoom(@NonNull String groupId, @NonNull String userId, @Nullable String reason){
		MultiUserChat multiUserChat = XMPPUtils.getMultiUserChat(sXmppConnection, groupId);
		reason = (reason == null) ? AppConstant.DEFAULT_USER_RESOURCE : reason;
		String userJID = XMPPUtils.getUserJID(userId);
		try
		{
			multiUserChat.invite(userJID, reason);
			multiUserChat.grantMembership(userJID);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeUserFromRoom(@NonNull String groupId, @NonNull String userId, @Nullable String reason){
		MultiUserChat multiUserChat = XMPPUtils.getMultiUserChat(sXmppConnection, groupId);
		reason = (reason == null) ? AppConstant.DEFAULT_USER_RESOURCE : reason;
		try{
			multiUserChat.kickParticipant(userId, reason);
			multiUserChat.banUser(userId, reason);
			return true;
		}catch (Exception e){
			e.printStackTrace();
		}

		return false;
	}

	private boolean sendMucStanza(String groupId, Object message) throws SmackException.NotConnectedException
	{
		MultiUserChat multiUserChat = XMPPUtils.getMultiUserChat(sXmppConnection, groupId);
		if(message instanceof Message){
			multiUserChat.sendMessage((Message) message);
		}else{
			//todo handle text message
		}

		return true;
	}

	// group chat functions end

    public boolean isAlive() {
        return (sXmppConnection != null
                && sXmppConnection.isAuthenticated()
                && sXmppConnection.isConnected()
                && !sXmppConnection.isSocketClosed());
    }
}
