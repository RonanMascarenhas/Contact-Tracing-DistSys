package ie.sds.resultsDiscovery.service;

import ie.sds.resultsDiscovery.core.Patient;
import ie.sds.resultsDiscovery.core.PatientResultWorkItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.ConnectionFactory;

@Service
public class JmsPatientResultsCallQueue implements PatientResultsCallQueue {
    private final JmsTemplate template;

    @Autowired
    public JmsPatientResultsCallQueue(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
        template = new JmsTemplate(connectionFactory);

        // todo extract the string to core
        template.setDefaultDestinationName("PatientResultsCallWorkItem_Queue");
    }

    @Override
    public void add(PatientResultWorkItem workItem) {
        template.convertAndSend(workItem);
    }

    @Override
    public void add(Patient patient) {
        PatientResultWorkItem workItem = new PatientResultWorkItem(patient);

        // pass this to JMS
    }

    @Override
    public PatientResultWorkItem remove() {
        return (PatientResultWorkItem) template.receiveAndConvert();
    }

    @Override
    public boolean isEmpty() {
        Boolean empty = template.browse((session, queueBrowser) -> !queueBrowser.getEnumeration().hasMoreElements());
        return (empty == null) ? false : empty;
    }
}
