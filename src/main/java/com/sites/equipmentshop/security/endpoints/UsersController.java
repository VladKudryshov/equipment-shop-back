package com.sites.equipmentshop.security.endpoints;

import com.sites.equipmentshop.security.UserService;
import com.sites.equipmentshop.security.domain.AccountCredentials;
import com.sites.equipmentshop.security.domain.UserData;
import com.sites.equipmentshop.security.endpoints.dto.ForgotPasswordDTO;
import com.sites.equipmentshop.security.endpoints.dto.ModifyUserDTO;
import com.sites.equipmentshop.security.endpoints.dto.NewUserDTO;
import com.sites.equipmentshop.security.endpoints.dto.ResetPasswordDTO;
import com.sites.equipmentshop.security.endpoints.dto.UpdateUserDTO;
import com.sites.equipmentshop.security.endpoints.dto.UserDTO;
import com.sites.equipmentshop.security.endpoints.responses.UserResponse;
import com.sites.equipmentshop.security.exceptions.EmailOrUserNameExistsException;
import com.sites.equipmentshop.security.exceptions.PasswordResetNotExistsException;
import com.sites.equipmentshop.security.exceptions.UserNotExistsException;
import com.sites.equipmentshop.security.exceptions.WrongPasswordException;
import com.sites.equipmentshop.security.persistence.entities.UserEntity;
import com.sites.equipmentshop.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping(value = APIConstants.API_ROOT + "/users", consumes = { "application/json" }, produces = {"application/json"})
public class UsersController {

    private UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    /* Covered*/
    @PostMapping(value = "/registration")
    public UserResponse registration(@RequestBody @Validated NewUserDTO user) {
        return new UserResponse(userService.createNewUser(user));
    }

    // Covered
    @PostMapping(value = "/login")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void login(@RequestBody AccountCredentials creds) {
        //It's fake endpoint to show login API in Swagger doc
    }

    // Covered
    @PutMapping(value = "/{id}")
    public UserResponse updateUser(@PathVariable("id") String id, @RequestBody @Validated UpdateUserDTO user) {
        return new UserResponse(userService.updateUser(id, user));
    }

    // Covered
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") String id) {
        userService.deleteUserById(id);
    }

    // Covered
    @GetMapping(value = "/{id}")
    public UserDTO getUserById(@PathVariable("id") String id) {
        return new UserDTO(userService.getUserById(id));
    }

    // Covered
    @GetMapping(value = "/")
    public Collection<UserDTO> getAllUsers() {
        Collection<UserEntity> entities = userService.getAllUsers();
        Collection<UserDTO> dtos = new ArrayList<>();
        for (UserEntity entity : entities) {
            dtos.add(new UserDTO(entity));
        }
        return dtos;
    }

    // Covered
    @PutMapping(value = "{id}/modify")
    public UserData modifyUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id, @RequestBody @Validated ModifyUserDTO modifyDto) {
        return userService.modifyUser(request, response, modifyDto, id);
    }

    @PostMapping(value = "/password/forgot")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void forgotPassword(@RequestBody @Validated ForgotPasswordDTO email) {
        userService.passwordToReset(email.email);
    }

    @PutMapping(value = "/password/reset")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void resetPassword(@RequestBody @Validated ResetPasswordDTO rpDto) {
        userService.resetPassword(rpDto);
    }

    @ExceptionHandler(PasswordResetNotExistsException.class)
    public void prNotExists(HttpServletResponse response, PasswordResetNotExistsException ex) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(UserNotExistsException.class)
    public void userNotExists(HttpServletResponse response, UserNotExistsException ex) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(EmailOrUserNameExistsException.class)
    public void emailExists(HttpServletResponse response, EmailOrUserNameExistsException ex) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(WrongPasswordException.class)
    public void emailExists(HttpServletResponse response, WrongPasswordException ex) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void validationError(HttpServletResponse response, MethodArgumentNotValidException ex) throws IOException {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        if (errors.iterator().hasNext()) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), errors.iterator().next().getDefaultMessage());
        } else {
            response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        }
    }

}
