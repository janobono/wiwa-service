package sk.janobono.wiwa.business.service;

import sk.janobono.wiwa.business.model.auth.*;

public interface AuthService {

    AuthenticationResponseSo confirm(ConfirmationSo confirmationSo);

    AuthenticationResponseSo changeEmail(ChangeEmailSo changeEmailSo);

    AuthenticationResponseSo changePassword(ChangePasswordSo changePasswordSo);

    AuthenticationResponseSo changeUserDetails(ChangeUserDetailsSo changeUserDetailsSo);

    void resendConfirmation(ResendConfirmationSo resendConfirmationSo);

    void resetPassword(ResetPasswordSo resetPasswordSo);

    AuthenticationResponseSo signIn(SignInSo signInSo);

    AuthenticationResponseSo signUp(SignUpSo signUpSo);

    AuthenticationResponseSo refresh(RefreshTokenSo refreshTokenSo);
}
