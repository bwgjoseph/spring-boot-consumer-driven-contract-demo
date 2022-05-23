package com.bwgjoseph.pactconsumer;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private int id;
    private String name;
    private int age;
    private String email;
    private LocalDate dob;
}
