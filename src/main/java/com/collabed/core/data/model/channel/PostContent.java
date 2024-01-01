package com.collabed.core.data.model.channel;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class PostContent {
    @NotNull
    private String richTextContent;
    private Map<String, String> urls;
}
