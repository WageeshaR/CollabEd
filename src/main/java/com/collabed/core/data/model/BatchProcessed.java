package com.collabed.core.data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class BatchProcessed extends AuditMetadata {
    private boolean batchProcessed = false;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime batchProcessedAt;
}
