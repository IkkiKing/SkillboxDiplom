package com.ikkiking.service;

import com.ikkiking.api.request.CommentRequest;
import com.ikkiking.api.response.CommentAddResponse;
import com.ikkiking.base.ContextUser;
import com.ikkiking.base.exception.CommentException;
import com.ikkiking.model.Post;
import com.ikkiking.model.PostComments;
import com.ikkiking.repository.PostCommentsRepository;
import com.ikkiking.repository.PostRepository;
import com.ikkiking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
public class PostCommentsService {

    private static final int COMMENT_MIN_TEXT_LENGTH = 50;
    private final PostCommentsRepository postCommentsRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostCommentsService(PostCommentsRepository postCommentsRepository,
                               PostRepository postRepository,
                               UserRepository userRepository) {
        this.postCommentsRepository = postCommentsRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * Добавления комментария к посту.
     * */
    @Transactional
    public ResponseEntity<CommentAddResponse> comment(CommentRequest commentRequest) {
        String text = commentRequest.getText();
        Long parentId = commentRequest.getParentId();
        Long postId = commentRequest.getPostId();

        validateCommentRequest(text, parentId);

        Optional<Post> post = postRepository.findById(postId);
        if (!post.isPresent()) {
            throw new CommentException("Пост на который ссылается комментарий не найден!");
        }

        PostComments comment = new PostComments();
        comment.setParentId(parentId);
        comment.setPost(post.get());
        comment.setText(text);
        comment.setTime(new Date());
        comment.setUser(ContextUser.getUserFromContext(userRepository));

        PostComments postComments = postCommentsRepository.save(comment);
        CommentAddResponse commentAddResponse = new CommentAddResponse();
        commentAddResponse.setId(postComments.getId());
        commentAddResponse.setResult(true);
        return ResponseEntity.ok(commentAddResponse);
    }

    /**
     * Метод валидации комментария.
     * */
    private void validateCommentRequest(String text,
                                        Long parentId) {
        if (text == null || text.isEmpty()) {
            throw new CommentException("Текст комментария не задан");
        }
        if (text.length() < COMMENT_MIN_TEXT_LENGTH) {
            throw new CommentException("Текст комментария слишком короткий");
        }
        if (parentId != null) {
            Optional<PostComments> postComments = postCommentsRepository.findById(parentId);
            if (!postComments.isPresent()) {
                throw new CommentException("Родительсткий комментарий не найден!");
            }
        }
    }

}
