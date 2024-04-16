package sk.janobono.wiwa.business.service;

import sk.janobono.wiwa.business.model.auth.*;
import sk.janobono.wiwa.model.User;

public interface AuthService {

    AuthenticationResponseData confirm(final ConfirmationData confirmation);

    AuthenticationResponseData changeEmail(final User user, final ChangeEmailData changeEmail);

    AuthenticationResponseData changePassword(final User user, final ChangePasswordData changePassword);

    AuthenticationResponseData changeUserDetails(final User user, final ChangeUserDetailsData changeUserDetails);

    void resendConfirmation(final User user, final ResendConfirmationData resendConfirmation);

    void resetPassword(final ResetPasswordData resetPassword);

    AuthenticationResponseData signIn(final SignInData signIn);

    AuthenticationResponseData signUp(final SignUpData signUp);

    AuthenticationResponseData refresh(final RefreshTokenData refreshToken);
}
