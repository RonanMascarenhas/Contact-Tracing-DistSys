package ie.sds.resultsDiscovery;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.jms.ConnectionFactory;

@SpringBootApplication
public class ResultsDiscoveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResultsDiscoveryApplication.class, args);
    }

    // Configuration for ActiveMQ
    @Bean
    public ConnectionFactory activeMQConnectionFactory(Environment env) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(env.getProperty("spring.activemq.broker-url"));
        factory.setTrustAllPackages(true);  // todo fix to only trust required packages
        return factory;
    }
}
