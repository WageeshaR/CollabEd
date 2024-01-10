package com.collabed.core.batch;

import com.collabed.core.batch.data.ContentType;
import com.collabed.core.batch.data.IntelTextContent;
import com.collabed.core.batch.util.DocProcessor;
import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.model.channel.PostContent;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.batch.item.ItemProcessor;

import java.util.UUID;

@Log4j2
public class PostBodyProcessor implements ItemProcessor<Post, IntelTextContent> {
    @Override
    public IntelTextContent process(Post post) {
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

        IntelTextContent textContent = new IntelTextContent();
        textContent.setId(UUID.randomUUID());
        textContent.setContent(plainText);

        if (plainText.length() > 20)
            textContent.setContentType(ContentType.TEXT_DOCUMENT);
        else textContent.setContentType(ContentType.TEXT_SHORT);

        textContent.setParentRef(Post.class.getName());

        log.info("Post body processing finished");
        return textContent;
    }
}
