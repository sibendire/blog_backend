package com.blogger.blog.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String category;

    private String imagePath; // stores filename
    private String videoPath; // stores filename
    @Column(nullable = false)
    private Integer likes = 0;

}
