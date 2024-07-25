package com.cupid.qufit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTypeMBTI is a Querydsl query type for TypeMBTI
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTypeMBTI extends EntityPathBase<TypeMBTI> {

    private static final long serialVersionUID = -688478452L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTypeMBTI typeMBTI = new QTypeMBTI("typeMBTI");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QTag tag;

    public final QTypeProfiles typeProfiles;

    public QTypeMBTI(String variable) {
        this(TypeMBTI.class, forVariable(variable), INITS);
    }

    public QTypeMBTI(Path<? extends TypeMBTI> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTypeMBTI(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTypeMBTI(PathMetadata metadata, PathInits inits) {
        this(TypeMBTI.class, metadata, inits);
    }

    public QTypeMBTI(Class<? extends TypeMBTI> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag")) : null;
        this.typeProfiles = inits.isInitialized("typeProfiles") ? new QTypeProfiles(forProperty("typeProfiles"), inits.get("typeProfiles")) : null;
    }

}

