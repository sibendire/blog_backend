package com.blogger.blog.security;

import com.blogger.blog.entities.AppUser;
import com.blogger.blog.entities.Privilege;
import com.blogger.blog.entities.Roles;
import com.blogger.blog.repositories.AppUserRepository;
import com.blogger.blog.repositories.PrivilegeRepository;
import com.blogger.blog.repositories.RoleRepository;
import jakarta.transaction.Transactional;
//import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collection;

import java.util.*;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService, ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PrivilegeRepository privilegeRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    private boolean alreadySetup = false;

    public CustomUserDetailsService(AppUserRepository userRepository, RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
    }

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found.");
        }
        System.out.println("Trying login with email: " + email);

        return new CustomUserDetails(user);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;

        Privilege read = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege write = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(read, write);

        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Collections.singletonList(read));

        if (!appUserRepository.existsByEmail("admin@school.com")) {
            Roles adminRole = roleRepository.findByName("ROLE_ADMIN");
            AppUser admin = new AppUser();
            admin.setEmail("admin@school.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setIsEnabled(true);
            admin.setVerificationCode(UUID.randomUUID().toString());
            admin.setRoles(Collections.singleton(adminRole));
            appUserRepository.save(admin);
        }

        alreadySetup = true;
    }

    private Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    private Roles createRoleIfNotFound(String name, Collection<Privilege> privileges) {
        Roles role = roleRepository.findByName(name);
        if (role == null) {
            role = new Roles(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
    public boolean updateUserPassword(Long userId, String rawPassword) {
        Optional<AppUser> optionalUser = appUserRepository.findById(userId);
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
            appUserRepository.save(user);
            return true;
        }
        return false;
    }

    public Optional<AppUser> findById(Long userId) {
        return appUserRepository.findById(userId);
    }
}
