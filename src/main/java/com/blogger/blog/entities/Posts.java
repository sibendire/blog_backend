package com.blogger.blog.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private String category;

    private String imagePath; // stores filename
    private String videoPath; // stores filename
    @Column(nullable = false)
    private Integer likes = 0;
    @CreationTimestamp
    private LocalDateTime createdAt;


}
