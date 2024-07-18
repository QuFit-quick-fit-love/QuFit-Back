package com.cupid.qufit.domain.member.dto;

import com.cupid.qufit.entity.Member;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class PrincipalDetails implements OAuth2User, UserDetails {

    private Member member;
    private Map<String, Object> attributes;

    public PrincipalDetails(Member member) {this.member = member;}
    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getNickname();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getKey()));
    }

    @Override
    public String getName() {
        return member.getNickname();
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> map = new HashMap<>();

        map.put("email", member.getEmail());
        map.put("nickname",member.getNickname());
        map.put("roleNames",member.getRole().getKey());

        return map;
    }
}
