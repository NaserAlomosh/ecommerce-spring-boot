package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.user.UserDtos.*;
import com.smart.ecommerce.entity.User;
import com.smart.ecommerce.enums.*;
import com.smart.ecommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;

    public User current(String email) { return users.findByEmail(email.toLowerCase()).orElseThrow(); }
    public UserResponse me(String email) { return mapper.toResponse(current(email)); }

    @Transactional
    public UserResponse updateMe(String email, UpdateProfileRequest r) {
        User u = current(email);
        if (!u.getPhoneNumber().equals(r.phoneNumber()) && users.existsByPhoneNumber(r.phoneNumber())) throw new IllegalArgumentException("error.phone_exists");
        u.setFirstName(r.firstName()); u.setLastName(r.lastName()); u.setPhoneNumber(r.phoneNumber()); u.setProfileImage(r.profileImage());
        return mapper.toResponse(u);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest r) {
        User u = current(email);
        if (!encoder.matches(r.currentPassword(), u.getPasswordHash())) throw new BadCredentialsException("error.invalid_password");
        u.setPasswordHash(encoder.encode(r.newPassword())); refreshTokens.revokeAll(u, java.time.Instant.now());
    }

    @Transactional
    public UserResponse adminCreate(String actorEmail, AdminCreateUserRequest r) {
        User actor = current(actorEmail); requireCanManageRole(actor, r.role());
        if (users.existsByEmail(r.email().toLowerCase()) || users.existsByPhoneNumber(r.phoneNumber())) throw new IllegalArgumentException("error.email_or_phone_exists");
        User u = new User(); u.setFirstName(r.firstName()); u.setLastName(r.lastName()); u.setEmail(r.email().toLowerCase()); u.setPhoneNumber(r.phoneNumber());
        u.setPasswordHash(encoder.encode(r.password())); u.setRole(r.role()); u.setStatus(UserStatus.ACTIVE); u.setEmailVerified(true);
        return mapper.toResponse(users.save(u));
    }

    public Page<UserResponse> search(String q, Role role, UserStatus status, Pageable p) { return users.search(q, role, status, p).map(mapper::toResponse); }
    public UserResponse get(Long id) { return mapper.toResponse(users.findById(id).orElseThrow()); }

    @Transactional
    public UserResponse status(String actorEmail, Long id, StatusRequest r) {
        User actor = current(actorEmail); User u = users.findById(id).orElseThrow(); requireCanManageTarget(actor, u);
        if (u.getRole() == Role.ADMIN && u.getStatus() == UserStatus.ACTIVE && r.status() != UserStatus.ACTIVE && users.countByRoleAndStatus(Role.ADMIN, UserStatus.ACTIVE) <= 1) throw new AccessDeniedException("error.last_admin");
        u.setStatus(r.status()); if (r.status() == UserStatus.BLOCKED) refreshTokens.revokeAll(u, java.time.Instant.now()); return mapper.toResponse(u);
    }

    @Transactional
    public void adminChangePassword(String actorEmail, Long id, AdminChangePasswordRequest r) {
        User actor = current(actorEmail); User target = users.findById(id).orElseThrow(); requireCanManageTarget(actor, target);
        if (actor.getRole() != Role.ADMIN) throw new AccessDeniedException("error.forbidden");
        target.setPasswordHash(encoder.encode(r.newPassword())); refreshTokens.revokeAll(target, java.time.Instant.now());
    }

    @Transactional
    public void delete(String actorEmail, Long id) {
        User actor = current(actorEmail); User target = users.findById(id).orElseThrow(); requireCanManageTarget(actor, target);
        if (target.getId().equals(actor.getId())) throw new AccessDeniedException("error.delete_self");
        if (target.getRole() == Role.ADMIN && users.countByRoleAndStatus(Role.ADMIN, UserStatus.ACTIVE) <= 1) throw new AccessDeniedException("error.last_admin");
        refreshTokens.revokeAll(target, java.time.Instant.now()); target.setStatus(UserStatus.DELETED); target.setEmail("deleted-" + target.getId() + "-" + target.getEmail()); target.setPhoneNumber("+999" + String.format("%012d", target.getId() % 1_000_000_000_000L));
    }

    private void requireCanManageTarget(User actor, User target) { requireCanManageRole(actor, target.getRole()); }
    private void requireCanManageRole(User actor, Role role) {
        if (actor.getRole() == Role.ADMIN) return;
        if (actor.getRole() == Role.SUB_ADMIN && (role == Role.CUSTOMER || role == Role.DELIVERY)) return;
        throw new AccessDeniedException("error.forbidden");
    }
}
