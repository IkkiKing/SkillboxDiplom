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
    @PreAuthorize("hasAuthority('user:write')")
    private ResponseEntity<GetPostResponse> getPosts(@RequestParam(name = "limit",  required = false, defaultValue = "10") int limit,
                                                    @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                    @RequestParam(name = "mode", required = false, defaultValue = "recent") String mode){
        if (postService == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else {
            return new ResponseEntity<>(postService.getPosts(limit, offset, mode), HttpStatus.OK);
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('user:moderate')")
    private SearchPostResponse searchPosts(@RequestParam(name = "limit",  required = false, defaultValue = "10") int limit,
                                           @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                           @RequestParam(name = "query", required = false, defaultValue = "") String query){
        return postService.searchPosts(limit, offset, query);
    }

    @GetMapping("/byDate")
    private PostByDateResponse getPostsByDate(@RequestParam(name = "limit",  required = false, defaultValue = "10") int limit,
                                              @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                              @RequestParam(name = "date") String date){
        return postService.getPostsByDate(limit, offset, date);
    }

    @GetMapping("/byTag")
    private PostByTagResponse getPostsByTag(@RequestParam(name = "limit",  required = false, defaultValue = "10") int limit,
                                            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                            @RequestParam(name = "tag", defaultValue = "") String tag){
        return postService.getPostsByTag(limit, offset, tag);
    }

    @GetMapping("/moderation")
    private PostForModerationResponse getPostsForModeration(@RequestParam(name = "limit",  required = false, defaultValue = "10") int limit,
                                               @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                               @RequestParam(name = "status", defaultValue = "accepted") String status){
        return postService.getPostsForModeration(limit, offset, status);
    }

    @GetMapping("/my")
    private MyPostResponse getMyPosts(@RequestParam(name = "limit",  required = false, defaultValue = "10") int limit,
                                    @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                    @RequestParam(name = "status", defaultValue = "published") String status){
        return postService.getMyPosts(limit, offset, status);
    }

    @GetMapping("/{id}")
    private PostByIdResponse getPostById(@PathVariable long id){
        return postService.getPostByid(id);
    }
}
