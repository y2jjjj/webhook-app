package com.bfhl.webhookapp.service;

import com.bfhl.webhookapp.model.SolutionRequest;
import com.bfhl.webhookapp.model.WebhookRequest;
import com.bfhl.webhookapp.model.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    private static final String GENERATE_WEBHOOK_URL = 
        "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String SUBMIT_SOLUTION_URL = 
        "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    
    @Autowired
    private RestTemplate restTemplate;
    
    @EventListener(ApplicationReadyEvent.class)
    public void executeOnStartup() {
        logger.info("Application started, beginning webhook flow...");
        
        try {
            // Step 1: Generate webhook
            WebhookResponse webhookResponse = generateWebhook();
            logger.info("Webhook generated successfully");
            logger.info("Webhook URL: {}", webhookResponse.getWebhook());
            
            // Step 2: Get the SQL solution
            String sqlQuery = getSqlQuery();
            logger.info("SQL Query prepared: {}", sqlQuery);
            
            // Step 3: Submit solution
            submitSolution(webhookResponse.getAccessToken(), sqlQuery);
            logger.info("Solution submitted successfully!");
            
        } catch (Exception e) {
            logger.error("Error in webhook flow: ", e);
        }
    }
    
    private WebhookResponse generateWebhook() {
        WebhookRequest request = new WebhookRequest(
            "Yuvraj Kumar",             
            "22BCE2394",               
            "yuvraj.kr2022@gmail.com"  
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
        
        return restTemplate.postForObject(
            GENERATE_WEBHOOK_URL, 
            entity, 
            WebhookResponse.class
        );
    }
    
    private String getSqlQuery() {
        return "WITH HighEarners AS ( " +
               "SELECT DISTINCT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, e.DOB, e.DEPARTMENT, " +
               "d.DEPARTMENT_NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE " +
               "FROM EMPLOYEE e " +
               "INNER JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
               "INNER JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID " +
               "WHERE p.AMOUNT > 70000 ), " +
               "RankedEmployees AS ( " +
               "SELECT DEPARTMENT, DEPARTMENT_NAME, EMP_ID, FIRST_NAME, LAST_NAME, AGE, " +
               "ROW_NUMBER() OVER (PARTITION BY DEPARTMENT ORDER BY EMP_ID) AS rn " +
               "FROM HighEarners ) " +
               "SELECT DEPARTMENT_NAME, ROUND(AVG(AGE), 2) AS AVERAGE_AGE, " +
               "GROUP_CONCAT(CONCAT(FIRST_NAME, ' ', LAST_NAME) ORDER BY EMP_ID SEPARATOR ', ') AS EMPLOYEE_LIST " +
               "FROM RankedEmployees " +
               "WHERE rn <= 10 " +
               "GROUP BY DEPARTMENT, DEPARTMENT_NAME " +
               "ORDER BY DEPARTMENT DESC";
    }
    
    private void submitSolution(String accessToken, String sqlQuery) {
        SolutionRequest request = new SolutionRequest(sqlQuery);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        
        HttpEntity<SolutionRequest> entity = new HttpEntity<>(request, headers);
        
        String response = restTemplate.postForObject(
            SUBMIT_SOLUTION_URL,
            entity,
            String.class
        );
        
        logger.info("Submission response: {}", response);
    }
}
