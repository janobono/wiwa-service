package sk.janobono.wiwa.business.service;

import sk.janobono.wiwa.business.model.auth.*;
import sk.janobono.wiwa.model.User;

public interface AuthService {

    AuthenticationResponseData confirm(ConfirmationData confirmation);

    AuthenticationResponseData changeEmail(User user, ChangeEmailData changeEmail);

    AuthenticationResponseData changePassword(User user, ChangePasswordData changePassword);

    AuthenticationResponseData changeUserDetails(User user, ChangeUserDetailsData changeUserDetails);

    void resendConfirmation(User user, ResendConfirmationData resendConfirmation);

    void resetPassword(ResetPasswordData resetPassword);

    AuthenticationResponseData signIn(SignInData signIn);

    AuthenticationResponseData signUp(SignUpData signUp);

    AuthenticationResponseData refresh(RefreshTokenData refreshToken);
}
