package com.cupid.qufit.domain.friend.service;

import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface FriendService {

    void addFriend(Long memberId, Long friendId);

    void deleteFriend(Long memberId, Long friendId);

    Map<String, Object> getFriends(Long memberId, Pageable pageable);
}
