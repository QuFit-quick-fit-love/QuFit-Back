package com.cupid.qufit.entity.video;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVideoRoomParticipant is a Querydsl query type for VideoRoomParticipant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVideoRoomParticipant extends EntityPathBase<VideoRoomParticipant> {

    private static final long serialVersionUID = 95021304L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVideoRoomParticipant videoRoomParticipant = new QVideoRoomParticipant("videoRoomParticipant");

    public final StringPath connectionId = createString("connectionId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> joinedAt = createDateTime("joinedAt", java.time.LocalDateTime.class);

    public final com.cupid.qufit.entity.QMember member;

    public final QVideoRoom videoRoom;

    public QVideoRoomParticipant(String variable) {
        this(VideoRoomParticipant.class, forVariable(variable), INITS);
    }

    public QVideoRoomParticipant(Path<? extends VideoRoomParticipant> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVideoRoomParticipant(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVideoRoomParticipant(PathMetadata metadata, PathInits inits) {
        this(VideoRoomParticipant.class, metadata, inits);
    }

    public QVideoRoomParticipant(Class<? extends VideoRoomParticipant> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.cupid.qufit.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.videoRoom = inits.isInitialized("videoRoom") ? new QVideoRoom(forProperty("videoRoom")) : null;
    }

}

