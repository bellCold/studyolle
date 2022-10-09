package com.studyolle.settings;

import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;
import com.studyolle.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void create() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("jongchan");
        signUpForm.setEmail("kkj8219@naver.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void delete() {
        accountRepository.deleteAll();
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("수정 성공")
    void update_profile() throws Exception {
        String update = "update";
        mockMvc.perform(post(SettingsController.SETTING_PROFILE_URL)
                        .param("bio", update)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTING_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account jongchan = accountRepository.findByNickname("jongchan");
        assertEquals(update, jongchan.getBio());
    }

    @Test
    @DisplayName("수정 실패")
    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void update_profile_fail() throws Exception {
        String update = "가나다라마바사가나다라마바사가나다라마바사가나다라마바사가나다라마바사가나다라마바사";
        mockMvc.perform(post(SettingsController.SETTING_PROFILE_URL)
                        .param("bio", update)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account jongchan = accountRepository.findByNickname("jongchan");
        assertNull(jongchan.getBio());
    }

    @Test
    @DisplayName("프로필 수정폼")
    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void profile_update_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @Test
    @DisplayName("비멀번호 수정 테스트")
    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void password_update() throws Exception {
        mockMvc.perform(post(SettingsController.SETTING_PASSWORD_URL)
                        .param("newPassword", "!12rlawhdcks")
                        .param("newPasswordConfirm", "!12rlawhdcks")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTING_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account jonghan = accountRepository.findByNickname("jongchan");
        assertTrue(passwordEncoder.matches("!12rlawhdcks",jonghan.getPassword()));
    }

    @Test
    @DisplayName("비멀번호 수정 실패 테스트")
    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void password_update_fail() throws Exception {
        mockMvc.perform(post(SettingsController.SETTING_PASSWORD_URL)
                        .param("newPassword", "!12rlawhdcks")
                        .param("newPasswordConfirm", "asdfbasdf")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @Test
    @DisplayName("비밀번호 수정 폼")
    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void password_update_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTING_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

}