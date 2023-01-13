package com.example.assignment;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="users")
@ToString //instead of printing the addresses printing the exact attributes and values
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //primary key

    @Column(unique = true)
    private String userName; //since userName is unique we can use this as PK
    private String name;
    private int age;
    private String email;
}
