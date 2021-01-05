package ie.sds.resultsDiscovery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import service.core.Names;
import service.core.Patient;
import service.messages.PatientResultCallWorkItem;

import javax.jms.ConnectionFactory;

@Service
public class JmsPatientResultsCallQueue implements PatientResultsCallQueue {
    private static final long FIVE_SECOND_TIMEOUT = 5 * 1000;

    private final JmsTemplate template;

    @Autowired
    public JmsPatientResultsCallQueue(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
        template = new JmsTemplate(connectionFactory);
        template.setDefaultDestinationName(Names.PATIENT_RESULTS_CALL_WI_QUEUE);
        template.setReceiveTimeout(FIVE_SECOND_TIMEOUT);
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
}
