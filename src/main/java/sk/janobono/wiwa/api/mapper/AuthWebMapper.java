package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.auth.*;
import sk.janobono.wiwa.business.model.auth.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AuthWebMapper {
    ConfirmationData mapToData(ConfirmationWebDto confirmation);

    ChangeEmailData mapToData(ChangeEmailWebDto changeEmail);

    ChangePasswordData mapToData(ChangePasswordWebDto changePassword);

    ChangeUserDetailsData mapToData(ChangeUserDetailsWebDto changeUserDetails);

    ResendConfirmationData mapToData(ResendConfirmationWebDto resendConfirmation);

    ResetPasswordData mapToData(ResetPasswordWebDto resetPassword);

    SignInData mapToData(SignInWebDto signIn);

    SignUpData mapToData(SignUpWebDto signUp);

    RefreshTokenData mapToData(RefreshTokenWebDto refreshToken);
}
