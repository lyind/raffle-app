package net.talpidae.raffle.app.util.auth;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.talpidae.base.resource.CredentialValidator;
import net.talpidae.base.util.BaseArguments;
import net.talpidae.base.util.auth.Credentials;

import java.util.Objects;
import java.util.UUID;

import lombok.val;


/**
 * This is fake and not intended for production use.
 */
@Singleton
public class StaticAdminValidator implements CredentialValidator
{
    private static final UUID RANDOM_ADMIN_ID = UUID.randomUUID();

    private final String username;

    private final String password;


    @Inject
    public StaticAdminValidator(BaseArguments baseArguments)
    {
        val parser = baseArguments.getOptionParser();
        val userNameOption = parser.accepts("admin.username").withRequiredArg().ofType(String.class).defaultsTo("ADMIN");
        val passwordOption = parser.accepts("admin.password").withRequiredArg().ofType(String.class).defaultsTo("ADMIN");
        val options = baseArguments.parse();

        this.username = options.valueOf(userNameOption);
        this.password = options.valueOf(passwordOption);
    }


    @Override
    public UUID validate(Credentials credentials)
    {
        if (Objects.equals(username, credentials.getName()))
        {
            val passwordInput = credentials.getPassword();
            
            boolean match = true;
            int i;
            for (i = 0; i < passwordInput.length(); ++i)
            {
                if (passwordInput.charAt(i) != password.charAt(i))
                {
                    match = false;
                }
            }

            if (match && passwordInput.length() == password.length())
            {
                return RANDOM_ADMIN_ID;
            }
        }

        return null;
    }
}