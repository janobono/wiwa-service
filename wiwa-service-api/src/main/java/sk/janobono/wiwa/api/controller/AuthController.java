package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public AuthenticationResponseSo confirm(@Valid @RequestBody final ConfirmationSo confirmationSo) {
        log.debug("confirm({})", confirmationSo);
        return authService.confirm(confirmationSo);
    }

    @PostMapping("/change-email")
    public AuthenticationResponseSo changeEmail(@Valid @RequestBody final ChangeEmailSo changeEmailSo) {
        log.debug("changeEmail({})", changeEmailSo);
        return authService.changeEmail(changeEmailSo);
    }

    @PostMapping("/change-password")
    public AuthenticationResponseSo changePassword(@Valid @RequestBody final ChangePasswordSo changePasswordSo) {
        log.debug("changePassword({})", changePasswordSo);
        return authService.changePassword(changePasswordSo);
    }

    @PostMapping("/change-user-details")
    public AuthenticationResponseSo changeUserDetails(@Valid @RequestBody final ChangeUserDetailsSo changeUserDetailsSo) {
        log.debug("changeUserDetails({})", changeUserDetailsSo);
        return authService.changeUserDetails(changeUserDetailsSo);
    }

    @PostMapping("/resend-confirmation")
    public void resendConfirmation(@Valid @RequestBody final ResendConfirmationSo resendConfirmationSo) {
        log.debug("resendConfirmation({})", resendConfirmationSo);
        authService.resendConfirmation(resendConfirmationSo);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody final ResetPasswordSo resetPasswordSo) {
        log.debug("resetPassword({})", resetPasswordSo);
        authService.resetPassword(resetPasswordSo);
    }

    @PostMapping("/sign-in")
    public AuthenticationResponseSo signIn(@Valid @RequestBody final SignInSo signInSo) {
        log.debug("signIn({})", signInSo);
        return authService.signIn(signInSo);
    }

    @PostMapping("/sign-up")
    public AuthenticationResponseSo signUp(@Valid @RequestBody final SignUpSo signUpSo) {
        log.debug("signUp({})", signUpSo);
        return authService.signUp(signUpSo);
    }

    @PostMapping("/refresh")
    public AuthenticationResponseSo refresh(@Valid @RequestBody final RefreshTokenSo refreshTokenSo) {
        log.debug("refresh({})", refreshTokenSo);
        return authService.refresh(refreshTokenSo);
    }
}
