package com.blogger.blog.controller;

import com.blogger.blog.entities.Posts;
import com.blogger.blog.repositories.PostRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@CrossOrigin(
        origins = {
                "http://localhost:3000",
                "https://blog-frontend-kv4q.onrender.com"
        },
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/posts/blog")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private Cloudinary cloudinary;

    // ✅ GET all posts
    @GetMapping
    public List<Posts> getAllPosts() {
        return postRepository.findAll();
    }

    // ✅ CREATE post (multipart)
    @PostMapping(value = "/multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Posts> createPost(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) MultipartFile video
    ) throws IOException {

        Posts post = new Posts();
        post.setTitle(title);
        post.setDescription(description);
        post.setCategory(category);
        post.setLikes(0);

        if (image != null && !image.isEmpty()) {
            var result = cloudinary.uploader().upload(
                    image.getBytes(),
                    ObjectUtils.asMap("resource_type", "image")
            );
            post.setImagePath(result.get("secure_url").toString());
        }

        if (video != null && !video.isEmpty()) {
            var result = cloudinary.uploader().upload(
                    video.getBytes(),
                    ObjectUtils.asMap("resource_type", "video")
            );
            post.setVideoPath(result.get("secure_url").toString());
        }

        postRepository.save(post);
        return ResponseEntity.ok(post);
    }

    // ✅ CREATE post (JSON)
    @PostMapping(value = "/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Posts> createPostJson(@RequestBody Posts post) {
        post.setId(null);
        postRepository.save(post);
        return ResponseEntity.ok(post);
    }
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllPosts() {
        postRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }


}
