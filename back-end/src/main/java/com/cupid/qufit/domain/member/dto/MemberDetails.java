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

public class MemberDetails implements UserDetails {

    private final Member member;
    public MemberDetails(Member member) {this.member = member;}

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getNickname();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getKey()));
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> map = new HashMap<>();

        map.put("email", member.getEmail());
        map.put("nickname",member.getNickname());
        map.put("roleNames",member.getRole().getKey());

        return map;
    }
}
