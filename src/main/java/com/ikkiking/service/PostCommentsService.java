package com.ikkiking.service;

import com.ikkiking.api.request.CommentRequest;
import com.ikkiking.api.response.CommentAddError;
import com.ikkiking.api.response.CommentAddResponse;
import com.ikkiking.base.ContextUser;
import com.ikkiking.model.Post;
import com.ikkiking.model.PostComments;
import com.ikkiking.repository.PostCommentsRepository;
import com.ikkiking.repository.PostRepository;
import com.ikkiking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class PostCommentsService {
    private final PostCommentsRepository postCommentsRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostCommentsService(PostCommentsRepository postCommentsRepository, PostRepository postRepository, UserRepository userRepository) {
        this.postCommentsRepository = postCommentsRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * Добавления комментария к посту.
     * */
    @Transactional
    public ResponseEntity<CommentAddResponse> comment(CommentRequest commentRequest) {

        CommentAddResponse commentAddResponse = new CommentAddResponse();

        String text = commentRequest.getText();
        Long parentId = commentRequest.getParentId();
        Long postId = commentRequest.getPostId();

        //TODO: Возможно сделать через ControllerAdvice?
        if (text == null || text.isEmpty()) {
            commentAddResponse.setErrors(new CommentAddError("Текст комментария не задан"));
            return new ResponseEntity(commentAddResponse, HttpStatus.BAD_REQUEST);
        }
        if (text.length() < 3) {
            commentAddResponse.setErrors(new CommentAddError("Текст комментария слишком короткий"));
            return new ResponseEntity(commentAddResponse, HttpStatus.BAD_REQUEST);
        }

        Optional<Post> post = postRepository.findById(postId);
        if (!post.isPresent()) {
            commentAddResponse.setErrors(new CommentAddError("Пост на который ссылается комментарий не найден!"));
            return new ResponseEntity(commentAddResponse, HttpStatus.BAD_REQUEST);
        }

        if (parentId != null) {
            Optional<PostComments> postComments = postCommentsRepository.findById(parentId);
            if (!postComments.isPresent()) {
                commentAddResponse.setErrors(new CommentAddError("Родительсткий комментарий не найден!"));
                return new ResponseEntity(commentAddResponse, HttpStatus.BAD_REQUEST);
            }
        }

        PostComments comment = new PostComments();
        comment.setParentId(parentId);
        comment.setPost(post.get());
        comment.setText(text);
        comment.setTime(new Date());
        comment.setUser(ContextUser.getUserFromContext(userRepository));

        PostComments postComments = postCommentsRepository.save(comment);
        commentAddResponse.setId(postComments.getId());
        return ResponseEntity.ok(commentAddResponse);
    }


}
