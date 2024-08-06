package com.cupid.qufit.domain.friend.service;

public interface FriendService {

    void addFriend(Long memberId, Long friendId);

    void deleteFriend(Long memberId, Long friendId);
}
