package fcu.app.i_ching.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class AccountStoreTest {
    private SharedPreferences prefs;
    private AccountStore store;

    @Before
    public void setUp() {
        Context context = RuntimeEnvironment.getApplication();
        prefs = context.getSharedPreferences(AccountStore.PREFS, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
        AccountStore.resetForTests();
        store = AccountStore.get(context);
        store.useGuest();
    }

    @Test
    public void registerCreatesActiveAccountWithoutPlaintextPassword() {
        AccountStore.AuthResult result = store.register("User@Example.COM", "password123", "password123");

        assertTrue(result.success);
        assertTrue(result.transferGuestData);
        assertEquals("user@example.com", result.account.email);
        assertEquals(result.account.id, store.activeAccountId());
        assertFalse(prefs.getString("accountsJson", "").contains("password123"));
    }

    @Test
    public void registerValidatesEmailPasswordConfirmationAndDuplicates() {
        assertFalse(store.register("bad-email", "password123", "password123").success);
        assertFalse(store.register("user@example.com", "short", "short").success);
        assertFalse(store.register("user@example.com", "password123", "different123").success);

        assertTrue(store.register("user@example.com", "password123", "password123").success);
        AccountStore.AuthResult duplicate = store.register("USER@example.com", "password123", "password123");

        assertFalse(duplicate.success);
        assertEquals("這個電子郵件已建立本機帳號", duplicate.message);
    }

    @Test
    public void loginUsesGenericFailureAndActivatesMatchingAccount() {
        AccountStore.AuthResult registered = store.register("user@example.com", "password123", "password123");
        store.useGuest();

        AccountStore.AuthResult wrongPassword = store.login("user@example.com", "wrong-password");
        AccountStore.AuthResult missingAccount = store.login("missing@example.com", "password123");
        AccountStore.AuthResult login = store.login("USER@example.com", "password123");

        assertFalse(wrongPassword.success);
        assertFalse(missingAccount.success);
        assertEquals(wrongPassword.message, missingAccount.message);
        assertTrue(login.success);
        assertEquals(registered.account.id, store.activeAccountId());
    }

    @Test
    public void changePasswordReplacesVerifierAndInvalidatesOldPassword() {
        AccountStore.AuthResult registered = store.register("user@example.com", "password123", "password123");
        String oldVerifier = registered.account.verifier;

        AccountStore.AuthResult changed = store.changePassword("password123", "newpassword123", "newpassword123");
        store.useGuest();

        assertTrue(changed.success);
        assertNotEquals(oldVerifier, changed.account.verifier);
        assertFalse(store.login("user@example.com", "password123").success);
        assertTrue(store.login("user@example.com", "newpassword123").success);
    }

    @Test
    public void deleteCurrentAccountFallsBackToGuest() {
        AccountStore.AuthResult registered = store.register("user@example.com", "password123", "password123");

        String deletedId = store.deleteCurrentAccount();

        assertEquals(registered.account.id, deletedId);
        assertTrue(store.isGuest());
        assertTrue(store.accounts().isEmpty());
    }
}
