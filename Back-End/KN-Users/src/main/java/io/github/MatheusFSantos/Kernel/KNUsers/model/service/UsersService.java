package io.github.MatheusFSantos.Kernel.KNUsers.model.service;

import io.github.MatheusFSantos.Kernel.KNUsers.model.annotation.Authorize;
import io.github.MatheusFSantos.Kernel.KNUsers.model.annotation.BusinessCritical;
import io.github.MatheusFSantos.Kernel.KNUsers.model.annotation.InternalUseOnly;
import io.github.MatheusFSantos.Kernel.KNUsers.model.annotation.ReadOnly;
import io.github.MatheusFSantos.Kernel.KNUsers.model.entity.DTO.UsersBackupDTO;
import io.github.MatheusFSantos.Kernel.KNUsers.model.entity.DTO.UsersDTO;
import io.github.MatheusFSantos.Kernel.KNUsers.model.entity.DTO.UsersLoginDTO;
import io.github.MatheusFSantos.Kernel.KNUsers.model.entity.Users;
import io.github.MatheusFSantos.Kernel.KNUsers.model.enumeration.Roles;
import io.github.MatheusFSantos.Kernel.KNUsers.model.exception.UsersException;
import io.github.MatheusFSantos.Kernel.KNUsers.model.exception.specific.UserAlreadyExists;
import io.github.MatheusFSantos.Kernel.KNUsers.model.exception.specific.UserLoginNotValid;
import io.github.MatheusFSantos.Kernel.KNUsers.model.exception.specific.UsersNotFound;
import io.github.MatheusFSantos.Kernel.KNUsers.model.repository.UsersRepository;
import io.github.MatheusFSantos.Kernel.KNUsers.model.util.converter.NicknameConverter;
import io.github.MatheusFSantos.Kernel.KNUsers.model.util.cryptography.algorithms.Base64;
import io.github.MatheusFSantos.Kernel.KNUsers.model.util.validation.UUIDValidation;
import io.github.MatheusFSantos.Kernel.KNUsers.model.util.validation.UsersValidations;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@BusinessCritical
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersBackupService usersBackupService;

    @ReadOnly
    public List<Users> findAll() throws UsersException {
        List<Users> usersList = this.usersRepository.findAll();

        if(!usersList.isEmpty())
            return usersList;

        throw new UsersNotFound("Users not found in database!", "The method was invoked to list all users (find all method), however the database is empty", HttpStatus.SC_NOT_FOUND);
    }

    @ReadOnly
    @Authorize(required=true)
    public Users findById(String id) throws UsersException {
        return this.usersRepository.findById(id).orElseThrow(() -> new UsersNotFound("User not found in database!", "The method was invoked to list a user (method find by id), but the user is not present in the database of this service", HttpStatus.SC_NOT_FOUND));
    }

    @Authorize(required=true)
    public void save(UsersDTO usersDTO) throws UsersException {
        UsersValidations.validation(usersDTO);
        usersDTO.setNickname(NicknameConverter.convert(usersDTO.getNickname()));

        if(this.usersRepository.findByNickname(usersDTO.getNickname()).isPresent() || this.usersRepository.findByEmail(usersDTO.getEmail()).isPresent())
            throw new UserAlreadyExists("User already exists!", "The method was invoked to save one user (save method), however this user already exists.", HttpStatus.SC_CONFLICT);

        Users user = new Users(UUID.randomUUID().toString(), usersDTO.getName(), usersDTO.getNickname(), Base64.encoder(usersDTO.getEmail()), Base64.encoder(usersDTO.getPassword()), usersDTO.getLocation(), usersDTO.getBiography(), Roles.BASIC);

        this.usersBackupService.save(new UsersBackupDTO(user));
        this.usersRepository.save(user);
    }

    public boolean isValidUser(UsersLoginDTO usersLoginDTO) throws UsersException {

        UsersValidations.loginValidation(usersLoginDTO);

        List<Users> usersList = this.findAll();
        boolean response = false;

        String emailDecoded = "";
        String passwordDecoded = "";

        for (Users user : usersList) {
            emailDecoded = Base64.decode(user.getEmail());
            passwordDecoded = Base64.decode(user.getPassword());

            if (emailDecoded.equals(usersLoginDTO.getEmail()) && passwordDecoded.equals(usersLoginDTO.getPassword())) {
                response = true;
                break;
            }
        }

        if(!response) {
            throw new UserLoginNotValid("Users login is not valid!", "The method was invoked to valid one user (isValidUser method), however the user login is invalid", HttpStatus.SC_BAD_REQUEST);
        }

        return response;
    }

    @Authorize(required=true)
    public void update(String id, UsersDTO usersDTO) throws UsersException {
        UUIDValidation.isValidUUID(id);
        UsersValidations.validation(usersDTO);
        usersDTO.setNickname(NicknameConverter.convert(usersDTO.getNickname()));

        Users oldUser = this.findById(id); /* automatically checks if user exists */
        this.updateVerification(oldUser, usersDTO);

        /* Parse DTO to entity */
        Users updatedUser = new Users(id, usersDTO.getName(), usersDTO.getNickname(), usersDTO.getEmail(), usersDTO.getPassword(), usersDTO.getLocation(), usersDTO.getBiography(), oldUser.getRoles(), oldUser.getCreatedAt());
        this.usersRepository.save(updatedUser);
    }

    @Authorize(required=true)
    public void delete(String id) throws UsersException {
        if(this.findById(id) != null)
            this.usersRepository.deleteById(id);
    }

    @InternalUseOnly
    private void updateVerification(Users oldUser, UsersDTO updatedUser) throws UsersException {
        if(!updatedUser.getNickname().equals(oldUser.getNickname()))
            if(this.usersRepository.findByNickname(updatedUser.getNickname()).isPresent()) /* There is another user with the nickname he entered */
                throw new UserAlreadyExists("User already exists!", "The method was invoked to update one user (update method), however this user already exists.", HttpStatus.SC_CONFLICT);

        if(!updatedUser.getEmail().equals(oldUser.getEmail()))
            if(this.usersRepository.findByEmail(updatedUser.getEmail()).isPresent())/* There is another user with the e-mail he entered */
                throw new UserAlreadyExists("User already exists!", "The method was invoked to update one user (update method), however this user already exists.", HttpStatus.SC_CONFLICT);
    }

}
