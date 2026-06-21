package fcu.app.i_ching.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AccountStore {
    public static final String PREFS = "i_ching_accounts";
    public static final String GUEST_ACCOUNT_ID = "guest";
    public static final int MIN_PASSWORD_LENGTH = 8;

    private static final String KEY_ACCOUNTS = "accountsJson";
    private static final String KEY_ACTIVE_ACCOUNT_ID = "activeAccountId";
    private static final String KEY_GUEST_TRANSFERRED = "guestTransferredToFirstAccount";
    private static final int PASSWORD_ITERATIONS = 120_000;
    private static final int PASSWORD_KEY_LENGTH = 256;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    private static volatile AccountStore instance;

    private final SharedPreferences prefs;
    private final SecureRandom random;

    public static AccountStore get(Context context) {
        if (instance == null) {
            synchronized (AccountStore.class) {
                if (instance == null) {
                    instance = new AccountStore(
                            context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE),
                            new SecureRandom()
                    );
                }
            }
        }
        return instance;
    }

    AccountStore(SharedPreferences prefs, SecureRandom random) {
        this.prefs = prefs;
        this.random = random;
    }

    static void resetForTests() {
        instance = null;
    }

    public Account currentAccount() {
        String accountId = activeAccountId();
        if (GUEST_ACCOUNT_ID.equals(accountId)) return Account.guest();
        Account account = findById(accountId);
        if (account == null) {
            useGuest();
            return Account.guest();
        }
        return account;
    }

    public String activeAccountId() {
        return prefs.getString(KEY_ACTIVE_ACCOUNT_ID, GUEST_ACCOUNT_ID);
    }

    public boolean isGuest() {
        return GUEST_ACCOUNT_ID.equals(activeAccountId());
    }

    public void useGuest() {
        prefs.edit().putString(KEY_ACTIVE_ACCOUNT_ID, GUEST_ACCOUNT_ID).commit();
    }

    public AuthResult register(String email, String password, String confirmPassword) {
        String normalizedEmail = normalizeEmail(email);
        String validation = validateEmail(normalizedEmail);
        if (validation != null) return AuthResult.error(validation);
        validation = validateNewPassword(password, confirmPassword);
        if (validation != null) return AuthResult.error(validation);
        if (findByEmail(normalizedEmail) != null) {
            return AuthResult.error("這個電子郵件已建立本機帳號");
        }

        try {
            Credential credential = createCredential(password);
            Account account = new Account(newAccountId(), normalizedEmail, System.currentTimeMillis(),
                    credential.salt, credential.verifier, credential.iterations, credential.algorithm);
            List<Account> accounts = accounts();
            accounts.add(account);
            boolean transferGuestData = !prefs.getBoolean(KEY_GUEST_TRANSFERRED, false);
            SharedPreferences.Editor editor = prefs.edit()
                    .putString(KEY_ACCOUNTS, accountsToJson(accounts).toString())
                    .putString(KEY_ACTIVE_ACCOUNT_ID, account.id);
            if (transferGuestData) editor.putBoolean(KEY_GUEST_TRANSFERRED, true);
            editor.commit();
            return AuthResult.success(account, transferGuestData);
        } catch (GeneralSecurityException | JSONException e) {
            return AuthResult.error("無法建立本機帳號，請稍後再試");
        }
    }

    public AuthResult login(String email, String password) {
        Account account = findByEmail(normalizeEmail(email));
        if (account == null || password == null || !verifyPassword(account, password)) {
            return AuthResult.error("電子郵件或密碼不正確");
        }
        prefs.edit().putString(KEY_ACTIVE_ACCOUNT_ID, account.id).commit();
        return AuthResult.success(account, false);
    }

    public AuthResult changePassword(String currentPassword, String newPassword, String confirmPassword) {
        Account account = currentAccount();
        if (account.isGuest()) return AuthResult.error("本機模式沒有密碼");
        if (!verifyPassword(account, currentPassword)) return AuthResult.error("目前密碼不正確");
        String validation = validateNewPassword(newPassword, confirmPassword);
        if (validation != null) return AuthResult.error(validation);
        try {
            Credential credential = createCredential(newPassword);
            Account updated = new Account(account.id, account.email, account.createdAt,
                    credential.salt, credential.verifier, credential.iterations, credential.algorithm);
            List<Account> accounts = accounts();
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).id.equals(account.id)) {
                    accounts.set(i, updated);
                    prefs.edit().putString(KEY_ACCOUNTS, accountsToJson(accounts).toString()).commit();
                    return AuthResult.success(updated, false);
                }
            }
            return AuthResult.error("找不到目前帳號");
        } catch (GeneralSecurityException | JSONException e) {
            return AuthResult.error("無法更新密碼，請稍後再試");
        }
    }

    public String deleteCurrentAccount() {
        String activeAccountId = activeAccountId();
        if (GUEST_ACCOUNT_ID.equals(activeAccountId)) return GUEST_ACCOUNT_ID;
        List<Account> accounts = accounts();
        List<Account> kept = new ArrayList<>();
        for (Account account : accounts) {
            if (!account.id.equals(activeAccountId)) kept.add(account);
        }
        try {
            prefs.edit()
                    .putString(KEY_ACCOUNTS, accountsToJson(kept).toString())
                    .putString(KEY_ACTIVE_ACCOUNT_ID, GUEST_ACCOUNT_ID)
                    .commit();
        } catch (JSONException e) {
            useGuest();
        }
        return activeAccountId;
    }

    public List<Account> accounts() {
        List<Account> accounts = new ArrayList<>();
        String value = prefs.getString(KEY_ACCOUNTS, "[]");
        try {
            JSONArray array = new JSONArray(value == null || value.isEmpty() ? "[]" : value);
            for (int i = 0; i < array.length(); i++) {
                Account account = Account.fromJson(array.getJSONObject(i));
                if (account != null && !account.isGuest()) accounts.add(account);
            }
        } catch (JSONException ignored) {
        }
        return accounts;
    }

    private Account findById(String accountId) {
        if (accountId == null || GUEST_ACCOUNT_ID.equals(accountId)) return Account.guest();
        for (Account account : accounts()) {
            if (account.id.equals(accountId)) return account;
        }
        return null;
    }

    private Account findByEmail(String email) {
        if (email == null || email.isEmpty()) return null;
        for (Account account : accounts()) {
            if (email.equals(account.email)) return account;
        }
        return null;
    }

    private String newAccountId() {
        byte[] bytes = new byte[9];
        random.nextBytes(bytes);
        return "acct_" + System.currentTimeMillis() + "_"
                + Base64.encodeToString(bytes, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    }

    private Credential createCredential(String password) throws GeneralSecurityException {
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        String salt = Base64.encodeToString(saltBytes, Base64.NO_WRAP);
        String algorithm = "PBKDF2WithHmacSHA256";
        try {
            return new Credential(salt, hashPassword(password, saltBytes, algorithm, PASSWORD_ITERATIONS),
                    PASSWORD_ITERATIONS, algorithm);
        } catch (GeneralSecurityException e) {
            algorithm = "PBKDF2WithHmacSHA1";
            return new Credential(salt, hashPassword(password, saltBytes, algorithm, PASSWORD_ITERATIONS),
                    PASSWORD_ITERATIONS, algorithm);
        }
    }

    private boolean verifyPassword(Account account, String password) {
        if (account == null || account.isGuest() || password == null) return false;
        try {
            byte[] salt = Base64.decode(account.salt, Base64.DEFAULT);
            String verifier = hashPassword(password, salt, account.algorithm, account.iterations);
            byte[] expected = Base64.decode(account.verifier, Base64.DEFAULT);
            byte[] actual = Base64.decode(verifier, Base64.DEFAULT);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException | GeneralSecurityException e) {
            return false;
        }
    }

    private String hashPassword(String password, byte[] salt, String algorithm, int iterations)
            throws GeneralSecurityException {
        KeySpec spec = new PBEKeySpec(
                password == null ? new char[0] : password.toCharArray(),
                salt,
                iterations,
                PASSWORD_KEY_LENGTH
        );
        byte[] hash = SecretKeyFactory.getInstance(algorithm).generateSecret(spec).getEncoded();
        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }

    private String validateEmail(String email) {
        if (email == null || email.isEmpty()) return "請輸入電子郵件";
        if (!EMAIL_PATTERN.matcher(email).matches()) return "請輸入有效的電子郵件";
        return null;
    }

    private String validateNewPassword(String password, String confirmPassword) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) return "密碼至少需要 8 個字";
        if (confirmPassword != null && !password.equals(confirmPassword)) return "兩次輸入的密碼不一致";
        return null;
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private JSONArray accountsToJson(List<Account> accounts) throws JSONException {
        JSONArray array = new JSONArray();
        for (Account account : accounts) array.put(account.toJson());
        return array;
    }

    private static final class Credential {
        final String salt;
        final String verifier;
        final int iterations;
        final String algorithm;

        Credential(String salt, String verifier, int iterations, String algorithm) {
            this.salt = salt;
            this.verifier = verifier;
            this.iterations = iterations;
            this.algorithm = algorithm;
        }
    }

    public static final class Account {
        public final String id;
        public final String email;
        public final long createdAt;
        final String salt;
        final String verifier;
        final int iterations;
        final String algorithm;

        Account(String id, String email, long createdAt, String salt, String verifier,
                int iterations, String algorithm) {
            this.id = id;
            this.email = email;
            this.createdAt = createdAt;
            this.salt = salt;
            this.verifier = verifier;
            this.iterations = iterations;
            this.algorithm = algorithm;
        }

        static Account guest() {
            return new Account(GUEST_ACCOUNT_ID, "", 0L, "", "", 0, "");
        }

        public boolean isGuest() {
            return GUEST_ACCOUNT_ID.equals(id);
        }

        JSONObject toJson() throws JSONException {
            JSONObject object = new JSONObject();
            object.put("id", id);
            object.put("email", email);
            object.put("createdAt", createdAt);
            object.put("salt", salt);
            object.put("verifier", verifier);
            object.put("iterations", iterations);
            object.put("algorithm", algorithm);
            return object;
        }

        static Account fromJson(JSONObject object) {
            if (object == null) return null;
            String id = object.optString("id");
            String email = object.optString("email");
            String salt = object.optString("salt");
            String verifier = object.optString("verifier");
            String algorithm = object.optString("algorithm", "PBKDF2WithHmacSHA256");
            int iterations = object.optInt("iterations", PASSWORD_ITERATIONS);
            if (id.isEmpty() || email.isEmpty() || salt.isEmpty() || verifier.isEmpty()) return null;
            return new Account(id, email, object.optLong("createdAt"), salt, verifier, iterations, algorithm);
        }
    }

    public static final class AuthResult {
        public final boolean success;
        public final Account account;
        public final String message;
        public final boolean transferGuestData;

        private AuthResult(boolean success, Account account, String message, boolean transferGuestData) {
            this.success = success;
            this.account = account;
            this.message = message;
            this.transferGuestData = transferGuestData;
        }

        static AuthResult success(Account account, boolean transferGuestData) {
            return new AuthResult(true, account, "", transferGuestData);
        }

        static AuthResult error(String message) {
            return new AuthResult(false, null, message, false);
        }
    }
}
