package org.example.deets.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class RandomCodeGenerator {
    private final int NUM_CHARS_SHORT_LINK = 8;
    private final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private Random random = new Random();

    public String generateRandomCode(){
        char[] result = new char[NUM_CHARS_SHORT_LINK];
        for (int i = 0; i < NUM_CHARS_SHORT_LINK; i++) {
            int randomIndex = random.nextInt(ALPHABET.length());
            result[i] = ALPHABET.charAt(randomIndex);
        }
        return new String(result);
    }

}
