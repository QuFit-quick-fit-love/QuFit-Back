package com.cupid.qufit.entity.chat;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatRoomMember is a Querydsl query type for ChatRoomMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoomMember extends EntityPathBase<ChatRoomMember> {

    private static final long serialVersionUID = -2060001445L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatRoomMember chatRoomMember = new QChatRoomMember("chatRoomMember");

    public final QChatRoom chatRoom;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastReadMessageId = createString("lastReadMessageId");

    public final DateTimePath<java.time.LocalDateTime> lastReadTime = createDateTime("lastReadTime", java.time.LocalDateTime.class);

    public final com.cupid.qufit.entity.QMember member;

    public final BooleanPath notificationEnabled = createBoolean("notificationEnabled");

    public final EnumPath<ChatRoomMemberStatus> status = createEnum("status", ChatRoomMemberStatus.class);

    public final DateTimePath<java.time.LocalDateTime> statusChangeAt = createDateTime("statusChangeAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> unreadCount = createNumber("unreadCount", Integer.class);

    public QChatRoomMember(String variable) {
        this(ChatRoomMember.class, forVariable(variable), INITS);
    }

    public QChatRoomMember(Path<? extends ChatRoomMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatRoomMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatRoomMember(PathMetadata metadata, PathInits inits) {
        this(ChatRoomMember.class, metadata, inits);
    }

    public QChatRoomMember(Class<? extends ChatRoomMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new QChatRoom(forProperty("chatRoom"), inits.get("chatRoom")) : null;
        this.member = inits.isInitialized("member") ? new com.cupid.qufit.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

