package com.cupid.qufit.entity.video;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVideoRoom is a Querydsl query type for VideoRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVideoRoom extends EntityPathBase<VideoRoom> {

    private static final long serialVersionUID = 538831675L;

    public static final QVideoRoom videoRoom = new QVideoRoom("videoRoom");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> curMCount = createNumber("curMCount", Integer.class);

    public final NumberPath<Integer> curWCount = createNumber("curWCount", Integer.class);

    public final NumberPath<Integer> maxParticipants = createNumber("maxParticipants", Integer.class);

    public final ListPath<VideoRoomParticipant, QVideoRoomParticipant> participants = this.<VideoRoomParticipant, QVideoRoomParticipant>createList("participants", VideoRoomParticipant.class, QVideoRoomParticipant.class, PathInits.DIRECT2);

    public final StringPath sessionId = createString("sessionId");

    public final EnumPath<VideoRoomStatus> status = createEnum("status", VideoRoomStatus.class);

    public final NumberPath<Long> videoRoomId = createNumber("videoRoomId", Long.class);

    public final StringPath videoRoomName = createString("videoRoomName");

    public QVideoRoom(String variable) {
        super(VideoRoom.class, forVariable(variable));
    }

    public QVideoRoom(Path<? extends VideoRoom> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVideoRoom(PathMetadata metadata) {
        super(VideoRoom.class, metadata);
    }

}

