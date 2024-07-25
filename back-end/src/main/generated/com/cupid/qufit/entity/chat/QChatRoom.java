package com.cupid.qufit.entity.chat;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatRoom is a Querydsl query type for ChatRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoom extends EntityPathBase<ChatRoom> {

    private static final long serialVersionUID = -210848415L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatRoom chatRoom = new QChatRoom("chatRoom");

    public final ListPath<ChatRoomMember, QChatRoomMember> chatRoomMembers = this.<ChatRoomMember, QChatRoomMember>createList("chatRoomMembers", ChatRoomMember.class, QChatRoomMember.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastMessage = createString("lastMessage");

    public final StringPath lastMessageId = createString("lastMessageId");

    public final DateTimePath<java.time.LocalDateTime> lastMessageTime = createDateTime("lastMessageTime", java.time.LocalDateTime.class);

    public final com.cupid.qufit.entity.QMember member1;

    public final com.cupid.qufit.entity.QMember member2;

    public final EnumPath<ChatRoomStatus> status = createEnum("status", ChatRoomStatus.class);

    public final DateTimePath<java.time.LocalDateTime> statusChangedAt = createDateTime("statusChangedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QChatRoom(String variable) {
        this(ChatRoom.class, forVariable(variable), INITS);
    }

    public QChatRoom(Path<? extends ChatRoom> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatRoom(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatRoom(PathMetadata metadata, PathInits inits) {
        this(ChatRoom.class, metadata, inits);
    }

    public QChatRoom(Class<? extends ChatRoom> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member1 = inits.isInitialized("member1") ? new com.cupid.qufit.entity.QMember(forProperty("member1"), inits.get("member1")) : null;
        this.member2 = inits.isInitialized("member2") ? new com.cupid.qufit.entity.QMember(forProperty("member2"), inits.get("member2")) : null;
    }

}

