package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.AuthWebMapper;
import sk.janobono.wiwa.api.model.auth.*;
import sk.janobono.wiwa.business.model.auth.AuthenticationResponseData;
import sk.janobono.wiwa.business.service.AuthService;

@RequiredArgsConstructor
@Service
public class AuthApiService {

    private final AuthService authService;
    private final AuthWebMapper authWebMapper;

    public AuthenticationResponseWebDto confirm(final ConfirmationWebDto confirmation) {
        return mapToWebDto(authService.confirm(authWebMapper.mapToData(confirmation)));
    }

    public AuthenticationResponseWebDto changeEmail(final ChangeEmailWebDto changeEmail) {
        return mapToWebDto(authService.changeEmail(authWebMapper.mapToData(changeEmail)));
    }

    public AuthenticationResponseWebDto changePassword(final ChangePasswordWebDto changePassword) {
        return mapToWebDto(authService.changePassword(authWebMapper.mapToData(changePassword)));
    }

    public AuthenticationResponseWebDto changeUserDetails(final ChangeUserDetailsWebDto changeUserDetails) {
        return mapToWebDto(authService.changeUserDetails(authWebMapper.mapToData(changeUserDetails)));
    }

    public void resendConfirmation(final ResendConfirmationWebDto resendConfirmation) {
        authService.resendConfirmation(authWebMapper.mapToData(resendConfirmation));
    }

    public void resetPassword(final ResetPasswordWebDto resetPassword) {
        authService.resetPassword(authWebMapper.mapToData(resetPassword));
    }

    public AuthenticationResponseWebDto signIn(final SignInWebDto signIn) {
        return mapToWebDto(authService.signIn(authWebMapper.mapToData(signIn)));
    }

    public AuthenticationResponseWebDto signUp(final SignUpWebDto signUp) {
        return mapToWebDto(authService.signUp(authWebMapper.mapToData(signUp)));
    }

    public AuthenticationResponseWebDto refresh(final RefreshTokenWebDto refreshToken) {
        return mapToWebDto(authService.refresh(authWebMapper.mapToData(refreshToken)));
    }

    private AuthenticationResponseWebDto mapToWebDto(final AuthenticationResponseData authenticationResponseData) {
        return new AuthenticationResponseWebDto(authenticationResponseData.token(), authenticationResponseData.refreshToken());
    }
}
