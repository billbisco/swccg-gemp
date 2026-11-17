package com.gempukku.swccgo.async.handler;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Play's own password sign-in and sign-up are closed (swccg-guild/work#264): everyone uses swccg.com/login. */
public class ClassicSignInClosedTests {
    private static final Path MAIN = Paths.get("src", "main");

    private static String read(String path) throws IOException {
        return Files.readString(MAIN.resolve(path), StandardCharsets.UTF_8);
    }

    @Test
    public void LoginAndRegisterRoutesAnswer404() throws IOException {
        //test1: /login and /register are routed to a 404, and no handler for them is created any more.
        String root = read("java/com/gempukku/swccgo/async/handler/RootUriRequestHandler.java");
        int route = root.indexOf("uri.startsWith(_serverContextPath + \"login\") || uri.startsWith(_serverContextPath + \"register\")");
        assertTrue(route > 0, "login/register route missing");
        assertTrue(root.indexOf("throw new HttpProcessingException(404);", route) > route, "login/register must answer 404");
        assertFalse(root.contains("new LoginRequestHandler("), "classic sign-in handler still created");
        assertFalse(root.contains("new RegisterRequestHandler("), "classic sign-up handler still created");
        assertFalse(Files.exists(MAIN.resolve("java/com/gempukku/swccgo/async/handler/LoginRequestHandler.java")));
    }

    @Test
    public void BrowserCodeNoLongerPostsPasswords() throws IOException {
        //test2: the web client has no password POST to /login or /register, and the old sign-in box script is gone.
        String comm = read("web/js/gemp-016/communication.js");
        assertFalse(comm.contains("this.url + \"/login\""), "communication.js still posts to /login");
        assertFalse(comm.contains("this.url + \"/register\""), "communication.js still posts to /register");
        assertFalse(Files.exists(MAIN.resolve("web/js/swccg-login-box.js")), "js/swccg-login-box.js still exists");
    }
}
