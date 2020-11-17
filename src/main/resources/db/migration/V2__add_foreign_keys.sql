ALTER TABLE posts
    ADD CONSTRAINT fk_posts_user_moderator FOREIGN KEY (moderator_id) REFERENCES users (id);
ALTER TABLE posts
    ADD CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE post_votes
    ADD CONSTRAINT fk_post_votes_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE post_votes
    ADD CONSTRAINT fk_post_votes_post FOREIGN KEY (post_id) REFERENCES posts (id);

ALTER TABLE tag2post
    ADD CONSTRAINT fk_tag2post_tag FOREIGN KEY (tag_id) REFERENCES tags (id);
ALTER TABLE tag2post
    ADD CONSTRAINT fk_tag2post_post FOREIGN KEY (post_id) REFERENCES posts (id);

ALTER TABLE post_comments
    ADD CONSTRAINT fk_post_comments_parent FOREIGN KEY (parent_id) REFERENCES post_comments (id);
ALTER TABLE post_comments
    ADD CONSTRAINT fk_post_comments_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE post_comments
    ADD CONSTRAINT fk_post_comments_post FOREIGN KEY (post_id) REFERENCES posts (id);




