package com.cupid.qufit.entity.video;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "chat_rooms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VideoChatRoom {

    @Id
    private String roomId;
    private List<Long> participants = new ArrayList<>();

    public VideoChatRoom(String roomId) {
        this.roomId = roomId;
    }

    public int getParticipantsCount() {
        return participants.size();
    }

    public void addParticipant(Long participantId) {
        if (!participants.contains(participantId)) {
            participants.add(participantId);
        }
    }

    public void removeParticipant(Long participantId) {
        participants.remove(participantId);
    }
}
