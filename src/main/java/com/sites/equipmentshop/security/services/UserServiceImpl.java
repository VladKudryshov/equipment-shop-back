package com.sites.equipmentshop.security.services;

import com.sites.equipmentshop.security.Tokens;
import com.sites.equipmentshop.security.UserService;
import com.sites.equipmentshop.security.config.TokenConfig;
import com.sites.equipmentshop.security.domain.UserData;
import com.sites.equipmentshop.security.endpoints.dto.ModifyUserDTO;
import com.sites.equipmentshop.security.endpoints.dto.NewUserDTO;
import com.sites.equipmentshop.security.endpoints.dto.ResetPasswordDTO;
import com.sites.equipmentshop.security.endpoints.dto.UpdateUserDTO;
import com.sites.equipmentshop.security.exceptions.EmailOrUserNameExistsException;
import com.sites.equipmentshop.security.exceptions.MailException;
import com.sites.equipmentshop.security.exceptions.PasswordResetNotExistsException;
import com.sites.equipmentshop.security.exceptions.UserNotExistsException;
import com.sites.equipmentshop.security.persistence.entities.PasswordResets;
import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import com.sites.equipmentshop.security.persistence.entities.UserRoles;
import com.sites.equipmentshop.security.persistence.entities.UserStatuses;
import com.sites.equipmentshop.security.persistence.repositories.JPAPasswordResetsRepository;
import com.sites.equipmentshop.security.persistence.repositories.JPAUserRepository;
import com.sites.equipmentshop.utils.shared.SendMailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final int PASSWORD_LENGTH = 12;

    private final JPAUserRepository userRepo;
    private final JPAPasswordResetsRepository pResRepo;
    private final SendMailService sendMailService;
    private final MailTemplateFactory templateFactory;
    private final TokenConfig tokenConfig;
    private final PasswordEncoder passwordEncoder;

    @Value("${management.security.reset.expirationtime}")
    private long resetExpiredTime;


    @Autowired
    public UserServiceImpl(JPAUserRepository userRepo,
                           SendMailService sendMailService,
                           JPAPasswordResetsRepository pResRepo,
                           MailTemplateFactory templateFactory,
                           TokenConfig tokenConfig) {
        this.userRepo = userRepo;
        this.sendMailService = sendMailService;
        this.pResRepo = pResRepo;
        this.templateFactory = templateFactory;
        this.tokenConfig = tokenConfig;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    // 	Don't need transactions here
    public UserEntity createNewUser(NewUserDTO userDto) {
        UserEntity entity = new UserEntity();
        String email = userDto.getEmail();
        String id = UserService.md5Hex(email);
        Optional<UserEntity> entityFromDb = userRepo.findById(id);
        if (!(entityFromDb.isPresent()) || UserStatuses.DELETED.equals(entityFromDb.get().getUserStatus())) {
            String password = generatePassword();
            entity.setId(id);
            entity.setEmail(userDto.getEmail());
            entity.setPassword(passwordEncoder.encode(password));
            entity.setUserName(userDto.getUserName());
            entity.setUserRole(userDto.getUserRole() != null ? userDto.getUserRole() : UserRoles.USER);
            entity.setUserStatus(UserStatuses.NEW);
            entity.setLastLogin(null);
            userRepo.save(entity);
            try {
                sendMailService.sendMessage(userDto.getEmail(),
                        "[Sem Platform] Welcome!",
                        templateFactory.prepareSignTemplate(userDto, password));
            } catch (Exception e) {
                throw new MailException("Can't create new user", e);
            }
            return entity;
        } else {
            throw new EmailOrUserNameExistsException(userDto.getUserName(), userDto.getEmail());
        }
    }

    @Override
    @Transactional
    public void deleteUserById(String id) {
        Optional<UserEntity> entity = userRepo.findById(id);
        if (entity.isPresent()) {
            entity.get().setUserStatus(UserStatuses.DELETED);
            userRepo.save(entity.get());
        } else {
            throw new UserNotExistsException(id);
        }
    }

    @Override
    @Transactional
    public UserEntity updateUser(String id, UpdateUserDTO userDto) {
        Optional<UserEntity> entity = userRepo.findById(id);
        if (entity.isPresent()) {
            if (userDto.getUserRole() != null) entity.get().setUserRole(userDto.getUserRole());
            if (userDto.getUserStatus() != null) entity.get().setUserStatus(userDto.getUserStatus());
            userRepo.save(entity.get());
            return entity.get();
        } else {
            throw new UserNotExistsException(id);
        }
    }

    @Override
    public Collection<UserEntity> getAllUsers() {
        return userRepo.findByUserStatusNotInAndIdNot(
                Sort.by(Direction.DESC, "userName"),
                Collections.singletonList(UserStatuses.DELETED), UserService.currentUserId());
    }

    @Override
    public UserEntity getUserById(String id) {
        Optional<UserEntity> entity = userRepo.findById(id);
        if (entity.isPresent()) {
            return entity.get();
        } else {
            throw new UserNotExistsException(id);
        }
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        UserEntity entity = userRepo.findUserByEmail(email);
        if (Objects.nonNull(entity)) {
            return entity;
        } else {
            throw new UserNotExistsException(email);
        }
    }

    @Override
    public UserEntity getActiveUserByEmail(String email) {
        UserEntity entity = userRepo.findUserByEmailAndUserStatusNotIn(email, UserStatuses.getInActive());
        if (entity != null) {
            return entity;
        } else {
            throw new UserNotExistsException(email);
        }
    }

    @Override
    @Transactional
    public UserData modifyUser(HttpServletRequest request, HttpServletResponse response, ModifyUserDTO modifyDto, String userId) {
        UserEntity result = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotExistsException(userId));

        if (modifyDto.getPassword() != null) {
            result.setPassword(passwordEncoder.encode(modifyDto.getPassword()));
            result.setUserStatus(UserStatuses.ACTIVE);
            result = userRepo.save(result);
        }
        if (modifyDto.getUserName() != null && !modifyDto.getUserName().equals(result.getUserName())) {
            result.setUserName(modifyDto.getUserName());
            result = userRepo.save(result);
            response.setHeader(Tokens.ACCESS_TOKEN.getHeader(), TokenConfig.TOKEN_PREFIX + " " + tokenConfig.buildToken(result, tokenConfig.getAccessTokenExpirationTime(), Tokens.ACCESS_TOKEN.getType()));
        }
        return new UserData(result);
    }

    @Override
    @Transactional
    public void passwordToReset(String email) {
        UserEntity entity = userRepo.findUserByEmailAndUserStatusNotIn(email, Arrays.asList(UserStatuses.DELETED, UserStatuses.DISABLED));
        if (entity == null) {
            throw new UserNotExistsException(email);
        }
        List<PasswordResets> p = pResRepo.findByUserId(entity.getId());
        if (p != null) {
            pResRepo.deleteAll(p);
        }
        PasswordResets pRes = pResRepo.save(new PasswordResets(entity.getId()));
        try {
            sendMailService.sendMessage(entity.getEmail(),
                    "[Sem Platform] Password reset",
                    templateFactory.prepareResetPassTemplate(entity, pRes.getId()));
        } catch (MessagingException e) {
            throw new MailException("Can't reset user password", e);
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO rpDto) {
        Date expDate = new Date(LocalDateTime.now().minus(resetExpiredTime, ChronoUnit.MILLIS).toEpochSecond(ZoneOffset.UTC) * 1000);
        PasswordResets pRes = pResRepo.findByIdAndCreatedAfter(rpDto.getResetId(), expDate);
        if (pRes != null) {
            UserEntity userEntity = userRepo.findById(pRes.getUserId())
                    .orElseThrow(() -> new UserNotExistsException(pRes.getUserId()));
            userEntity.setPassword(passwordEncoder.encode(rpDto.getNewPassword()));
            userEntity.setUserStatus(UserStatuses.ACTIVE);
            userRepo.save(userEntity);
            pResRepo.delete(pRes);
        } else {
            throw new PasswordResetNotExistsException(rpDto.getResetId());
        }
    }

    private String generatePassword() {
        return new PasswordGenerator.PasswordGeneratorBuilder()
                .useDigits(true)
                .useLower(true)
                .useUpper(true)
                .build()
                .generate(PASSWORD_LENGTH);
    }
}
