package fr.cpe;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import fr.cpe.service.AES256Strategy;
import fr.cpe.service.EncryptionStrategy;
import fr.cpe.service.PasswordGenerator;
import fr.cpe.service.SessionManager;

/**
 * Module de configuration Guice.
 *
 * <p>C'est ici qu'on définit comment Guice doit instancier vos classes.</p>
 */
public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        // Liaison de l'interface EncryptionStrategy vers son implémentation AES
        bind(EncryptionStrategy.class).to(AES256Strategy.class);
        
        // SessionManager en Singleton
        bind(SessionManager.class).asEagerSingleton();
    }

    /**
     * Fournit une instance de PasswordGenerator configurée via son Builder.
     */
    @Provides
    public PasswordGenerator providePasswordGenerator() {
        return new PasswordGenerator.Builder()
                .setLength(16)
                .setUseUpper(true)
                .setUseDigits(true)
                .setUseSpecial(true)
                .build();
    }
}
