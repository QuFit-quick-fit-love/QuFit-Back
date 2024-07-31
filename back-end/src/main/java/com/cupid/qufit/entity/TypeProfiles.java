package com.cupid.qufit.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DynamicUpdate
public class TypeProfiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_profiles_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = false)
    private Member member;

    @Nullable
    private Integer typeAgeMax;
    @Nullable
    private Integer typeAgeMin;

    @OneToMany(mappedBy = "typeProfiles", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TypeHobby> typeHobbies = new HashSet<>();

    @OneToMany(mappedBy = "typeProfiles", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TypePersonality> typePersonalities = new HashSet<>();

    @OneToMany(mappedBy = "typeProfiles", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TypeMBTI> typeMBTIs = new HashSet<>();

    // 연관관계 메소드
    public void addTypeHobbies(TypeHobby typeHobby) {
        this.typeHobbies.add(typeHobby);
        typeHobby.setTypeProfiles(this);
    }

    public void addTypePersonalities(TypePersonality typePersonality) {
        this.typePersonalities.add(typePersonality);
        typePersonality.setTypeProfiles(this);
    }

    public void addtypeMBTIs(TypeMBTI typeMBTI) {
        this.typeMBTIs.add(typeMBTI);
        typeMBTI.setTypeProfiles(this);
    }

    public void updateTyeAgeMax(Integer typeAgeMax) {
        this.typeAgeMax = typeAgeMax;
    }

    public void updateTypeAgeMin(Integer typeAgeMin) {
        this.typeAgeMin = typeAgeMin;
    }
}
