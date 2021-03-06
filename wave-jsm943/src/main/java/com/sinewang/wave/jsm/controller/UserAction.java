package com.sinewang.wave.jsm.controller;

import com.sinewang.wave.jsm.model.Action;
import com.sinewang.wave.jsm.model.RESTResult;
import com.sinewang.wave.jsm.model.User;
import com.sinewang.wave.jsm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by wangyanjiong on 6/15/15.
 */
@RestController
public class UserAction {


    private final int CODE_BASE = 101;

    private final String SESSION_KEY_USER = "wave-user";

    public enum UserCode {
        cannot_find_user,
        username_or_password_error,
        get_actions_by_user_module_error
    }

    @Autowired
    private UserService service;

    @RequestMapping("/user")
    public RESTResult user(HttpServletRequest request) {
        String userId = (String) request.getSession().getAttribute(SESSION_KEY_USER);

        User user = service.getUser(userId, "system");
        if (user != null) {
            return RESTResult.newSuccess(user);
        } else {
            return RESTResult.newFailed(CODE_BASE, UserCode.cannot_find_user);
        }
    }

    @RequestMapping("/user/actions")
    public RESTResult userActions(@RequestParam String moduleId, HttpServletRequest request) {
        String userId = (String) request.getSession().getAttribute(SESSION_KEY_USER);

        List<Action> actions = service.getActionsByUserModule(userId, moduleId);
        if (actions != null) {
            return RESTResult.newSuccess(actions);
        } else {
            return RESTResult.newFailed(CODE_BASE, UserCode.get_actions_by_user_module_error);
        }
    }

    @RequestMapping("/login")
    public RESTResult login(@RequestParam String username, @RequestParam String password, HttpServletRequest request, HttpServletResponse response) {
        if (username.equals("admin")) {
            request.getSession().setAttribute(SESSION_KEY_USER, username);
            return RESTResult.newSuccess("OK");
        } else {
            return RESTResult.newFailed(CODE_BASE, UserCode.username_or_password_error);
        }
    }

    @RequestMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute(SESSION_KEY_USER);
        try {
            response.sendRedirect("/signin");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}