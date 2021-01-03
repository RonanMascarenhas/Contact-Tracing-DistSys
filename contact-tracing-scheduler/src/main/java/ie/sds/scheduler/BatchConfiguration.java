package ie.sds.scheduler;

import ie.sds.core.CallPatientWorkItem;
import ie.sds.core.Patient;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestTemplate;
import service.core.Names;
import service.dns.DomainNameService;
import service.exception.NoSuchServiceException;

import javax.jms.ConnectionFactory;
import java.net.URI;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    public JobBuilderFactory jobBuilderFactory;
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public RestPatientReader reader(DomainNameService dns) throws NoSuchServiceException {
        URI patientInfoURI = dns.find(Names.PATIENT_INFO)
                .orElseThrow(dns.getServiceNotFoundSupplier(Names.PATIENT_INFO));

        String patientsInfoEndpoint = URI.create(patientInfoURI + "/patientinfo/listpatients?ct=false").toString();
        return new RestPatientReader(patientsInfoEndpoint, new RestTemplate());
    }

    @Bean
    public PatientCallScheduler processor() {
        return new PatientCallScheduler();
    }

    @Bean
    public WorkItemQueuePusher writer(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setDefaultDestinationName("CallPatientWorkItem_Queue");
        return new WorkItemQueuePusher(template);
    }

    // todo add in a JobCompletionNotificationListener (https://spring.io/guides/gs/batch-processing/)
    @Bean
    public Job scheduleJob(Step processPatient) {
        return jobBuilderFactory.get("scheduleContactTracingJob")
                .flow(processPatient)
                .end()
                .build();
    }

    @Bean
    public Step processPatientStep(WorkItemQueuePusher queuePusher, RestPatientReader restPatientReader) {
        return stepBuilderFactory.get("schedulePatient")
                .<Patient, CallPatientWorkItem>chunk(10)
                .reader(restPatientReader)
                .processor(processor())
                .writer(queuePusher)
                .build();
    }
}
