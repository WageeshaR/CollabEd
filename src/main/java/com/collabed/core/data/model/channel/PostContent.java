package com.collabed.core.data.model.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostContent {
    @NotNull
    private String richTextContent;
    private Map<String, String> urls;
}
