package com.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.settings.form.TagForm;
import com.studyolle.settings.form.ZoneForm;
import com.studyolle.tag.TagRepository;
import com.studyolle.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.studyolle.settings.SettingsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ZoneRepository zoneRepository;

    private Zone testZone = Zone.builder().city("?????????").localNameOfCity("????????????").province("????????????").build();

    @BeforeEach
    void create() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("jongchan");
        signUpForm.setEmail("kkj8219@naver.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
        zoneRepository.save(testZone);
    }

    @AfterEach
    void delete() {
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ???")
    @Test
    void update_zones_form() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ZONES))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + ZONES))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("whitelist"));
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ??????")
    @Test
    void update_zones() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm)))
                .andExpect(status().isOk());

        Account jongchan = accountRepository.findByNickname("jongchan");
        Zone byCityAndProvince = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(jongchan.getZones().contains(byCityAndProvince));
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("?????? ?????? ??????")
    @Test
    void update_zones_remove() throws Exception {
        Account jongchan = accountRepository.findByNickname("jongchan");
        accountService.addZone(jongchan, testZone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/remove")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm)))
                .andExpect(status().isOk());

        Zone byCityAndProvince = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertFalse(jongchan.getZones().contains(byCityAndProvince));
    }


    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? ?????? ???")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + TAGS))
                .andExpect(view().name(SETTINGS + TAGS))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? ??????")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account jongchan = accountRepository.findByNickname("jongchan");
        assertTrue(jongchan.getTags().contains(newTag));
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? ??????")
    @Test
    void removeTag() throws Exception {
        Account jongchan = accountRepository.findByNickname("jongchan");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(jongchan, newTag);

        assertTrue(jongchan.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(jongchan.getTags().contains(newTag));
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? ???")
    @Test
    void updateAccountForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ACCOUNT))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? - ????????? ??????")
    @Test
    void updateAccount_success() throws Exception {
        String newNickname = "whiteship";
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                        .param("nickname", newNickname)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + ACCOUNT))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname("whiteship"));
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? - ????????? ??????")
    @Test
    void updateAccount_failure() throws Exception {
        String newNickname = "??\\_(???)_/??";
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                        .param("nickname", newNickname)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + ACCOUNT))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ?????? ???")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + PROFILE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? - ????????? ??????")
    @Test
    void updateProfile() throws Exception {
        String bio = "?????? ????????? ???????????? ??????.";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PROFILE))
                .andExpect(flash().attributeExists("message"));

        Account jongchan = accountRepository.findByNickname("jongchan");
        assertEquals(bio, jongchan.getBio());
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("????????? ???????????? - ????????? ??????")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "?????? ????????? ???????????? ??????. ?????? ????????? ???????????? ??????. ?????? ????????? ???????????? ??????. ???????????? ?????? ????????? ???????????? ??????. ";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PROFILE))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account jongchan = accountRepository.findByNickname("jongchan");
        assertNull(jongchan.getBio());
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("???????????? ?????? ???")
    @Test
    void updatePassword_form() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + PASSWORD))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("???????????? ?????? - ????????? ??????")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                        .param("newPassword", "12345678")
                        .param("newPasswordConfirm", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PASSWORD))
                .andExpect(flash().attributeExists("message"));

        Account jongchan = accountRepository.findByNickname("jongchan");
        assertTrue(passwordEncoder.matches("12345678", jongchan.getPassword()));
    }

    @WithUserDetails(value = "jongchan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("???????????? ?????? - ????????? ?????? - ???????????? ?????????")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                        .param("newPassword", "12345678")
                        .param("newPasswordConfirm", "11111111")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PASSWORD))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }
}
