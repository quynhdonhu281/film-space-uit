package com.example.filmspace_mobile.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.filmspace_mobile.data.model.auth.LoginResponse;
import com.example.filmspace_mobile.data.repository.AuthRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for AuthViewModel
 * 
 * These tests demonstrate the benefits of Repository Pattern:
 * - No network calls needed
 * - Easy to mock repository
 * - Fast test execution
 * - Can test all scenarios (success, error, loading states)
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AuthRepository mockAuthRepository;

    @Mock
    private Observer<LoginResponse> loginResponseObserver;

    @Mock
    private Observer<String> loginErrorObserver;

    @Mock
    private Observer<Boolean> loginLoadingObserver;

    private AuthViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new AuthViewModel(mockAuthRepository);
    }

    @Test
    public void testLogin_Success() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        LoginResponse expectedResponse = createMockLoginResponse();

        // Capture the callback
        ArgumentCaptor<AuthRepository.RepositoryCallback<LoginResponse>> callbackCaptor =
                ArgumentCaptor.forClass(AuthRepository.RepositoryCallback.class);

        // When
        viewModel.getLoginResponse().observeForever(loginResponseObserver);
        viewModel.getLoginLoading().observeForever(loginLoadingObserver);
        viewModel.login(email, password);

        // Verify loading is set to true
        verify(loginLoadingObserver).onChanged(true);

        // Capture the callback and trigger success
        verify(mockAuthRepository).login(eq(email), eq(password), callbackCaptor.capture());
        callbackCaptor.getValue().onSuccess(expectedResponse);

        // Then
        verify(loginLoadingObserver).onChanged(false);
        verify(loginResponseObserver).onChanged(expectedResponse);
        verifyNoInteractions(loginErrorObserver);
    }

    @Test
    public void testLogin_Error_InvalidCredentials() {
        // Given
        String email = "test@example.com";
        String password = "wrong_password";
        String expectedError = "Invalid email or password";

        ArgumentCaptor<AuthRepository.RepositoryCallback<LoginResponse>> callbackCaptor =
                ArgumentCaptor.forClass(AuthRepository.RepositoryCallback.class);

        // When
        viewModel.getLoginError().observeForever(loginErrorObserver);
        viewModel.getLoginLoading().observeForever(loginLoadingObserver);
        viewModel.login(email, password);

        // Verify loading is set to true
        verify(loginLoadingObserver).onChanged(true);

        // Capture the callback and trigger error
        verify(mockAuthRepository).login(eq(email), eq(password), callbackCaptor.capture());
        callbackCaptor.getValue().onError(expectedError);

        // Then
        verify(loginLoadingObserver).onChanged(false);
        verify(loginErrorObserver).onChanged(expectedError);
        verifyNoInteractions(loginResponseObserver);
    }

    @Test
    public void testLogin_Error_NoInternet() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        String expectedError = "No internet connection. Please check your network.";

        ArgumentCaptor<AuthRepository.RepositoryCallback<LoginResponse>> callbackCaptor =
                ArgumentCaptor.forClass(AuthRepository.RepositoryCallback.class);

        // When
        viewModel.getLoginError().observeForever(loginErrorObserver);
        viewModel.getLoginLoading().observeForever(loginLoadingObserver);
        viewModel.login(email, password);

        // Capture the callback and trigger network error
        verify(mockAuthRepository).login(eq(email), eq(password), callbackCaptor.capture());
        callbackCaptor.getValue().onError(expectedError);

        // Then
        verify(loginLoadingObserver).onChanged(false);
        verify(loginErrorObserver).onChanged(expectedError);
    }

    @Test
    public void testRegister_Success() {
        // This test would be similar to login test
        // Demonstrates that all auth operations can be easily tested
        // TODO: Implement when needed
    }

    @Test
    public void testVerifyOTP_Success() {
        // This test would verify OTP verification flow
        // TODO: Implement when needed
    }

    // Helper method to create mock response
    private LoginResponse createMockLoginResponse() {
        LoginResponse response = new LoginResponse();
        response.setUserId(1);
        response.setUsername("testuser");
        response.setEmail("test@example.com");
        response.setToken("mock_jwt_token");
        response.setName("Test User");
        return response;
    }
}
