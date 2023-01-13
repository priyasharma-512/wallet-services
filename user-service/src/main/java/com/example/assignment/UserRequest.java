package com.example.assignment;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    private String userName;
    private String name;
    private int age;
    private String email;
}
