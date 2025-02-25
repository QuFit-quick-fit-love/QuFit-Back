package com.cupid.qufit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberPersonality is a Querydsl query type for MemberPersonality
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberPersonality extends EntityPathBase<MemberPersonality> {

    private static final long serialVersionUID = 110144108L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberPersonality memberPersonality = new QMemberPersonality("memberPersonality");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final QTag tag;

    public QMemberPersonality(String variable) {
        this(MemberPersonality.class, forVariable(variable), INITS);
    }

    public QMemberPersonality(Path<? extends MemberPersonality> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberPersonality(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberPersonality(PathMetadata metadata, PathInits inits) {
        this(MemberPersonality.class, metadata, inits);
    }

    public QMemberPersonality(Class<? extends MemberPersonality> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag")) : null;
    }

}

