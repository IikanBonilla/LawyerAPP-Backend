package Development.Events;

import Development.DTOs.EmailBodyDTO;
import Development.Model.Proceeding;
import lombok.Getter;

import org.springframework.context.ApplicationEvent;

@Getter
public class ProceedingCreatedEvent extends ApplicationEvent{
    private final Proceeding proceeding;
    private final EmailBodyDTO emailBody;
    public ProceedingCreatedEvent(Object source, Proceeding proceeding, EmailBodyDTO emailBody){
        super(source);
        this.proceeding = proceeding;
        this.emailBody = emailBody;
    }

}
