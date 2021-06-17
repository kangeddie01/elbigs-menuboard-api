package com.elbigs.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InitialSearchEntity extends BaseEntity {
    private Long id;
    private String expressLetter;
}
