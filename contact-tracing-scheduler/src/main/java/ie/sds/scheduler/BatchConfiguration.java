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

import java.util.Scanner;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    public static final String apiURLFileName = "/apiUrl.txt";

    public JobBuilderFactory jobBuilderFactory;
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public RestPatientReader reader() {
        // get the apiURL for the Patient-Info Service
        // todo change this to get the URL from Eureka
        //  currently fetches from a file
        String apiUrl = "";
        try (Scanner scan = new Scanner(apiURLFileName)) {
            apiUrl = scan.nextLine();
        }

        return new RestPatientReader(apiUrl, new RestTemplate());
    }

    @Bean
    public PatientCallScheduler processor() {
        return new PatientCallScheduler();
    }

    @Bean
    public WorkItemQueuePusher writer(@Qualifier("cpwiQueueJmsTemplate") JmsTemplate template) {
        // todo tidy this. Do we need to provide the custom cpwiQueueJmsTemplate?
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
    public Step processPatientStep(WorkItemQueuePusher queuePusher) {
        return stepBuilderFactory.get("schedulePatient")
                .<Patient, CallPatientWorkItem>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(queuePusher)
                .build();
    }
}
