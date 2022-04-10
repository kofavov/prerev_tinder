package ru.liga.server.translator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Component
public class Translator {

    public String translate(String s) {
        s = getStringWithYat(s);
        s = getStringWithHardSign(s);
        s = getStringWithI(s);
        s = getStringWithFeta(s);
        return s.trim();
    }

    private String getStringWithYat(String s) {
        Map<String, String> roots = getRootWithYat();
        for (Map.Entry<String, String> root : roots.entrySet()) {
            s = s.replaceAll(root.getKey(), root.getValue());
        }
        return s;
    }

    private Map<String, String> getRootWithYat() {
        Path rootWithYat = Path.of("server/src/main/resources/root_with_yat.csv");
        List<String> root = getStringsFromFile(rootWithYat);
        Map<String, String> map = new HashMap<>();
        for (String r : root) {
            String[] values = r.split("\t");
            if (values.length == 2)
                map.put(values[0], values[1]);
            map.put(values[0].toUpperCase(Locale.ROOT), values[1].toUpperCase(Locale.ROOT));
            map.put(values[0].substring(0, 1).toUpperCase(Locale.ROOT) + values[0].substring(1),
                    values[1].substring(0, 1).toUpperCase(Locale.ROOT) + values[1].substring(1));
        }
        return map;
    }

    private Map<String, String> getWordWithYat() {
        Path wordsWithYat = Path.of("server/src/main/resources/words_with_yat.csv");
        List<String> words = getStringsFromFile(wordsWithYat);

        Map<String, String> map = new HashMap<>();
        for (String w : words) {
            String[] values = w.split("\t");
            if (values.length == 2)
                map.put(values[0], values[1]);
        }
        return map;
    }

    private String getStringWithFeta(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] words = s.split(" +");
        List<String> namesWithF = getNamesWithF();
        for (String word : words) {
            if (word.equals("")) continue;
            String sign = getSign(word);
            if (!sign.equals("")) word = word.substring(0, word.length() - 1);
            if (namesWithF.contains(word)) {
                word = word.replace("Ф", Character.toString('\u0472'));
                word = word.replace("ф", Character.toString('\u0473'));
            }
            stringBuilder.append(word).append(sign).append(" ");
        }
        return stringBuilder.toString();
    }

    private List<String> getNamesWithF() {
        Path namesWithFeta = Path.of("server/src/main/resources/names_with_ф.csv");
        return getStringsFromFile(namesWithFeta);
    }

    private String getStringWithI(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] words = s.split(" +");
        for (String word : words) {
            if (word.equals("")) continue;
            String sign = getSign(word);
            if (!sign.equals("")) word = word.substring(0, word.length() - 1);
            getWordWithI(stringBuilder, word);
            stringBuilder.append(sign).append(" ");
        }
        return stringBuilder.toString();
    }

    private String getSign(String word) {
        String sign = "";
        if (word.matches(".+[,.:/()!@$%?<>-]")) {
            sign = word.substring(word.length() - 1);
        }
        return sign;
    }

    private void getWordWithI(StringBuilder stringBuilder, String word) {
        if (word.matches(".*[Ии][аеёийоуыэюяѣ].*")) {
            char[] chars = word.toCharArray();
            for (int i = 0; i < chars.length - 1; i++) {
                if (Character.toString(chars[i]).matches("[Ии]") &&
                        Character.toString(chars[i + 1]).matches("[аеёийоуыэюяѣ]")) {
                    chars[i] = 'і';
                }
                stringBuilder.append(chars[i]);
                if (i == chars.length - 2) {
                    stringBuilder.append(chars[i + 1]);
                }
            }
        } else {
            stringBuilder.append(word);
        }
    }

    private String getStringWithHardSign(String s) {
        String[] words = s.split(" +");
        StringBuilder stringBuilder = new StringBuilder();
        for (String word : words) {
            if (word.equals("")) continue;
            String sign = getSign(word);
            if (!sign.equals("")) word = word.substring(0, word.length() - 1);

            if (word.endsWith("ь"))
                word = word.substring(0, word.length() - 1) + "ъ";
            else if (word.matches(".+[б-джзк-нп-тф-щ]"))
                word = word + "ъ";
            stringBuilder.append(word).append(sign).append(" ");
        }
        return stringBuilder.toString();
    }

    private List<String> getStringsFromFile(Path path) {
        List<String> root = new ArrayList<>();
        try {
            root = Files.readAllLines(path);
        } catch (IOException e) {
            log.error("Нет  файла по пути {},\n{}", path,e);
        }
        return root;
    }
}
