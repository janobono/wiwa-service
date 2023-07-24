package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.business.model.auth.*;
import sk.janobono.wiwa.business.service.AuthService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/confirm")
    public AuthenticationResponseSo confirm(@Valid @RequestBody final ConfirmationSo confirmationSo) {
        return authService.confirm(confirmationSo);
    }

    @PostMapping("/change-email")
    public AuthenticationResponseSo changeEmail(@Valid @RequestBody final ChangeEmailSo changeEmailSo) {
        return authService.changeEmail(changeEmailSo);
    }

    @PostMapping("/change-password")
    public AuthenticationResponseSo changePassword(@Valid @RequestBody final ChangePasswordSo changePasswordSo) {
        return authService.changePassword(changePasswordSo);
    }

    @PostMapping("/change-user-details")
    public AuthenticationResponseSo changeUserDetails(@Valid @RequestBody final ChangeUserDetailsSo changeUserDetailsSo) {
        return authService.changeUserDetails(changeUserDetailsSo);
    }

    @PostMapping("/resend-confirmation")
    public void resendConfirmation(@Valid @RequestBody final ResendConfirmationSo resendConfirmationSo) {
        authService.resendConfirmation(resendConfirmationSo);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody final ResetPasswordSo resetPasswordSo) {
        authService.resetPassword(resetPasswordSo);
    }

    @PostMapping("/sign-in")
    public AuthenticationResponseSo signIn(@Valid @RequestBody final SignInSo signInSo) {
        return authService.signIn(signInSo);
    }

    @PostMapping("/sign-up")
    public AuthenticationResponseSo signUp(@Valid @RequestBody final SignUpSo signUpSo) {
        return authService.signUp(signUpSo);
    }

    @PostMapping("/refresh")
    public AuthenticationResponseSo refresh(@Valid @RequestBody final RefreshTokenSo refreshTokenSo) {
        return authService.refresh(refreshTokenSo);
    }
}
