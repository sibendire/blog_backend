package com.blogger.blog.controller;

import com.blogger.blog.entities.Posts;
import com.blogger.blog.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts/blog")
@CrossOrigin(origins = "https://blog-frontend-kv4q.onrender.com")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    // ✅ GET all posts
    @GetMapping
    public List<Posts> getAllPosts() {
        return postRepository.findAll();
    }

    // ✅ CREATE a new post (form-data with optional image & video)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Posts> createPost(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "video", required = false) MultipartFile video
    ) throws IOException {

        // Ensure upload dir exists
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        Posts post = new Posts();
        post.setTitle(title);
        post.setDescription(description);
        post.setCategory(category);
        post.setLikes(0);

        if (image != null && !image.isEmpty()) {
            String fileName = image.getOriginalFilename();
            image.transferTo(new File(UPLOAD_DIR + fileName));
            post.setImagePath("/uploads/" + fileName);
        }

        if (video != null && !video.isEmpty()) {
            String fileName = video.getOriginalFilename();
            video.transferTo(new File(UPLOAD_DIR + fileName));
            post.setVideoPath("/uploads/" + fileName);
        }

        postRepository.save(post);
        return ResponseEntity.ok(post);
    }

    // ✅ CREATE a new post (plain JSON without files)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Posts> createPostJson(@RequestBody Posts post) {
        post.setId(null); // force insert
        postRepository.save(post);
        return ResponseEntity.ok(post);
    }

    // ✅ UPDATE a post
    @PutMapping("/{id}")
    public ResponseEntity<Posts> updatePost(@PathVariable Long id, @RequestBody Posts updatedPost) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setTitle(updatedPost.getTitle());
                    post.setDescription(updatedPost.getDescription());
                    post.setCategory(updatedPost.getCategory());
                    post.setImagePath(updatedPost.getImagePath());
                    post.setVideoPath(updatedPost.getVideoPath());
                    postRepository.save(post);
                    return ResponseEntity.ok(post);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    // ✅ LIKE a post
    @PutMapping("/{id}/like")
    public ResponseEntity<Posts> likePost(@PathVariable Long id) {
        Posts post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
        return ResponseEntity.ok(post);
    }
    // In PostController.java
    @GetMapping("/category/{category}")
    public List<Posts> getPostsByCategory(@PathVariable String category) {
        return postRepository.findByCategoryIgnoreCase(category);
    }

    // GET a single post by ID
    @GetMapping("/{id}")
    public ResponseEntity<Posts> getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(post -> ResponseEntity.ok(post))
                .orElse(ResponseEntity.notFound().build());
    }

}
