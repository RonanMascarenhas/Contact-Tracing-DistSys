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

import javax.jms.ConnectionFactory;
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
        // todo get rid of this
        apiUrl = "http://localhost:8087/patients";

        // todo this should add the correct endpoint to the URL here or inside the RestPatientReader, once this is finalized
        return new RestPatientReader(apiUrl, new RestTemplate());
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
    public Step processPatientStep(WorkItemQueuePusher queuePusher) {
        return stepBuilderFactory.get("schedulePatient")
                .<Patient, CallPatientWorkItem>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(queuePusher)
                .build();
    }
}
