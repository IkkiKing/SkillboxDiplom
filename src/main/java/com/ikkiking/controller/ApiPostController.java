package com.ikkiking.controller;

import com.ikkiking.api.response.PostResponse.*;
import com.ikkiking.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiPostController {

    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/api/post")
    private GetPostResponse getPosts(@RequestParam(name = "limit") int limit,
                                     @RequestParam(name = "offset") int offset,
                                     @RequestParam(name = "mode") String mode){
        return postService.getPosts(limit, offset, mode);
    }

    @GetMapping("/api/post/search")
    private SearchPostResponse searchPosts(@RequestParam(name = "limit") int limit,
                                           @RequestParam(name = "offset") int offset,
                                           @RequestParam(name = "query") String query){
        return postService.searchPosts(limit, offset, query);
    }

    @GetMapping("/api/post/byDate")
    private PostByDateResponse getPostsByDate(@RequestParam(name = "limit") int limit,
                                              @RequestParam(name = "offset") int offset,
                                              @RequestParam(name = "date") String date){
        return postService.getPostsByDate(limit, offset, date);
    }

    @GetMapping("/api/post/byTag")
    private PostByTagResponse getPostsByTag(@RequestParam(name = "limit") int limit,
                                            @RequestParam(name = "offset") int offset,
                                            @RequestParam(name = "tag") String tag){
        return postService.getPostsByTag(limit, offset, tag);
    }

    @GetMapping("/api/post/moderation")
    private PostForModerationResponse getPostsForModeration(@RequestParam(name = "limit") int limit,
                                               @RequestParam(name = "offset") int offset,
                                               @RequestParam(name = "status") String status){
        return postService.getPostsForModeration(limit, offset, status);
    }

    @GetMapping("/api/post/my")
    private MyPostResponse getMyPosts(@RequestParam(name = "limit") int limit,
                                    @RequestParam(name = "offset") int offset,
                                    @RequestParam(name = "status") String status){
        return postService.getMyPosts(limit, offset, status);
    }

    @GetMapping("/api/post/{id}")
    private PostByIdResponse getPostById(@PathVariable int id){
        return postService.getPostByid(id);
    }
}
