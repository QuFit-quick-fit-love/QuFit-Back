package com.cupid.qufit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1824149854L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final StringPath bio = createString("bio");

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    public final ListPath<com.cupid.qufit.entity.chat.ChatRoom, com.cupid.qufit.entity.chat.QChatRoom> chatRoomAsMember1 = this.<com.cupid.qufit.entity.chat.ChatRoom, com.cupid.qufit.entity.chat.QChatRoom>createList("chatRoomAsMember1", com.cupid.qufit.entity.chat.ChatRoom.class, com.cupid.qufit.entity.chat.QChatRoom.class, PathInits.DIRECT2);

    public final ListPath<com.cupid.qufit.entity.chat.ChatRoom, com.cupid.qufit.entity.chat.QChatRoom> chatRoomAsMember2 = this.<com.cupid.qufit.entity.chat.ChatRoom, com.cupid.qufit.entity.chat.QChatRoom>createList("chatRoomAsMember2", com.cupid.qufit.entity.chat.ChatRoom.class, com.cupid.qufit.entity.chat.QChatRoom.class, PathInits.DIRECT2);

    public final ListPath<com.cupid.qufit.entity.chat.ChatRoomMember, com.cupid.qufit.entity.chat.QChatRoomMember> chatRoomMembers = this.<com.cupid.qufit.entity.chat.ChatRoomMember, com.cupid.qufit.entity.chat.QChatRoomMember>createList("chatRoomMembers", com.cupid.qufit.entity.chat.ChatRoomMember.class, com.cupid.qufit.entity.chat.QChatRoomMember.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final StringPath gender = createString("gender");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QLocation location;

    public final QTag MBTI;

    public final ListPath<MemberHobby, QMemberHobby> memberHobbies = this.<MemberHobby, QMemberHobby>createList("memberHobbies", MemberHobby.class, QMemberHobby.class, PathInits.DIRECT2);

    public final ListPath<MemberPersonality, QMemberPersonality> memberPersonalities = this.<MemberPersonality, QMemberPersonality>createList("memberPersonalities", MemberPersonality.class, QMemberPersonality.class, PathInits.DIRECT2);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath profileImage = createString("profileImage");

    public final EnumPath<MemberRole> role = createEnum("role", MemberRole.class);

    public final EnumPath<MemberStatus> status = createEnum("status", MemberStatus.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.location = inits.isInitialized("location") ? new QLocation(forProperty("location")) : null;
        this.MBTI = inits.isInitialized("MBTI") ? new QTag(forProperty("MBTI")) : null;
    }

}

