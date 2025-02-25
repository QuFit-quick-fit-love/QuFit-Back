package com.cupid.qufit.domain.balancegame.service;

import com.cupid.qufit.domain.balancegame.dto.BalanceGameResult;
import com.cupid.qufit.domain.balancegame.dto.SaveChoice;
import com.cupid.qufit.domain.balancegame.dto.SaveChoice.Request;
import com.cupid.qufit.domain.balancegame.repository.BalanceGameChoiceRepository;
import com.cupid.qufit.domain.balancegame.repository.BalanceGameRepository;
import com.cupid.qufit.domain.member.repository.profiles.MemberRepository;
import com.cupid.qufit.domain.video.repository.VideoRoomRepository;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.balancegame.BalanceGame;
import com.cupid.qufit.entity.balancegame.BalanceGameChoice;
import com.cupid.qufit.entity.video.VideoRoom;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.BalanceGameException;
import com.cupid.qufit.global.exception.exceptionType.MemberException;
import com.cupid.qufit.global.exception.exceptionType.VideoException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceGameServiceImpl implements BalanceGameService {

    private final BalanceGameRepository balanceGameRepository;
    private final BalanceGameChoiceRepository balanceGameChoiceRepository;
    private final VideoRoomRepository videoRoomRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<BalanceGame> getRandomBalanceGameList() {
        return balanceGameRepository.findRandomBalanceGames();
    }

    @Override
    public List<BalanceGame> getAllBalanceGameList() {
        return balanceGameRepository.findAll();
    }

    @Override
    public SaveChoice.Response saveChoice(Long memberId, Request saveChoiceRequest) {
        // memberId로 Member를 찾아오기. 없으면 MemberException 발생
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // saveChoiceRequest로 balanceGameId로 BalanceGame을 찾아오기. 없으면 BalanceGameException 발생
        BalanceGame balanceGame = balanceGameRepository.findById(saveChoiceRequest.getBalanceGameId())
                                                       .orElseThrow(() -> new BalanceGameException(
                                                               ErrorCode.BALANCE_GAME_NOT_FOUND));

        // saveChoiceRequest로 videoRoomId로 VideoRoom을 찾아오기. 없으면 VideoException 발생
        VideoRoom videoRoom = videoRoomRepository.findById(saveChoiceRequest.getVideoRoomId())
                                                 .orElseThrow(() -> new VideoException(
                                                         ErrorCode.VIDEO_ROOM_NOT_FOUND));

        String choiceContent = "";

        if (saveChoiceRequest.getChoiceNum() == 1) {
            choiceContent = balanceGame.getScenario1();
        } else if (saveChoiceRequest.getChoiceNum() == 2) {
            choiceContent = balanceGame.getScenario2();
        }

        // 새로운 BalanceGameChoice 객체 생성
        BalanceGameChoice newBalanceGameChoice = BalanceGameChoice.builder()
                                                                  .member(member)
                                                                  .videoRoom(videoRoom)
                                                                  .balanceGame(balanceGame)
                                                                  .choiceNum(saveChoiceRequest.getChoiceNum())
                                                                  .choiceContent(choiceContent)
                                                                  .build();

        // 생성한 BalanceGameChoice를 저장
        BalanceGameChoice saveChoice = balanceGameChoiceRepository.save(newBalanceGameChoice);

        // 저장한 BalanceGameChoice를 Response 객체로 변환하여 반환
        return SaveChoice.Response.builder()
                                  .balanceGameChoiceId(saveChoice.getBalanceGameChoiceId())
                                  .balanceGameId(saveChoice.getBalanceGame().getBalanceGameId())
                                  .memberId(saveChoice.getMember().getId())
                                  .videoRoomId(saveChoice.getVideoRoom().getVideoRoomId())
                                  .choiceNum(saveChoice.getChoiceNum())
                                  .choiceContent(saveChoice.getChoiceContent())
                                  .build();
    }

    @Override
    public void deleteAllChoice(Long videoRoomId) {
        balanceGameChoiceRepository.deleteByVideoRoom(videoRoomId);
    }

    @Override
    public List<BalanceGameResult> getBalanceGameResultByVideoRoomId(Long videoRoomId) {
        List<BalanceGameChoice> allByVideoRoom = balanceGameChoiceRepository.findAllByVideoRoomIdOrderByMember(
                videoRoomId);

        if (allByVideoRoom.isEmpty()) {
            throw new BalanceGameException(ErrorCode.RESULT_NOT_FOUND);
        }

        return allByVideoRoom.stream()
                             .map(BalanceGameResult::toBalanceGameResult)
                             .toList();
    }

}
