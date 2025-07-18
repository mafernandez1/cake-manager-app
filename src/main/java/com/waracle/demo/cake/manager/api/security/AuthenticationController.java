package com.waracle.demo.cake.manager.api.security;

import com.waracle.demo.cake.manager.api.security.dto.JwtResponse;
import com.waracle.demo.cake.manager.api.security.dto.LoginRequest;
import com.waracle.demo.cake.manager.api.security.dto.MessageResponse;
import com.waracle.demo.cake.manager.api.security.dto.SignUpRequest;
import com.waracle.demo.cake.manager.models.security.CmRole;
import com.waracle.demo.cake.manager.models.security.CmRoleType;
import com.waracle.demo.cake.manager.models.security.CmUser;
import com.waracle.demo.cake.manager.repository.security.CmRoleRepository;
import com.waracle.demo.cake.manager.repository.security.CmUserRepository;
import com.waracle.demo.cake.manager.security.jwt.JwtUtils;
import com.waracle.demo.cake.manager.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    AuthenticationManager authenticationManager;

    CmUserRepository userRepository;

    CmRoleRepository roleRepository;

    PasswordEncoder encoder;

    JwtUtils jwtUtils;

    public AuthenticationController(AuthenticationManager authenticationManager, CmUserRepository userRepository,
                                      CmRoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new JwtResponse(jwt, "Bearer",
                userDetails.getId(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.email())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        CmUser user = new CmUser(signUpRequest.email(),
                encoder.encode(signUpRequest.password()), new HashSet<>());

        Set<String> strRoles = signUpRequest.roles();
        Set<CmRole> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            CmRole userRole = roleRepository.findByName(CmRoleType.USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        CmRole adminRole = roleRepository.findByName(CmRoleType.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        CmRole userRole = roleRepository.findByName(CmRoleType.USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
