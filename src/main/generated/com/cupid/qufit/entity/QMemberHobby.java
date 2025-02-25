package com.cupid.qufit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberHobby is a Querydsl query type for MemberHobby
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberHobby extends EntityPathBase<MemberHobby> {

    private static final long serialVersionUID = -1280134064L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberHobby memberHobby = new QMemberHobby("memberHobby");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final QTag tag;

    public QMemberHobby(String variable) {
        this(MemberHobby.class, forVariable(variable), INITS);
    }

    public QMemberHobby(Path<? extends MemberHobby> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberHobby(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberHobby(PathMetadata metadata, PathInits inits) {
        this(MemberHobby.class, metadata, inits);
    }

    public QMemberHobby(Class<? extends MemberHobby> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag")) : null;
    }

}

