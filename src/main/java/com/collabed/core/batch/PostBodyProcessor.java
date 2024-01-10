package com.collabed.core.batch;

import com.collabed.core.batch.util.DocProcessor;
import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.model.channel.PostContent;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.batch.item.ItemProcessor;

@Log4j2
public class PostBodyProcessor implements ItemProcessor<Post, String> {
    @Override
    public String process(Post post) {
        log.info("Post body processing started for post: " + post.getId());
        PostContent content = post.getContent();
        String plainText;
        if (content == null) {
            log.info("No content found for post, returning null");
            return null;
        }
        if (content.isRtf()) {
            String richText = content.getContent();
            Document rtfDoc = Jsoup.parse(richText);
            plainText = DocProcessor.getPlainText(rtfDoc);
        }
        else {
            plainText = content.getContent();
        }
        log.info("Post body processing finished");
        return plainText;
    }
}
