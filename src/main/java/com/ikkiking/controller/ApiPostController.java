package com.ikkiking.controller;

import com.ikkiking.api.request.PostRequest;
import com.ikkiking.api.request.VoteRequest;
import com.ikkiking.api.response.PostResponse.*;
import com.ikkiking.api.response.VoteResponse;
import com.ikkiking.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<GetPostResponse> getPosts(
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "mode", required = false, defaultValue = "recent") String mode) {

        return postService.getPosts(limit, offset, mode);
    }

    @GetMapping("/search")
    public ResponseEntity<SearchPostResponse> searchPosts(
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "query", required = false, defaultValue = "") String query) {

        return postService.searchPosts(limit, offset, query);
    }

    @GetMapping("/byDate")
    public ResponseEntity<PostByDateResponse> getPostsByDate(
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "date") String date)
    {
        return postService.getPostsByDate(limit, offset, date);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostByTagResponse> getPostsByTag(
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "tag", defaultValue = "") String tag) {
        return postService.getPostsByTag(limit, offset, tag);
    }

    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostForModerationResponse> getPostsForModeration(
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "status", defaultValue = "new") String status) {

        return postService.getPostsForModeration(limit, offset, status);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<MyPostResponse> getMyPosts(
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "status", defaultValue = "published") String status) {

        return postService.getMyPosts(limit, offset, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostByIdResponse> getPostById(@PathVariable long id) {
        return postService.getPostByid(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostReturnResponse> addPost(@RequestBody PostRequest postRequest) {
        return postService.addPost(postRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostReturnResponse> editPost(@PathVariable long id,
                                                       @RequestBody PostRequest postRequest) {
        return postService.editPost(id, postRequest);
    }

    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<VoteResponse> like(@RequestBody VoteRequest voteRequest) {
        return postService.like(voteRequest);
    }

    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<VoteResponse> dislike(@RequestBody VoteRequest voteRequest) {
        return postService.dislike(voteRequest);
    }

}
