package com.collabed.core.data.model.channel;

import lombok.Data;
import java.util.Map;

@Data
public class PostContent {
    private String richTextContent;
    private Map<String, String> urls;
}
