package sk.janobono.wiwa.component;

import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RandomString {

    private enum Type {
        NUMERIC(true, false, false),
        ALPHA(false, true, false),
        ALPHA_NUMERIC(true, true, false),
        ALPHA_NUMERIC_SPECIAL(true, true, true);

        final boolean useNumbers;
        final boolean useAlpha;
        final boolean useSpecial;

        Type(final boolean useNumbers, final boolean useAlpha, final boolean useSpecial) {
            this.useNumbers = useNumbers;
            this.useAlpha = useAlpha;
            this.useSpecial = useSpecial;
        }
    }

    private record Params(
            Type type, int minNumbers, int minAlpha, int minAlphaCap, int minSpecial
    ) {
    }

    private static final Character[] NUMBERS = new Character[]{
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
    };

    private static final Character[] ALPHA = new Character[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final Character[] ALPHA_CAP = new Character[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static final Character[] SPECIAL = new Character[]{
            '.', ':', '~', '!', '@', '#', '$', '%', '*', '_'
    };

    private final SecureRandom rnd;

    public RandomString() {
        this("SHA1PRNG");
    }

    public RandomString(final String algorithm) {
        try {
            this.rnd = SecureRandom.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String numeric(final int length) {
        return numeric(length, length);
    }

    public String numeric(final int minLength, final int maxLength) {
        return generate(new Params(Type.NUMERIC, 0, 0, 0, 0), minLength, maxLength);
    }

    public String alphabet(final int length) {
        return alphabet(0, 0, length, length);
    }

    public String alphabet(final int minChars, final int minCapitals, final int minLength, final int maxLength) {
        return generate(new Params(Type.ALPHA, 0, minChars, minCapitals, 0), minLength, maxLength);
    }

    public String alphaNumeric(final int length) {
        return alphaNumeric(0, 0, 0, length, length);
    }

    public String alphaNumeric(final int minNumbers, final int minChars, final int minCapitals, final int minLength, final int maxLength) {
        return generate(new Params(Type.ALPHA_NUMERIC, minNumbers, minChars, minCapitals, 0), minLength, maxLength);
    }

    public String alphaNumericWithSpecial(final int length) {
        return alphaNumericWithSpecial(0, 0, 0, 0, length, length);
    }

    public String alphaNumericWithSpecial(final int minNumbers, final int minChars, final int minCapitals, final int minSpecial, final int minLength, final int maxLength) {
        return generate(new Params(Type.ALPHA_NUMERIC_SPECIAL, minNumbers, minChars, minCapitals, minSpecial), minLength, maxLength);
    }

    private String generate(final Params params, final int minLength, final int maxLength) {
        checkMinimalCharacters(params, minLength, maxLength);
        final int totalLength = getTotalStringLength(minLength, maxLength);

        final List<Character> result = new LinkedList<>();
        if (params.type().useAlpha) {
            result.addAll(randomize(params.minAlpha(), ALPHA));
            result.addAll(randomize(params.minAlphaCap(), ALPHA_CAP));
        }

        if (params.type().useNumbers) {
            result.addAll(randomize(params.minNumbers(), NUMBERS));
        }

        if (params.type().useSpecial) {
            result.addAll(randomize(params.minSpecial(), SPECIAL));
        }

        if (result.size() < totalLength) {
            result.addAll(randomize(totalLength - result.size(), charactersByType(params)));
        }

        Collections.shuffle(result, rnd);
        return result.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    private void checkMinimalCharacters(final Params params, final int minLength, final int maxLength) {
        if (maxLength < minLength) {
            throw new RuntimeException("Min length cannot be more then min length");
        }
        final int totalMinimals = getTotalMinimals(params);
        if ((minLength == maxLength) && (totalMinimals > minLength)) {
            throw new RuntimeException("You define more minimal characters than total length of string");
        } else if (totalMinimals > minLength) {
            if (maxLength < totalMinimals) {
                throw new RuntimeException("You define more minimal characters than max length of string");
            }
        }
    }

    private int getTotalMinimals(final Params params) {
        int totalMinimals = 0;
        if (params.type().useAlpha) {
            totalMinimals += params.minAlpha() + params.minAlphaCap();
        }
        if (params.type().useNumbers) {
            totalMinimals += params.minNumbers();
        }
        if (params.type().useSpecial) {
            totalMinimals += params.minSpecial();
        }
        return totalMinimals;
    }

    private int getTotalStringLength(final int minLength, final int maxLength) {
        final int result;

        if (minLength == maxLength) {
            result = minLength;
        } else {
            result = rnd.nextInt(maxLength - minLength) + minLength;
        }

        return result;
    }

    private List<Character> randomize(final int size, final Character[] characters) {
        final List<Character> result = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            result.add(characters[rnd.nextInt(characters.length)]);
        }
        return result;
    }

    private Character[] charactersByType(final Params params) {
        final List<Character> result = new LinkedList<>();
        if (params.type().useAlpha) {
            result.addAll(Arrays.asList(ALPHA));
            result.addAll(Arrays.asList(ALPHA_CAP));
        }
        if (params.type().useNumbers) {
            result.addAll(Arrays.asList(NUMBERS));
        }
        if (params.type().useSpecial) {
            result.addAll(Arrays.asList(SPECIAL));
        }
        return result.toArray(Character[]::new);
    }
}
