package com.blogger.blog.repositories;

import com.blogger.blog.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser,Long> {
    @Query("SELECT u FROM AppUser u WHERE u.email=?1")
AppUser findByEmail(String email);
        AppUser findByEmailIgnoreCase(String email);

        boolean existsByEmail(String email);

}
