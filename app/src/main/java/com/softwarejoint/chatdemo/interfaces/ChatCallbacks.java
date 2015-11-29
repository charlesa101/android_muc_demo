package com.softwarejoint.chatdemo.interfaces;

import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.chatstates.ChatStateListener;

/**
 * Created by pankajsoni on 30/08/15.
 */
public interface ChatCallbacks extends ChatStateListener, RosterListener {}
