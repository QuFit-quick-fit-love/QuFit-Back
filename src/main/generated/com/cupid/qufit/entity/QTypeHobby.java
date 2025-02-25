package com.cupid.qufit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTypeHobby is a Querydsl query type for TypeHobby
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTypeHobby extends EntityPathBase<TypeHobby> {

    private static final long serialVersionUID = 128741808L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTypeHobby typeHobby = new QTypeHobby("typeHobby");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QTag tag;

    public final QTypeProfiles typeProfiles;

    public QTypeHobby(String variable) {
        this(TypeHobby.class, forVariable(variable), INITS);
    }

    public QTypeHobby(Path<? extends TypeHobby> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTypeHobby(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTypeHobby(PathMetadata metadata, PathInits inits) {
        this(TypeHobby.class, metadata, inits);
    }

    public QTypeHobby(Class<? extends TypeHobby> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag")) : null;
        this.typeProfiles = inits.isInitialized("typeProfiles") ? new QTypeProfiles(forProperty("typeProfiles"), inits.get("typeProfiles")) : null;
    }

}

