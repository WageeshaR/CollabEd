package com.collabed.core.data.model.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostContent {
    @NotNull
    private String content;
    private boolean rtf = false;
    private Map<String, String> urls;
}
