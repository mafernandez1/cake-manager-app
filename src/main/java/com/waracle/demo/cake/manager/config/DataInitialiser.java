package com.waracle.demo.cake.manager.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.waracle.demo.cake.manager.models.manager.CmCake;
import com.waracle.demo.cake.manager.models.security.CmRole;
import com.waracle.demo.cake.manager.models.security.CmRoleType;
import com.waracle.demo.cake.manager.models.security.CmUser;
import com.waracle.demo.cake.manager.repository.manager.CmCakeRepository;
import com.waracle.demo.cake.manager.repository.security.CmRoleRepository;
import com.waracle.demo.cake.manager.repository.security.CmUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;

@Component
public class DataInitialiser implements CommandLineRunner {

    public static final String DEFAULT_ADMIN_EMAIL = "admin.demo@waracle.com";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private final CmRoleRepository roleRepository;
    private final CmUserRepository userRepository;
    private final CmCakeRepository cakeRepository;
    private final PasswordEncoder encoder;

    public DataInitialiser(CmRoleRepository roleRepository,
                           CmUserRepository userRepository,
                           CmCakeRepository cakeRepository,
                           PasswordEncoder encoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.cakeRepository = cakeRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        if (roleRepository.count() == 0) {
            roleRepository.save(new CmRole(CmRoleType.USER));
            roleRepository.save(new CmRole(CmRoleType.ADMIN));
            System.out.println("Roles initialized successfully!");
        }
        // Initialize a default admin user if it doesn't exist
        if (!userRepository.existsByEmail(DEFAULT_ADMIN_EMAIL)) {
            CmRole adminRole = roleRepository.findByName(CmRoleType.ADMIN).orElseThrow(() -> new RuntimeException("Admin role not found!"));
            CmUser adminUser = new CmUser();
            adminUser.setEmail(DEFAULT_ADMIN_EMAIL);
            adminUser.setPassword(encoder.encode(DEFAULT_ADMIN_PASSWORD));
            adminUser.setRoles(Set.of(adminRole));
            userRepository.save(adminUser);
            System.out.println("Default admin user created successfully!");
        }
        // Initialize cakes from JSON if the repository is empty
        if (cakeRepository.count() == 0) {
            initialiseCakes();
        }
    }

    private void initialiseCakes() {
        System.out.println("downloading cake json");
        try (InputStream inputStream = new URL("https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json").openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer buffer = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line);
                line = reader.readLine();
            }

            System.out.println("parsing cake json");
            JsonParser parser = new JsonFactory().createParser(buffer.toString());
            if (JsonToken.START_ARRAY != parser.nextToken()) {
                throw new Exception("bad token");
            }

            JsonToken nextToken = parser.nextToken();
            while(nextToken == JsonToken.START_OBJECT) {
                System.out.println("creating cake entity");

                CmCake cakeEntity = new CmCake();
                System.out.println(parser.nextFieldName());
                cakeEntity.setTitle(parser.nextTextValue());

                System.out.println(parser.nextFieldName());
                cakeEntity.setDescription(parser.nextTextValue());

                System.out.println(parser.nextFieldName());
                cakeEntity.setImage(parser.nextTextValue());

                cakeRepository.save(cakeEntity);

                nextToken = parser.nextToken();
                System.out.println(nextToken);

                nextToken = parser.nextToken();
                System.out.println(nextToken);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        System.out.println("init finished");
    }
}
