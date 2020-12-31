package ie.sds.resultsDiscovery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.JmsDestinationAccessor;
import org.springframework.stereotype.Service;
import service.core.Patient;
import service.messages.PatientResultCallWorkItem;

import javax.jms.ConnectionFactory;

@Service
public class JmsPatientResultsCallQueue implements PatientResultsCallQueue {
    private static final int HIGH_PRIORITY = 9;
    private static final int REGULAR_PRIORITY = 4;

    private final JmsTemplate template;

    @Autowired
    public JmsPatientResultsCallQueue(@Qualifier("activeMQConnectionFactory") ConnectionFactory connectionFactory) {
        template = new JmsTemplate(connectionFactory);

        // todo extract the string to core
        template.setDefaultDestinationName("PatientResultsCallWorkItem_Queue");
        template.setReceiveTimeout(JmsDestinationAccessor.RECEIVE_TIMEOUT_NO_WAIT);
    }

    @Override
    public void add(PatientResultCallWorkItem workItem) {
        template.convertAndSend(workItem);
    }

    @Override
    public void add(Patient patient) {
        PatientResultCallWorkItem workItem = new PatientResultCallWorkItem(patient);
        add(workItem);
    }

    @Override
    public void addWithPriority(PatientResultCallWorkItem workItem) {
        setHighPriority();
        add(workItem);
        setRegularPriority();
    }

    /**
     * This is a blocking operation. Care should be taken to ensure that there is an item in the queue before calling
     * this method. This method will not wait for a message to appear in an empty queue.
     *
     * @return A {@code PatientResultWorkItem} from the Queue
     */
    @Override
    public PatientResultCallWorkItem remove() {
        return (PatientResultCallWorkItem) template.receiveAndConvert();
    }

    @Override
    public boolean isEmpty() {
        Boolean empty = template.browse((session, queueBrowser) -> !queueBrowser.getEnumeration().hasMoreElements());
        return (empty == null) ? false : empty;
    }

    private void setRegularPriority() {
        if (template.getPriority() != REGULAR_PRIORITY) {
            template.setPriority(REGULAR_PRIORITY);
        }
    }

    private void setHighPriority() {
        if (template.getPriority() != HIGH_PRIORITY) {
            template.setPriority(HIGH_PRIORITY);
        }
    }
}
