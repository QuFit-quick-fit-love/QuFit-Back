package com.cupid.qufit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTypePersonality is a Querydsl query type for TypePersonality
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTypePersonality extends EntityPathBase<TypePersonality> {

    private static final long serialVersionUID = -1948327988L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTypePersonality typePersonality = new QTypePersonality("typePersonality");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QTag tag;

    public final QTypeProfiles typeProfiles;

    public QTypePersonality(String variable) {
        this(TypePersonality.class, forVariable(variable), INITS);
    }

    public QTypePersonality(Path<? extends TypePersonality> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTypePersonality(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTypePersonality(PathMetadata metadata, PathInits inits) {
        this(TypePersonality.class, metadata, inits);
    }

    public QTypePersonality(Class<? extends TypePersonality> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag")) : null;
        this.typeProfiles = inits.isInitialized("typeProfiles") ? new QTypeProfiles(forProperty("typeProfiles"), inits.get("typeProfiles")) : null;
    }

}

