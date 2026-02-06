package Development.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import Development.DTOs.NotificationEventDTO;

@Service
public class NotificationServices {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotificationAccount(String idLawyer, String typeEvent, Object payload){

        NotificationEventDTO event = NotificationEventDTO.builder()
            .type(typeEvent)
            .data(payload)
            .build();

        messagingTemplate.convertAndSend(
            "/topic/lawyer/" + idLawyer,
            event
        );
    }

    public void sendNotificationAdmin(String idLawfirm, String typeEvent, Object payload){

        NotificationEventDTO event = NotificationEventDTO.builder()
            .type(typeEvent)
            .data(payload)
            .build();

        messagingTemplate.convertAndSend(
            "/topic/lawfirm/" + idLawfirm,
            event
        );
    }
}
