package com.bwgjoseph.sccconsumer;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    private int id;
    private String name;
    private int age;
    private String email;
    private LocalDate dob;
}
