package com.cupid.qufit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTypeProfiles is a Querydsl query type for TypeProfiles
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTypeProfiles extends EntityPathBase<TypeProfiles> {

    private static final long serialVersionUID = -257965236L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTypeProfiles typeProfiles = new QTypeProfiles("typeProfiles");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final NumberPath<Integer> typeAgeMax = createNumber("typeAgeMax", Integer.class);

    public final NumberPath<Integer> typeAgeMin = createNumber("typeAgeMin", Integer.class);

    public final ListPath<TypeHobby, QTypeHobby> typeHobbies = this.<TypeHobby, QTypeHobby>createList("typeHobbies", TypeHobby.class, QTypeHobby.class, PathInits.DIRECT2);

    public final ListPath<TypeMBTI, QTypeMBTI> typeMBTIs = this.<TypeMBTI, QTypeMBTI>createList("typeMBTIs", TypeMBTI.class, QTypeMBTI.class, PathInits.DIRECT2);

    public final ListPath<TypePersonality, QTypePersonality> typePersonalities = this.<TypePersonality, QTypePersonality>createList("typePersonalities", TypePersonality.class, QTypePersonality.class, PathInits.DIRECT2);

    public QTypeProfiles(String variable) {
        this(TypeProfiles.class, forVariable(variable), INITS);
    }

    public QTypeProfiles(Path<? extends TypeProfiles> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTypeProfiles(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTypeProfiles(PathMetadata metadata, PathInits inits) {
        this(TypeProfiles.class, metadata, inits);
    }

    public QTypeProfiles(Class<? extends TypeProfiles> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

