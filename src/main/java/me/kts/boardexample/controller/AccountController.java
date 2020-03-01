package me.kts.boardexample.controller;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.service.AccountService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/account")
@Slf4j
public class AccountController {


    private static String authorizationRequestBaseUri
            = "oauth2/authorization";
    private final AccountService accountService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    Map<String, String> oauth2AuthenticationUrls
            = new HashMap<>();

    public AccountController(AccountService accountService, ClientRegistrationRepository clientRegistrationRepository) {
        this.accountService = accountService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @GetMapping("/detail/{userId}")
    public String detail(Model model,
                         @PathVariable String userId) {
        Account account = accountService.getInfo(userId);
        if (account == null) {
            model.addAttribute("message", "fail to get user info");
            return "index";
        } else {
            model.addAttribute("account", account);
        }
        return "account/detail";
    }

    @GetMapping("/delete/{accountId}")
    public String delete(@PathVariable String accountId,
                         RedirectAttributes attributes,
                         Model model) {
        if (accountService.deleteUser(accountId)) {
            SecurityContextHolder.clearContext();
            attributes.addFlashAttribute("message", "delete success");
            return "redirect:/account/loginPage";
        } else {
            model.addAttribute("message", "delete fail");
            return "account/detail";
        }
    }

    @PostMapping("/detail")
    public String update(RedirectAttributes attributes,
                         @Valid Account account) {
        if (accountService.updateInfo(account)) {
            attributes.addFlashAttribute("message", "update success");
        } else {
            attributes.addFlashAttribute("message", "update fail");
        }
        return "redirect:/account/detail";
    }

    @GetMapping("/loginPage")
    public String loginPage() {
        return "account/login";
    }

//    @GetMapping("/oauth_login")
//    public String oauthLoginPage(Model model){
//        Iterable<ClientRegistration> clientRegistrations = null;
//        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
//                .as(Iterable.class);
//        if (type != ResolvableType.NONE &&
//                ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
//            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
//        }
//
//        clientRegistrations.forEach(registration ->
//                oauth2AuthenticationUrls.put(registration.getClientName(),
//                        authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
//        model.addAttribute("urls", oauth2AuthenticationUrls);
//        return "account/oauth_login";
//    }

    @GetMapping("/signUp")
    public String signUpPage() {
        return "account/signUp";
    }


    @PostMapping("/signUp")
    public String signUp(Model model,
                         @Valid Account account,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(c -> {
                model.addAttribute("message", c.getField() + " : " + c.getDefaultMessage());
            });
            return "/account/signUp";
        }
        if (accountService.signUpCheck(account)) {
            if (accountService.signUp(account)) {
                model.addAttribute("message", "signUp success");
                return "account/login";
            }
        }
        model.addAttribute("message", "signUp fail");
        return "/account/signUp";
    }

    @GetMapping("/list")
    public String AccountList(Model model) {
        model.addAttribute("list", accountService.getUsers());
        return "/account/list";
    }


}

