package com.project.shopapp.services.user;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.dtos.UserUpdateDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.ExpiredTokenException;
import com.project.shopapp.exceptions.PermissionDenyException;
import com.project.shopapp.models.Role;
import com.project.shopapp.models.SocialAccount;
import com.project.shopapp.models.Token;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.SocialAccountRepository;
import com.project.shopapp.repositories.TokenRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        String email = userDTO.getEmail();

        if ((phoneNumber == null || phoneNumber.isBlank()) && (email == null || email.isBlank())) {
            throw new DataNotFoundException("At least Phone Number or Email is required");
        }

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            if (userRepository.existsByPhoneNumber(phoneNumber)) {
                throw new DataIntegrityViolationException("Phone number is already registered.");
            }
        }

        if (email != null && !email.isBlank()) {
            if (userRepository.existsByEmail(email)) {
                throw new DataIntegrityViolationException("Email address is already registered.");
            }
        }

        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found."));

        if (role.getName().equalsIgnoreCase(Role.ADMIN)) {
            throw new PermissionDenyException("Assigning ADMIN role is not allowed.");
        }

        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(phoneNumber)
                .email(email)
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .active(true)
                .build();
        newUser.setRole(role);

        if (!userDTO.isSocialLogin()) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }

        return userRepository.save(newUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String subject = jwtTokenUtil.getSubject(token);
        Optional<User> user;
        user = userRepository.findByPhoneNumber(subject);
        if (user.isEmpty() && ValidationUtils.isValidEmail(subject)) {
            user = userRepository.findByEmail(subject);
        }
        return user.orElseThrow(() -> new Exception("User not found"));
    }

    @Override
    @Transactional
    public String loginSocial(UserLoginDTO userLoginDTO) throws Exception {
        Optional<User> optionalUser = Optional.empty();
        Role roleUser = roleRepository.findByName(Role.USER).orElseThrow(() -> new DataNotFoundException("Role USER not found"));

        if (!userLoginDTO.isSocialLogin()) {
            throw new IllegalArgumentException("Invalid social account information. Provider and ProviderId are required.");
        }

        String provider = userLoginDTO.getProvider().toUpperCase();
        String providerId = userLoginDTO.getProviderId();

        Optional<SocialAccount> socialAccount = socialAccountRepository.findByProviderAndProviderId(provider, providerId);

        if (socialAccount.isPresent()) {
            optionalUser = Optional.of(socialAccount.get().getUser());
        } else {
            User user;
            Optional<User> existingUserByEmail = userRepository.findByEmail(userLoginDTO.getEmail());

            if (existingUserByEmail.isPresent()) {
                user = existingUserByEmail.get();
            } else {
                User newUser = User.builder()
                        .fullName(Optional.ofNullable(userLoginDTO.getFullname()).orElse(""))
                        .email(Optional.ofNullable(userLoginDTO.getEmail()).orElse(""))
                        .profileImage(Optional.ofNullable(userLoginDTO.getProfileImage()).orElse(""))
                        .role(roleUser)
                        .password("")
                        .active(true)
                        .build();
                user = userRepository.save(newUser);
            }

            SocialAccount newSocialAccount = SocialAccount.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .name(userLoginDTO.getFullname())
                    .email(userLoginDTO.getEmail())
                    .user(user)
                    .build();
            socialAccountRepository.save(newSocialAccount);

            optionalUser = Optional.of(user);
        }

        User user = optionalUser.get();
        if (!user.isActive()) {
            throw new DataNotFoundException("User is locked");
        }

        return jwtTokenUtil.generateToken(user);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserUpdateDTO userUpdateDTO) throws Exception {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));

        if (userUpdateDTO.getFullName() != null) {
            existingUser.setFullName(userUpdateDTO.getFullName());
        }
        if (userUpdateDTO.getAddress() != null) {
            existingUser.setAddress(userUpdateDTO.getAddress());
        }
        if (userUpdateDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(userUpdateDTO.getDateOfBirth());
        }

        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            if (!userUpdateDTO.getPassword().equals(userUpdateDTO.getRetypePassword())) {
                throw new DataNotFoundException("Password and retype password not the same");
            }
            String newPassword = userUpdateDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void changeProfileImage(Long userId, String imageName) throws Exception {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setProfileImage(imageName);
        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
        String encodedPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(encodedPassword);
        userRepository.save(existingUser);

        List<Token> tokens = tokenRepository.findByUser(existingUser);
        if (!tokens.isEmpty()) {
            tokenRepository.deleteAll(tokens);
        }
    }

    @Override
    @Transactional
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setActive(active);
        userRepository.save(existingUser);
    }

}
