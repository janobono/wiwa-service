package sk.janobono.wiwa.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.business.model.auth.*;
import sk.janobono.wiwa.business.service.AuthService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/confirm")
    public ResponseEntity<AuthenticationResponseSo> confirm(@Valid @RequestBody ConfirmationSo confirmationSo) {
        log.debug("confirm({})", confirmationSo);
        return new ResponseEntity<>(authService.confirm(confirmationSo), HttpStatus.OK);
    }

    @PostMapping("/change-email")
    public ResponseEntity<AuthenticationResponseSo> changeEmail(@Valid @RequestBody ChangeEmailSo changeEmailSo) {
        log.debug("changeEmail({})", changeEmailSo);
        return new ResponseEntity<>(authService.changeEmail(changeEmailSo), HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<AuthenticationResponseSo> changePassword(@Valid @RequestBody ChangePasswordSo changePasswordSo) {
        log.debug("changePassword({})", changePasswordSo);
        return new ResponseEntity<>(authService.changePassword(changePasswordSo), HttpStatus.OK);
    }

    @PostMapping("/change-user-details")
    public ResponseEntity<AuthenticationResponseSo> changeUserDetails(@Valid @RequestBody ChangeUserDetailsSo changeUserDetailsSo) {
        log.debug("changeUserDetails({})", changeUserDetailsSo);
        return new ResponseEntity<>(authService.changeUserDetails(changeUserDetailsSo), HttpStatus.OK);
    }

    @PostMapping("/resend-confirmation")
    public void resendConfirmation(@Valid @RequestBody ResendConfirmationSo resendConfirmationSo) {
        log.debug("resendConfirmation({})", resendConfirmationSo);
        authService.resendConfirmation(resendConfirmationSo);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordSo resetPasswordSo) {
        log.debug("resetPassword({})", resetPasswordSo);
        authService.resetPassword(resetPasswordSo);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticationResponseSo> signIn(@Valid @RequestBody SignInSo signInSo) {
        log.debug("signIn({})", signInSo);
        return new ResponseEntity<>(authService.signIn(signInSo), HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthenticationResponseSo> signUp(@Valid @RequestBody SignUpSo signUpSo) {
        log.debug("signUp({})", signUpSo);
        return new ResponseEntity<>(authService.signUp(signUpSo), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponseSo> refresh(@Valid @RequestBody RefreshTokenSo refreshTokenSo) {
        log.debug("refresh({})", refreshTokenSo);
        return new ResponseEntity<>(authService.refresh(refreshTokenSo), HttpStatus.OK);
    }
}
