package com.example.assignment;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletRequest {
    private int amount;
    private String userName;
}
