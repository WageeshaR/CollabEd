package com.collabed.core.data.model.channel;

import com.collabed.core.data.model.user.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Document
@Data
@Log4j2
public class Thread {
    @Id
    private String id;
    @DocumentReference
    @NotNull
    private Forum forum;
    @DocumentReference
    @Size(min = 2)
    private List<User> members;
    @NotNull
    private String subject;
    private boolean resolved = false;
    @DocumentReference
    private Thread attachedTo;

    public void addMember(User user) {
        if (this.members == null)
            this.members = new ArrayList<>();
        
        try {
            this.members.add(user);
        } catch (Exception e) {
            log.error("Error adding member to the thread: {}", e);
        }
    }
}
