package fcu.app.i_ching.data;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SettingsStoreTest {
    private Context context;
    private SettingsStore store;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        context.getSharedPreferences("i_ching_settings", Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences(AccountStore.PREFS, Context.MODE_PRIVATE).edit().clear().commit();
        AccountStore.resetForTests();
        AccountStore.get(context).useGuest();
        store = new SettingsStore(context);
    }

    @Test
    public void defaultsMatchLocalBetaPreferences() {
        AppSettings settings = store.get();

        assertFalse(settings.onboardingComplete);
        assertFalse(settings.darkMode);
        assertTrue(settings.reduceMotion);
        assertTrue(settings.autoSave);
        assertTrue(store.favoriteHexagrams().isEmpty());
    }

    @Test
    public void togglesPersistAndFavoritesCanBeAddedAndRemoved() {
        store.setOnboardingComplete(true);
        store.setDarkMode(true);
        store.setReduceMotion(false);
        store.setAutoSave(false);

        assertTrue(store.isOnboardingComplete());
        assertTrue(store.isDarkMode());
        assertFalse(store.isReduceMotion());
        assertFalse(store.isAutoSave());

        assertTrue(store.toggleFavorite(15));
        assertTrue(store.isFavorite(15));
        assertFalse(store.toggleFavorite(15));
        assertFalse(store.isFavorite(15));
    }

    @Test
    public void accountScopedSettingsAreIsolated() {
        AccountStore accounts = AccountStore.get(context);
        store.setDarkMode(true);
        store.setAutoSave(false);
        assertTrue(store.toggleFavorite(15));

        AccountStore.AuthResult first = accounts.register("first@example.com", "password123", "password123");
        store.transferGuestSettingsTo(first.account.id);

        assertTrue(store.isDarkMode());
        assertFalse(store.isAutoSave());
        assertTrue(store.isFavorite(15));

        AccountStore.AuthResult second = accounts.register("second@example.com", "password123", "password123");
        assertTrue(second.success);
        store.setDarkMode(false);
        store.setAutoSave(true);
        assertFalse(store.isFavorite(15));

        accounts.login("first@example.com", "password123");

        assertTrue(store.isDarkMode());
        assertFalse(store.isAutoSave());
        assertTrue(store.isFavorite(15));
    }

    @Test
    public void guestTransferMovesFavoritesAndResetsGuestScope() {
        AccountStore accounts = AccountStore.get(context);
        store.setReduceMotion(false);
        assertTrue(store.toggleFavorite(29));

        AccountStore.AuthResult registered = accounts.register("user@example.com", "password123", "password123");
        store.transferGuestSettingsTo(registered.account.id);

        assertFalse(store.isReduceMotion());
        assertTrue(store.isFavorite(29));

        accounts.useGuest();

        assertTrue(store.isReduceMotion());
        assertFalse(store.isFavorite(29));
    }
}
