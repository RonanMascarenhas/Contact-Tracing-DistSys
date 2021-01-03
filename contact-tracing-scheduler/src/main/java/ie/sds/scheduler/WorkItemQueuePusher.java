package ie.sds.scheduler;

import org.springframework.batch.item.ItemWriter;
import org.springframework.jms.core.JmsTemplate;
import service.messages.ContactTracingWorkItem;

import java.util.List;


public class WorkItemQueuePusher implements ItemWriter<ContactTracingWorkItem> {
    private final JmsTemplate template;

    public WorkItemQueuePusher(JmsTemplate template) {
        this.template = template;
    }

    @Override
    public void write(List<? extends ContactTracingWorkItem> patientWorkItems) {
        for (ContactTracingWorkItem workItem : patientWorkItems) {
            template.convertAndSend(workItem);
        }
    }
}
