package ie.sds.resultsDiscovery.service;

import ie.sds.resultsDiscovery.core.CallPatientWorkItem;
import ie.sds.resultsDiscovery.core.Patient;
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
    public void add(CallPatientWorkItem workItem) {
        template.convertAndSend(workItem);
    }

    @Override
    public void add(Patient patient) {
        CallPatientWorkItem workItem = new CallPatientWorkItem(patient);

        // pass this to JMS
    }

    @Override
    public CallPatientWorkItem remove() {
        return (CallPatientWorkItem) template.receiveAndConvert();
    }

    @Override
    public boolean isEmpty() {
        Boolean empty = template.browse((session, queueBrowser) -> !queueBrowser.getEnumeration().hasMoreElements());
        return (empty == null) ? false : empty;
    }
}
