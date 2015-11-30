package com.softwarejoint.chatdemo.interfaces;

import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.chatstates.ChatStateListener;


public interface ChatCallbacks extends ChatStateListener, RosterListener {}
