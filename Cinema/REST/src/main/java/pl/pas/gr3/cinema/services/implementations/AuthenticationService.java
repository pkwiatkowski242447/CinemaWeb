package pl.pas.gr3.cinema.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.exceptions.repositories.UserRepositoryException;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.model.users.User;
import pl.pas.gr3.cinema.repositories.implementations.UserRepository;
import pl.pas.gr3.cinema.security.JWTService;
import pl.pas.gr3.cinema.services.interfaces.AuthenticationServiceInterface;
import pl.pas.gr3.dto.auth.LoginOutputDTO;
import pl.pas.gr3.dto.auth.RegisterOutputDTO;
import pl.pas.gr3.dto.auth.UserOutputDTO;

@Service
public class AuthenticationService implements AuthenticationServiceInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public RegisterOutputDTO registerClient(String clientUsername, String clientPassword) {
        try {
            Client newClient = userRepository.createClient(clientUsername, passwordEncoder.encode(clientPassword));
            return this.generateRegisterOutputDTO(newClient);
        } catch (UserRepositoryException exception) {
            return null;
        }
    }

    @Override
    public RegisterOutputDTO registerAdmin(String adminUsername, String adminPassword) {
        try {
            Admin newAdmin = userRepository.createAdmin(adminPassword, adminPassword);
            return this.generateRegisterOutputDTO(newAdmin);
        } catch (UserRepositoryException exception) {
            return null;
        }
    }

    @Override
    public RegisterOutputDTO registerStaff(String staffUsername, String staffPassword) {
        try {
            Staff newStaff = userRepository.createStaff(staffUsername, staffPassword);
            return this.generateRegisterOutputDTO(newStaff);
        } catch (UserRepositoryException exception) {
            return null;
        }
    }

    @Override
    public LoginOutputDTO loginClient(String clientUsername, String clientPassword) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(clientUsername, clientPassword));
        try {
            Client client = userRepository.findClientByLogin(clientUsername);
            return this.generateLoginOutputDTO(client);
        } catch (UserRepositoryException exception) {
            return null;
        }
    }

    @Override
    public LoginOutputDTO loginAdmin(String adminUsername, String adminPassword) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(adminUsername, adminPassword));
        try {
            Admin admin = userRepository.findAdminByLogin(adminUsername);
            return this.generateLoginOutputDTO(admin);
        } catch (UserRepositoryException exception) {
            return null;
        }
    }

    @Override
    public LoginOutputDTO loginStaff(String staffUsername, String staffPassword) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(staffUsername, staffPassword));
        try {
            Staff staff = userRepository.findStaffByLogin(staffUsername);
            return this.generateLoginOutputDTO(staff);
        } catch (UserRepositoryException exception) {
            return null;
        }
    }

    private RegisterOutputDTO generateRegisterOutputDTO(User user) {
        String jwtToken = jwtService.generateJWTToken(user);

        UserOutputDTO userOutputDTO = new UserOutputDTO(user.getUserID(), user.getUserLogin(), user.isUserStatusActive());

        return RegisterOutputDTO
                .builder()
                .user(userOutputDTO)
                .accessToken(jwtToken)
                .build();
    }

    private LoginOutputDTO generateLoginOutputDTO(User user) {
        String jwtToken = jwtService.generateJWTToken(user);

        return LoginOutputDTO
                .builder()
                .accessToken(jwtToken)
                .build();
    }
}
