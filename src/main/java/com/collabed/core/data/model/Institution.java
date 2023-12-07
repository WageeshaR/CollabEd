package com.collabed.core.data.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Institution {
    @Id
    private String id;
    private String name;
//    private
}
