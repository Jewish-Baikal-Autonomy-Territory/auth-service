package org.example.ai_auth_service.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.ai_auth_service.dto.JwtAuthenticationResponse;
import org.example.ai_auth_service.dto.RefreshTokenRequest;
import org.example.ai_auth_service.dto.SignInRequest;
import org.example.ai_auth_service.dto.SignUpRequest;
import org.example.ai_auth_service.entity.User;
import org.example.ai_auth_service.proto.auth.*;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.example.ai_auth_service.services.AuthenticationService;
import org.example.ai_auth_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@GrpcService
public class AuthGrpcService extends AuthGrpc.AuthImplBase {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Override
    public void login(UserCredentials request, StreamObserver<TokenPair> responseObserver) {
        String email = request.getEmail();
        String password = request.getPassword();
        User user = userService.getByEmail(email);
        String phoneNumber = user.getPhoneNumber();
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail(email);
        signInRequest.setPassword(password);
        JwtAuthenticationResponse jwtResponse = authenticationService.signIn(signInRequest);
        TokenPair tokenPair = TokenPair.newBuilder()
                .setAccessToken(jwtResponse.getToken())
                .setRefreshToken("")
                .build();
        responseObserver.onNext(tokenPair);
        responseObserver.onCompleted();
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<Empty> responseObserver) {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(request.getEmail());
        signUpRequest.setPassword(request.getPassword());
        signUpRequest.setPhoneNumber(request.getPhoneNumber());
        signUpRequest.setFirstName(request.getFirstName());
        signUpRequest.setLastName(request.getLastName());
        signUpRequest.setMiddleName(request.getMiddleName().isEmpty() ? null : request.getMiddleName());
        authenticationService.signUp(signUpRequest);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void refresh(RefreshRequest request, StreamObserver<TokenPair> responseObserver) {
        RefreshTokenRequest dto = new RefreshTokenRequest();
        dto.setRefreshToken(request.getRefreshToken());
        try {
            JwtAuthenticationResponse newTokens = authenticationService.refreshAccessToken(dto);
            TokenPair tokenPair = TokenPair.newBuilder()
                    .setAccessToken(newTokens.getToken())
                    .setRefreshToken(newTokens.getRefreshToken())
                    .build();
            responseObserver.onNext(tokenPair);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void changePassword(ChangePasswordRequest request, StreamObserver<Empty> responseObserver) {
        User currentUser = getCurrentUserFromContext();
        if (currentUser == null) {
            responseObserver.onError(Status.UNAUTHENTICATED.asRuntimeException());
            return;
        }
        boolean verified = userService.verifyPassword(currentUser.getEmail(), request.getOldPassword());
        if (!verified) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Old password is incorrect").asRuntimeException());
            return;
        }
        currentUser.setPassword(new BCryptPasswordEncoder().encode(request.getNewPassword()));
        userService.save(currentUser);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    private User getCurrentUserFromContext() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User user) {
            return user;
        }
        return null;
    }
}