package com.cupid.qufit.domain.member.dto;

import com.cupid.qufit.entity.Member;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MemberDetails implements UserDetails {

    private final Member member;

    public MemberDetails(Member member) {
        this.member = member;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return member.getNickname();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getKey()));
    }

    public Long getId() {
        return member.getId();
    }

    public String getEmail() {
        return member.getEmail();
    }

    public String getProfileImage() {
        return member.getProfileImage();
    }

    public String getGender() {
        return member.getGender();
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> map = new HashMap<>();

        map.put("id", member.getId());
        map.put("email", member.getEmail());

        return map;
    }
}
