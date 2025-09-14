package com.blogger.blog.repositories;

import com.blogger.blog.entities.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Posts, Long> {
    List<Posts> findByCategoryIgnoreCase(String category);

}
