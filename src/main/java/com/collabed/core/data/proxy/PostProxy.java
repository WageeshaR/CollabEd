package com.collabed.core.data.proxy;

import com.collabed.core.data.model.channel.Channel;
import com.collabed.core.data.model.channel.Post;
import com.collabed.core.data.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class PostProxy extends Post {
    public PostProxy(Post post) {
        this.id = post.getId();
        this.author = new User();
        this.author.setId(post.getAuthor().getId());
        this.author.setUsername(post.getAuthor().getUsername());
        this.author.setFirstName(post.getAuthor().getFirstName());
        this.author.setLastName(post.getAuthor().getLastName());
        this.parent = post.getParent();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.channel = new Channel();
        this.channel.setTopic(post.getChannel() == null ? null : post.getChannel().getTopic());
        this.channel.setName(post.getChannel() == null ? null : post.getChannel().getName());
    }
}
