package com.example.assignment;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {

    private String fromUser;
    private String toUser;
    private int amount;
}
