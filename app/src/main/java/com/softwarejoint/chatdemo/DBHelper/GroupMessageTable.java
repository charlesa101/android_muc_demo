package com.softwarejoint.chatdemo.DBHelper;

/**
 * Created by bhartisharma on 29/11/15.
 */
public class GroupMessageTable{

	private final static int IS_OUTGOING = 1;

	public static final String TABLE_NAME = "group_messages";
	public static final String id = "id";
	public static final String groupId = "groupId";
	public static final String isOutGoing = "direction";
	public static final String userId = "userId";
	public static final String body = "body";
	public static final String bodyType = "bodyType";
	public static final String msgStatus = "msgStatus";
	public static final String ts = "timestamp";
	public static final String membersCount = "membersCount";
	public static final String deliveryCount = "deliveryCount";
	public static final String readCount = "readCount";
	public static final String messageId = "messageId";

	static final String Projection[] = {
		id,
		groupId,
		isOutGoing,
		userId,
		body,
		bodyType,
		msgStatus,
		ts,
		messageId
	};

	public static final String DELETE_STATEMENT = "DELETE FROM " + TABLE_NAME;

	public static String getCreateTableStmt(){
		return new StringBuilder()
				.append("CREATE TABLE ").append(TABLE_NAME).append(" (")
				.append(id).append(" INTEGER PRIMARY KEY AUTOINCREMENT ,")
				.append(groupId).append(" VARCHAR, ")
				.append(isOutGoing).append(" INTEGER, ")
				.append(userId).append(" VARCHAR, ")
				.append(body).append(" TEXT, ")
				.append(bodyType).append(" VARCHAR, ")
				.append(msgStatus).append(" INTEGER DEFAULT 0, ")
				.append(ts).append(" INTEGER, ")
				.append(membersCount).append(" INTEGER DEFAULT 0, ")
				.append(deliveryCount).append(" INTEGER DEFAULT 0, ")
				.append(readCount).append(" INTEGER DEFAULT 0, ")
				.append(messageId).append(" VARCHAR ,")
				.append(" UNIQUE (")
				.append(groupId).append(",").append(userId).append(",").append(messageId)
				.append(") ON CONFLICT REPLACE").toString();
	}

}

