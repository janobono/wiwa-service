package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.auth.*;
import sk.janobono.wiwa.api.model.user.UserWebDto;
import sk.janobono.wiwa.api.service.AuthApiService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthApiService authApiService;

    @PostMapping("/confirm")
    public AuthenticationResponseWebDto confirm(@Valid @RequestBody final ConfirmationWebDto confirmation) {
        return authApiService.confirm(confirmation);
    }

    @PostMapping("/change-email")
    public AuthenticationResponseWebDto changeEmail(@Valid @RequestBody final ChangeEmailWebDto changeEmail) {
        return authApiService.changeEmail(changeEmail);
    }

    @PostMapping("/change-password")
    public AuthenticationResponseWebDto changePassword(@Valid @RequestBody final ChangePasswordWebDto changePassword) {
        return authApiService.changePassword(changePassword);
    }

    @PostMapping("/change-user-details")
    public AuthenticationResponseWebDto changeUserDetails(@Valid @RequestBody final ChangeUserDetailsWebDto changeUserDetails) {
        return authApiService.changeUserDetails(changeUserDetails);
    }

    @PostMapping("/resend-confirmation")
    public void resendConfirmation(@Valid @RequestBody final ResendConfirmationWebDto resendConfirmation) {
        authApiService.resendConfirmation(resendConfirmation);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody final ResetPasswordWebDto resetPassword) {
        authApiService.resetPassword(resetPassword);
    }

    @PostMapping("/sign-in")
    public AuthenticationResponseWebDto signIn(@Valid @RequestBody final SignInWebDto signIn) {
        return authApiService.signIn(signIn);
    }

    @PostMapping("/sign-up")
    public AuthenticationResponseWebDto signUp(@Valid @RequestBody final SignUpWebDto signUp) {
        return authApiService.signUp(signUp);
    }

    @PostMapping("/refresh")
    public AuthenticationResponseWebDto refresh(@Valid @RequestBody final RefreshTokenWebDto refreshToken) {
        return authApiService.refresh(refreshToken);
    }

    @GetMapping("/user-detail")
    public UserWebDto getUserDetail() {
        return authApiService.getUserDetail();
    }
}
