package ie.sds.scheduler;

import ie.sds.core.CallPatientWorkItem;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;


public class WorkItemQueuePusher implements ItemWriter<CallPatientWorkItem> {
    private final JmsTemplate template;

    public WorkItemQueuePusher(JmsTemplate template) {
        this.template = template;
    }

    @Override
    public void write(List<? extends CallPatientWorkItem> patientWorkItems) {
        for (CallPatientWorkItem workItem : patientWorkItems) {
            template.convertAndSend(workItem);
        }
    }
}
