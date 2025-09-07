package com.example.hr_assistant.service.ml;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Простая токенизация для BERT-подобных моделей.
 * Для продакшна рекомендуется заменить на полноценный WordPiece/BPE токенайзер.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TextTokenizer {

    private static final int DEFAULT_MAX_TOKENS = 256;

    public NDList tokenizeSingle(String text) {
        return tokenizePair(text, null, DEFAULT_MAX_TOKENS);
    }

    public NDList tokenizePair(String textA, String textB) {
        return tokenizePair(textA, textB, DEFAULT_MAX_TOKENS);
    }

    public NDList tokenizePair(String textA, String textB, int maxTokens) {
        try (NDManager manager = NDManager.newBaseManager()) {
            int[] inputIds = toPseudoIds(textA, textB, maxTokens);
            int[] attentionMask = new int[inputIds.length];
            for (int i = 0; i < inputIds.length; i++) attentionMask[i] = inputIds[i] == 0 ? 0 : 1;

            NDArray ids = manager.create(inputIds).reshape(1, inputIds.length);
            NDArray mask = manager.create(attentionMask).reshape(1, attentionMask.length);
            return new NDList(ids, mask);
        }
    }

    private int[] toPseudoIds(String a, String b, int maxTokens) {
        List<Integer> ids = new ArrayList<>();
        ids.add(101); // [CLS]
        append(ids, a);
        ids.add(102); // [SEP]
        if (b != null && !b.isEmpty()) {
            append(ids, b);
            ids.add(102);
        }
        if (ids.size() > maxTokens) ids = ids.subList(0, maxTokens);
        while (ids.size() < maxTokens) ids.add(0); // pad
        return ids.stream().mapToInt(Integer::intValue).toArray();
    }

    private void append(List<Integer> ids, String text) {
        if (text == null) return;
        byte[] bytes = text.toLowerCase().getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            int v = (b & 0xFF) + 1000; // deterministic but naive mapping
            ids.add(v);
        }
    }
}


