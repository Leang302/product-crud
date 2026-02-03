package com.example.productscrud.model.entity;

import com.example.productscrud.model.enumeration.AppUserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "app_user")   // ← explicit table name is good practice
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue
    private UUID userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "roles", columnDefinition = "json")
    private Set<AppUserRole> roles = new HashSet<>();

    // Locking fields
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int failedLoginAttempts = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date accountLockedUntil;   // null = not locked
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isActive = false;     // ← new field


    // Optional: helper methods
    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (AppUserRole role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));   // ← changed to standard prefix
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountLockedUntil == null || new Date().after(accountLockedUntil);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    public boolean autoUnlockIfPossible() {
        if (accountLockedUntil != null && new Date().after(accountLockedUntil)) {
            accountLockedUntil = null;
            failedLoginAttempts = 0;
            return true;
        }
        return false;
    }

    public void resetFailedAttempts() {
        failedLoginAttempts = 0;
        accountLockedUntil = null;
    }

    public void incrementFailedAttempts() {
        failedLoginAttempts++;
    }

    public boolean isLocked() {
        return !isAccountNonLocked();
    }
}