package com.collabed.core.data.model;

import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Setter
public class AuditMetadata {
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @CreatedBy
    private String createdByUser;
    @LastModifiedBy
    private String modifiedByUser;

    public String getCreatedDate() {
        if (createdDate != null)
            return createdDate.toString();
        return null;
    }

    public String getLastModifiedDate() {
        if (lastModifiedDate != null)
            return lastModifiedDate.toString();
        return null;
    }

    public String getCreatedByUser() {
        if (createdByUser != null)
            return createdByUser;
        return null;
    }

    public String getModifiedByUser() {
        if (modifiedByUser != null)
            return modifiedByUser;
        return null;
    }

    public void clearAudits() {
        this.setCreatedByUser(null);
        this.setCreatedDate(null);
        this.setModifiedByUser(null);
        this.setLastModifiedDate(null);
    }

}
