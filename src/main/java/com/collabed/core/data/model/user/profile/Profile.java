package com.collabed.core.data.model.user.profile;

import com.collabed.core.data.model.institution.Institution;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "userProfile")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Profile {
    @Id
    private String id;
    private boolean isStudying;
    @NotNull
    private TitleEnum title;
    @NotNull
    private EducationLevelEnum highestEducation;
    @DocumentReference
    private Institution almaMater;
    @NotNull
    private String primaryInterest;
    private String secondaryInterest;
    private List<String> tertiaryInterests;
}
