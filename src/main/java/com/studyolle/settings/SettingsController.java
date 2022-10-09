package com.studyolle.settings;

import com.studyolle.account.AccountService;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators((new PasswordFormValidator()));
    }

    static final String SETTING_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTING_PROFILE_URL = "/settings/profile";

    static final String SETTING_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTING_PASSWORD_URL = "/settings/password";

    private final AccountService accountService;

    @GetMapping(SETTING_PROFILE_URL)
    public String updateProfileForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return SETTING_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTING_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTING_PROFILE_VIEW_NAME;
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필이 수정되었습니다.");
        return "redirect:" + SETTING_PROFILE_URL;
    }

    @GetMapping(SETTING_PASSWORD_URL)
    public String updatePasswordForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTING_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTING_PASSWORD_URL)
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm password, Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTING_PASSWORD_VIEW_NAME;
        }

        accountService.passwordUpdate(account, password.getNewPassword());
        attributes.addFlashAttribute("message", "비밀번호가 수정되었습니다.");
        return "redirect:" + SETTING_PASSWORD_URL;
    }

}
