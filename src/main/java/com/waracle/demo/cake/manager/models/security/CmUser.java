package com.waracle.demo.cake.manager.models.security;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "cm_users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
@Getter
@Setter
@NoArgsConstructor
public class CmUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(columnDefinition = "text")
    private String email;

    @NotBlank
    @Column(columnDefinition = "text")
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cm_user_roles",
            joinColumns = @JoinColumn(name = "cm_user_id"),
            inverseJoinColumns = @JoinColumn(name = "cm_role_id"))
    private Set<CmRole> roles = new HashSet<>();

    public CmUser(String email, String password, Set<CmRole> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }
}
