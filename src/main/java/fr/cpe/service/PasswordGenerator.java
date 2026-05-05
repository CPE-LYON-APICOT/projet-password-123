package fr.cpe.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {
    private final int length;
    private final boolean useUpper;
    private final boolean useDigits;
    private final boolean useSpecial;
   
    private final SecureRandom random = new SecureRandom();

    public PasswordGenerator() {
        this.length = 12;
        this.useUpper = true;
        this.useDigits = true;
        this.useSpecial = true;
    }

    private PasswordGenerator(Builder builder) {
        this.length = builder.length;
        this.useUpper = builder.useUpper;
        this.useDigits = builder.useDigits;
        this.useSpecial = builder.useSpecial;
    }

    public String generate() {
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String special = "!@#$%^&*?";

        StringBuilder password = new StringBuilder();
        String available = lower;

        password.append(lower.charAt(random.nextInt(lower.length())));

        if (useUpper) {
            password.append(upper.charAt(random.nextInt(upper.length())));
            available += upper;
        }
        if (useDigits) {
            password.append(digits.charAt(random.nextInt(digits.length())));
            available += digits;
        }
        if (useSpecial) {
            password.append(special.charAt(random.nextInt(special.length())));
            available += special;
        }

        while (password.length() < length) {
            password.append(available.charAt(random.nextInt(available.length())));
        }

        return shuffleString(password.toString());
    }

    private String shuffleString(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) characters.add(c);
        Collections.shuffle(characters);
        StringBuilder result = new StringBuilder();
        for (char c : characters) result.append(c);
        return result.toString();
        
    }


    public static class Builder {
        private int length = 12;
        private boolean useUpper = true;
        private boolean useDigits = true;
        private boolean useSpecial = true;

        public Builder setLength(int length) {
            if (length < 4) this.length = 4;
            else if (length > 64) this.length = 64;
            else this.length = length;
            return this;
        }

        public Builder setUseUpper(boolean useUpper) {
            this.useUpper = useUpper;
            return this;
        }

        public Builder setUseDigits(boolean useDigits) {
            this.useDigits = useDigits;
            return this;
        }

        public Builder setUseSpecial(boolean useSpecial) {
            this.useSpecial = useSpecial;
            return this;
        }
        
        public PasswordGenerator build() {
            return new PasswordGenerator(this);
        }
    }

}

