package ie.sds.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import service.core.Names;
import service.core.Patient;
import service.dns.DomainNameService;
import service.exception.NoSuchServiceException;
import service.messages.ContactTracingWorkItem;

import javax.jms.ConnectionFactory;
import java.net.URI;
import java.util.List;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfiguration {
    public static final long DELAY = 30000;
    public static final String JOB_NAME = "scheduleContactTracingJob";

    private final JobLauncher jobLauncher;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    private final ApplicationContext context;

    @Autowired
    public BatchConfiguration(
            JobLauncher jobLauncher, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
            JobExplorer jobExplorer, JobOperator jobOperator, ApplicationContext context
    ) {
        this.jobLauncher = jobLauncher;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobExplorer = jobExplorer;
        this.jobOperator = jobOperator;
        this.context = context;
    }

    @Scheduled(initialDelay = DELAY, fixedRate = DELAY)
    public void run() throws Exception {
        Step jobStep = context.getBean("processPatientStep", Step.class);

        List<JobInstance> lastInstances = jobExplorer.getJobInstances(JOB_NAME, 0, 1);
        if (lastInstances.isEmpty()) {
            jobLauncher.run(scheduleJob(jobStep), new JobParameters());
        } else {
            jobOperator.startNextInstance(JOB_NAME);
        }
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
        return jobBuilderFactory.get(JOB_NAME)
                .flow(processPatient)
                .end()
                .build();
    }

    @Bean
    public Step processPatientStep(RestPatientReader restPatientReader, WorkItemQueuePusher queuePusher) {
        return stepBuilderFactory.get("schedulePatient")
                .<Patient, ContactTracingWorkItem>chunk(10)
                .reader(restPatientReader)
                .processor(processor())
                .writer(queuePusher)
                .build();
    }
}
