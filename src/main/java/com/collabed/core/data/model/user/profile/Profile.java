package com.collabed.core.data.model.user.profile;

import com.collabed.core.data.model.Institution;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class Profile {
    @Id
    private String id;
    @NotNull
    private TitleEnum title;
    @NotNull
    private EducationLevelEnum highestEducation;
    private Institution almaMater;
    @NotNull
    private String primaryInterest;
    private String secondaryInterest;
    private List<String> tertiaryInterests;
}
