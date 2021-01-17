package com.ikkiking.controller;

import com.ikkiking.api.response.PostResponse.*;
import com.ikkiking.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostService postService;

    @Autowired
    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    //@PreAuthorize("hasAuthority('user:write')")
    public GetPostResponse getPosts(@RequestParam(name = "limit",  required = false, defaultValue = "10") int limit,
                                                    @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                    @RequestParam(name = "mode", required = false, defaultValue = "recent") String mode){
        return postService.getPosts(limit, offset, mode);
    }

    @GetMapping("/search")
    //@PreAuthorize("hasAuthority('user:moderate')")
    public SearchPostResponse searchPosts(@RequestParam(name = "limit",  required = false, defaultValue = "10") int limit,
                                           @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                           @RequestParam(name = "query", required = false, defaultValue = "") String query){
        return postService.searchPosts(limit, offset, query);
    }

    @GetMapping("/byDate")
    public PostByDateResponse getPostsByDate(@RequestParam(name = "limit",  required = false, defaultValue = "10") int limit,
                                              @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                              @RequestParam(name = "date") String date){
        return postService.getPostsByDate(limit, offset, date);
    }

    @GetMapping("/byTag")
    public PostByTagResponse getPostsByTag(@RequestParam(name = "limit",  required = false, defaultValue = "10") int limit,
                                            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                            @RequestParam(name = "tag", defaultValue = "") String tag){
        return postService.getPostsByTag(limit, offset, tag);
    }

    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public PostForModerationResponse getPostsForModeration(@RequestParam(name = "limit",  required = false, defaultValue = "10") int limit,
                                               @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                               @RequestParam(name = "status", defaultValue = "new") String status){
        return postService.getPostsForModeration(limit, offset, status);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public MyPostResponse getMyPosts(@RequestParam(name = "limit",  required = false, defaultValue = "10") int limit,
                                    @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                    @RequestParam(name = "status", defaultValue = "published") String status){
        return postService.getMyPosts(limit, offset, status);
    }

    @GetMapping("/{id}")
    public PostByIdResponse getPostById(@PathVariable long id){
        return postService.getPostByid(id);
    }
}
