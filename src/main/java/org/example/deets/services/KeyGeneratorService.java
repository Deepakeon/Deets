package org.example.deets.services;
import lombok.RequiredArgsConstructor;
import org.example.deets.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class KeyGeneratorService {
    private final int NUM_CHARS_SHORT_LINK = 8;
    private final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private Random random = new Random();
    private final int maxRetryCount = 10;

    private final UrlRepository urlRepository;

    private String generateRandomCode(){
        char[] result = new char[NUM_CHARS_SHORT_LINK];
        for (int i = 0; i < NUM_CHARS_SHORT_LINK; i++) {
            int randomIndex = random.nextInt(ALPHABET.length());
            result[i] = ALPHABET.charAt(randomIndex);
        }
        return new String(result);
    }

    public String generateRandomCodeWithRetry() {
        for(int attempt=0; attempt<maxRetryCount; attempt++){
            String code = generateRandomCode();
            if(urlRepository.findUrlByCode(code).isEmpty()){
                return code;
            }
        }

        throw new RuntimeException("Failed to generate unique code after 5 attempts");
    }

}
