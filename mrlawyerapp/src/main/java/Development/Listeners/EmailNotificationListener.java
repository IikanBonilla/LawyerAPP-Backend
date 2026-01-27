package Development.Listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import Development.Events.ProceedingCreatedEvent;
import Development.Services.EmailServices;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotificationListener {
    
    @Autowired
    private EmailServices emailService;

    @EventListener
    public void handleProceedingCreated(ProceedingCreatedEvent event){

        emailService.sendSimpleMessage(event.getEmailBody());

        System.out.println("Email enviado para la actuación:  " + event.getProceeding().getId());
    }


}
