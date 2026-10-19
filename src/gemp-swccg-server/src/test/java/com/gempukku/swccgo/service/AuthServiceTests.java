package com.gempukku.swccgo.service;

import com.gempukku.swccgo.db.LoginInvalidException;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTests {
    private static final String IP = "203.0.113.7";
    private final PasswordHasher hasher = new PasswordHasher(1_000);
    private final AtomicLong now = new AtomicLong(1_700_000_000_000L);
    private InMemoryPlayerDAO dao;
    private AuthService auth;

    @BeforeEach
    public void setUp() {
        dao = new InMemoryPlayerDAO(hasher);
        auth = new AuthService(dao, hasher, new LoginThrottle(), now::get);
    }

    @Test
    public void legacySha256AccountLogsInAndIsUpgradedToPbkdf2() throws Exception {
        dao.addWithStoredHash("Bill", PasswordHasher.legacySha256("oldpass"), "u", null);

        AuthService.AuthResult result = auth.verifyCredentials("Bill", "oldpass", IP);

        assertEquals(AuthService.Status.OK, result.getStatus());
        assertTrue(dao.storedHash("Bill").startsWith("pbkdf2-sha256$"), "hash upgraded on login");
        assertEquals(AuthService.Status.OK, auth.verifyCredentials("Bill", "oldpass", IP).getStatus(),
                "same password still works after the upgrade");
        assertEquals(1, dao.hashUpdates, "no second rehash once upgraded");
    }

    @Test
    public void wrongPasswordAndUnknownNameAreBothInvalid() throws Exception {
        dao.addWithStoredHash("Bill", hasher.hash("rightpass"), "u", null);
        assertEquals(AuthService.Status.INVALID, auth.verifyCredentials("Bill", "wrongpass", IP).getStatus());
        assertEquals(AuthService.Status.INVALID, auth.verifyCredentials("Nobody", "rightpass", IP).getStatus());
        assertNull(auth.verifyCredentials("Nobody", "rightpass", IP).getPlayer());
    }

    @Test
    public void loginIgnoresCaseAndReturnsTheStoredName() throws Exception {
        dao.addWithStoredHash("Bill", hasher.hash("rightpass"), "u", null);
        AuthService.AuthResult result = auth.verifyCredentials("bILL", "rightpass", IP);
        assertEquals(AuthService.Status.OK, result.getStatus());
        assertEquals("Bill", result.getCanonicalName());
    }

    @Test
    public void legacyCaseCollisionNeedsExactCase() throws Exception {
        dao.addWithStoredHash("bill", hasher.hash("lowerpass"), "u", null);
        dao.addWithStoredHash("Bill", hasher.hash("upperpass"), "u", null);
        assertEquals("bill", auth.verifyCredentials("bill", "lowerpass", IP).getCanonicalName());
        assertEquals("Bill", auth.verifyCredentials("Bill", "upperpass", IP).getCanonicalName());
        assertEquals(AuthService.Status.INVALID, auth.verifyCredentials("BILL", "upperpass", IP).getStatus(),
                "ambiguous casing matches neither account");
    }

    @Test
    public void registrationRejectsShortPasswordsAndCaseOnlyDuplicates() throws Exception {
        assertThrows(AuthService.WeakPasswordException.class, () -> auth.register("Newbie", "short", IP));
        assertTrue(auth.register("Newbie", "longenough", IP));
        assertThrows(LoginInvalidException.class, () -> auth.register("NEWBIE", "longenough", IP));
        assertTrue(dao.storedHash("Newbie").startsWith("pbkdf2-sha256$"), "new accounts are salted from the start");
    }

    @Test
    public void bannedTempBannedAndDeactivatedAreReportedAfterAPasswordCheck() throws Exception {
        dao.addWithStoredHash("Perma", hasher.hash("password1"), "", null);
        dao.addWithStoredHash("Temp", hasher.hash("password1"), "", new Date(now.get() + 60_000));
        dao.addWithStoredHash("Expired", hasher.hash("password1"), "", new Date(now.get() - 60_000));
        dao.addWithStoredHash("Gone", hasher.hash("password1"), "ud", null);

        assertEquals(AuthService.Status.BANNED, auth.verifyCredentials("Perma", "password1", IP).getStatus());
        assertEquals(AuthService.Status.TEMP_BANNED, auth.verifyCredentials("Temp", "password1", IP).getStatus());
        assertEquals(AuthService.Status.OK, auth.verifyCredentials("Expired", "password1", IP).getStatus());
        assertEquals(AuthService.Status.DEACTIVATED, auth.verifyCredentials("Gone", "password1", IP).getStatus());
        assertEquals(AuthService.Status.INVALID, auth.verifyCredentials("Perma", "wrong", IP).getStatus(),
                "ban state is not revealed without the password");
    }

    @Test
    public void adminResetKillsOldPasswordAndCodeLetsOwnerSetANewOne() throws Exception {
        Player bill = dao.addWithStoredHash("Bill", hasher.hash("oldpassword"), "u", null);

        String code = auth.issueResetCode("Bill", "AdminAl");
        assertNotNull(code);
        assertEquals("AdminAl", dao.codeCreatedBy(bill.getId()));
        assertEquals(AuthService.Status.INVALID, auth.verifyCredentials("Bill", "oldpassword", IP).getStatus(),
                "old password stops working");

        AuthService.AuthResult withCode = auth.verifyCredentials("bill", code.toLowerCase(), IP);
        assertEquals(AuthService.Status.RESET_REQUIRED, withCode.getStatus());

        String ticket = auth.createResetTicket(withCode.getPlayer());
        assertEquals(AuthService.ResetOutcome.WEAK_PASSWORD, auth.completeReset(ticket, "Bill", "short"));
        assertEquals(AuthService.ResetOutcome.OK, auth.completeReset(ticket, "Bill", "brand-new-pass"));

        assertEquals(AuthService.Status.OK, auth.verifyCredentials("Bill", "brand-new-pass", IP).getStatus());
        assertEquals(AuthService.Status.INVALID, auth.verifyCredentials("Bill", code, IP).getStatus(), "code is single use");
        assertEquals(AuthService.ResetOutcome.INVALID_TICKET, auth.completeReset(ticket, "Bill", "another-pass"),
                "ticket is single use");
    }

    @Test
    public void resetAccountCannotBeClaimedWithoutTheCode() throws Exception {
        dao.addWithStoredHash("Bill", hasher.hash("oldpassword"), "u", null);
        auth.issueResetCode("Bill", "AdminAl");

        // The old hijack: anyone re-"registers" the reset name. Now the name is simply taken.
        assertThrows(LoginInvalidException.class, () -> auth.register("Bill", "attackerpass", IP));
        assertEquals(AuthService.Status.INVALID, auth.verifyCredentials("Bill", "", IP).getStatus());
        assertEquals(AuthService.Status.INVALID, auth.verifyCredentials("Bill", "AAAA-BBBB-CCCC", IP).getStatus());
        assertEquals(AuthService.ResetOutcome.INVALID_TICKET, auth.completeReset("forged-ticket", "Bill", "attackerpass"));
    }

    @Test
    public void ticketForOneAccountCannotResetAnother() throws Exception {
        dao.addWithStoredHash("Alice", hasher.hash("alicepass1"), "u", null);
        dao.addWithStoredHash("Mallory", hasher.hash("mallorypw1"), "u", null);
        String code = auth.issueResetCode("Mallory", "AdminAl");
        String ticket = auth.createResetTicket(auth.verifyCredentials("Mallory", code, IP).getPlayer());

        assertFalse(auth.hasValidResetTicket(ticket, "Alice"));
        assertEquals(AuthService.ResetOutcome.INVALID_TICKET, auth.completeReset(ticket, "Alice", "hijacked-pass"));
        assertEquals(AuthService.Status.OK, auth.verifyCredentials("Alice", "alicepass1", IP).getStatus());
    }

    @Test
    public void resetCodeExpiresAfterTwentyFourHours() throws Exception {
        dao.addWithStoredHash("Bill", hasher.hash("oldpassword"), "u", null);
        String code = auth.issueResetCode("Bill", "AdminAl");
        now.addAndGet(24L * 60 * 60 * 1000 + 1);
        assertEquals(AuthService.Status.INVALID, auth.verifyCredentials("Bill", code, IP).getStatus());
    }

    @Test
    public void resetTicketExpiresAfterFifteenMinutes() throws Exception {
        dao.addWithStoredHash("Bill", hasher.hash("oldpassword"), "u", null);
        String code = auth.issueResetCode("Bill", "AdminAl");
        String ticket = auth.createResetTicket(auth.verifyCredentials("Bill", code, IP).getPlayer());
        now.addAndGet(AuthService.RESET_TICKET_TTL_MILLIS + 1);
        assertEquals(AuthService.ResetOutcome.INVALID_TICKET, auth.completeReset(ticket, "Bill", "brand-new-pass"));
    }

    @Test
    public void tenFailuresLockTheAccountForFiveMinutesEvenWithTheRightPassword() throws Exception {
        dao.addWithStoredHash("Bill", hasher.hash("rightpass"), "u", null);
        for (int i = 0; i < 10; i++)
            assertEquals(AuthService.Status.INVALID, auth.verifyCredentials("Bill", "guess" + i, "198.51.100." + i).getStatus());

        assertEquals(AuthService.Status.THROTTLED, auth.verifyCredentials("bill", "rightpass", IP).getStatus());
        now.addAndGet(5L * 60 * 1000 + 1);
        assertEquals(AuthService.Status.OK, auth.verifyCredentials("Bill", "rightpass", IP).getStatus());
    }

    @Test
    public void registrationStopsAtThreeAccountsPerIpPerDay() throws Exception {
        assertTrue(auth.register("One", "password1", IP));
        assertTrue(auth.register("Two", "password1", IP));
        assertTrue(auth.register("Three", "password1", IP));
        assertThrows(com.gempukku.swccgo.db.RegisterNotAllowedException.class, () -> auth.register("Four", "password1", IP));
        assertTrue(auth.register("Four", "password1", "198.51.100.1"), "another IP is unaffected");
    }
}
