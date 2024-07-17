package com.cupid.qufit.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeProfiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_profiles_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", unique = false)
    private Location location;

    @Nullable
    private Integer typeAgeMax;
    @Nullable
    private Integer typeAgeMin;

    @OneToMany(mappedBy = "typeHobby", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TypeHobby> typeHobbies = new ArrayList<>();

    @OneToMany(mappedBy = "typePersonality", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TypePersonality> typePersonalities = new ArrayList<>();

    @OneToMany(mappedBy = "typeMBTI", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TypeMBTI> typeMBTIs = new ArrayList<>();
}
