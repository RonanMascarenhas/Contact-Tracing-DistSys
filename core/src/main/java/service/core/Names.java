package service.core;

public interface Names {
    // Service Names - Note: these should be the same as the names specified in each service's bootstrap.properties file
    String RESULTS_DISCOVERY = "results-discovery";
    String PATIENT_INFO = "patient-info";
    String CONTACT_TRACING = "contact-tracing-service";
    String CONTACT_SERVICE = "contact-service";
    // todo fill in the rest of the service names here

    //
    String WEB_UI = "web-ui";   // probably not necessary => web-ui does not need to be 'discovered' like other services

    // Jms Queue Names
    String PATIENT_RESULTS_CALL_WI_QUEUE = "Patient_Results_Call_WorkItem_Queue";
    String CONTACT_TRACING_WI_QUEUE = "Contact_Tracing_WorkItem_Queue";
}
