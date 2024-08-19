package pl.pas.gr3.cinema.controllers.implementations;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.aspects.logging.LoggerInterceptor;
import pl.pas.gr3.cinema.common.dto.authentication.AccessTokenOutputDTO;
import pl.pas.gr3.cinema.common.dto.authentication.AccountCredentialLoginDTO;
import pl.pas.gr3.cinema.controllers.interfaces.IAuthenticationController;
import pl.pas.gr3.cinema.exceptions.authentication.AccountBlockedException;
import pl.pas.gr3.cinema.exceptions.authentication.AccountInactiveException;
import pl.pas.gr3.cinema.exceptions.authentication.ApplicationInvalidAuthenticationException;
import pl.pas.gr3.cinema.model.users.Account;
import pl.pas.gr3.cinema.services.interfaces.IAccountService;
import pl.pas.gr3.cinema.utils.providers.JWTService;

@RestController
@LoggerInterceptor
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NEVER)
public class AuthenticationController implements IAuthenticationController {

    private final IAccountService accountService;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;

    // Login methods

    @PreAuthorize("hasRole('ANONYMOUS')")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public ResponseEntity<?> loginWithCredentials(AccountCredentialLoginDTO loginDTO) {
        try {
            this.authManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.login(), loginDTO.password()));
            Account account = this.accountService.findByLogin(loginDTO.login());
            String accessToken = this.jwtService.generateJWTToken(account);
            return ResponseEntity.ok(new AccessTokenOutputDTO(accessToken));
        } catch (DisabledException exception) {
            throw new AccountInactiveException();
        } catch (LockedException exception) {
            throw new AccountBlockedException();
        } catch (BadCredentialsException | UsernameNotFoundException exception) {
            throw new ApplicationInvalidAuthenticationException();
        }
    }

    // Logout method

    @PreAuthorize("hasRole('AUTHENTICATED')")
    @Override
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.noContent().build();
    }
}
