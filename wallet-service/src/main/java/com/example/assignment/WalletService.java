package com.example.assignment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @KafkaListener(topics = {"create_wallet"}, groupId = "assignment") //listening from this topic


    public void createWallet(String message) throws JsonProcessingException {

        JSONObject jsonObject = objectMapper.readValue(message, JSONObject.class);

        String userName = (String) jsonObject.get("username");

        Wallet wallet = Wallet.builder()
                .userName(userName)
                .balance(1000)
                .build();
        walletRepository.save(wallet);
    }
//
//    Wallet incrementWallet(String userName, int amount)
//    {
//        Wallet oldWallet = walletRepository.findByUserName(userName); //get the oldwallet
//        int creditedAmount = amount + oldWallet.getAmount(); //increment amount
//        Wallet newWallet = Wallet.builder()
//                .userName(userName)
//                .amount(creditedAmount)
//                .build();
//        walletRepository.save(newWallet);
//        return newWallet;
//
//        //method 2: using sql query
//        //int credit = walletRpository.updateWallet(userName, amount);
//    }
//
//    Wallet decrementWallet(String userName, int amount)
//    {
//        Wallet oldWallet = walletRepository.findByUserName(userName);
//        int creditedAmount = amount - oldWallet.getAmount();
//        Wallet newWallet = Wallet.builder()
//                .id(oldWallet.getId())
//                .userName(userName)
//                .amount(creditedAmount)
//                .build();
//        walletRepository.save(newWallet);
//        return newWallet;
//
//        //method 2: using sql query
//        //int debit = walletRpository.updateWallet(userName, amount);
//    }

    @KafkaListener(topics = {"create_transaction"}, groupId = "assignment")
    public void updateWallet(String message) throws JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(message,JSONObject.class);

        //get all the values
        String fromUser = (String) jsonObject.get("fromUser");
        String toUser = (String) jsonObject.get("toUser");
        int amount = (int) jsonObject.get("amount");
        String transactionId = (String) jsonObject.get("transactionId");

        //get sender's wallet
        Wallet sender = walletRepository.findByUserName(fromUser);
        int balance = sender.getBalance();

        JSONObject transactionObject = new JSONObject();

        if(balance >= amount) {

            Wallet fromWallet = walletRepository.findByUserName(fromUser);
            fromWallet.setBalance(balance - amount);
            walletRepository.save(fromWallet);

            Wallet toWallet = walletRepository.findByUserName(toUser);
            toWallet.setBalance(balance + amount);
            walletRepository.save(toWallet);

            transactionObject.put("status","SUCCESS");
            transactionObject.put("transactionId",transactionId);
        }
        else{
            transactionObject.put("status","FAILED");
            transactionObject.put("transactionId",transactionId);
        }

        //send it to kafka
        String acknowledgement = transactionObject.toString();
        kafkaTemplate.send("update_transaction",acknowledgement);

    }
}

