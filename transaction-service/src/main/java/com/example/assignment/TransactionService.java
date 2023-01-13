package com.example.assignment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    public void createTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = Transaction.builder()
                .fromUser(transactionRequest.getFromUser())
                .toUser(transactionRequest.getToUser())
                .amount(transactionRequest.getAmount())
                .status(TransactionStatus.PENDING)
                .transactionId(String.valueOf(UUID.randomUUID()))
                .transactionTime(String.valueOf(new Date()))
                .build();

        //save in db
        transactionRepository.save(transaction);

        //communicate with wallet-service
        //wallet is now producer and transaction is consumer
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fromUser", transactionRequest.getFromUser());
        jsonObject.put("toUser", transactionRequest.getToUser());
        jsonObject.put("amount", transactionRequest.getAmount());
        jsonObject.put("transactionId",transaction.getTransactionId());


        //convert it to message string
        String message = jsonObject.toString();
        kafkaTemplate.send("create_transaction",message);
    }

    @KafkaListener(topics={"update_transaction"}, groupId = "assignment")
    public void updateTransaction(String message) throws JsonProcessingException {
        JSONObject transactionRequest = objectMapper.readValue(message, JSONObject.class);

        //extract info from json
        String status = (String) transactionRequest.get("status");
        String transactionId = (String) transactionRequest.get("transactionId");

        Transaction transaction = transactionRepository.findByTransactionId(transactionId);

        //updating the status
        if(status == "SUCCESS")
        {
            transaction.setStatus(TransactionStatus.SUCCESS);
        }
        else {
            transaction.setStatus(TransactionStatus.FAILED);
        }

        //save it to transactionrepository
        transactionRepository.save(transaction);
    }
}
