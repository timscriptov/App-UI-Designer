package com.mcal.uidesigner.common;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TextToSpeechHelper {
    private final Context context;
    private final Map<String, LanguageTTS> langTTS = new HashMap<>();
    private final Map<String, Map<String, String>> langOperatorWords = new HashMap<>();

    public TextToSpeechHelper(Context context) {
        this.context = context;
        this.langOperatorWords.put("en", new HashMap<>());
        this.langOperatorWords.get("en").put("println", "printline");
        this.langOperatorWords.get("en").put(";", "semicolon");
        this.langOperatorWords.get("en").put("<", "less than");
        this.langOperatorWords.get("en").put(">", "greater than");
        this.langOperatorWords.get("en").put("< =", "less than or equal");
        this.langOperatorWords.get("en").put("> =", "greater than or equal");
        this.langOperatorWords.get("en").put("==", "equal equal");
        this.langOperatorWords.get("en").put("!=", "not equal");
        this.langOperatorWords.get("en").put("--", "minus minus");
        this.langOperatorWords.get("en").put("++", "plus plus");
        this.langOperatorWords.get("en").put("+", "plus");
        this.langOperatorWords.get("en").put("-", "minus");
        this.langOperatorWords.get("en").put("*", "star");
        this.langOperatorWords.get("en").put("/", "slash");
        this.langOperatorWords.put("de", new HashMap<>());
        this.langOperatorWords.get("de").put("println", "printlein");
        this.langOperatorWords.get("de").put(";", "Strichpunkt");
        this.langOperatorWords.get("de").put("<", "kleiner als");
        this.langOperatorWords.get("de").put(">", "grösser als");
        this.langOperatorWords.get("de").put("< =", "kleiner gleich");
        this.langOperatorWords.get("de").put("> =", "grösser gleich");
        this.langOperatorWords.get("de").put("==", "gleich gleich");
        this.langOperatorWords.get("de").put("!=", "ungleich");
        this.langOperatorWords.get("de").put("--", "minus minus");
        this.langOperatorWords.get("de").put("++", "plus plus");
        this.langOperatorWords.get("de").put("+", "plus");
        this.langOperatorWords.get("de").put("-", "minus");
        this.langOperatorWords.get("de").put("*", "Stern");
        this.langOperatorWords.get("de").put("/", "Strich");
    }

    public void shutdown() {
        for (LanguageTTS tts : this.langTTS.values()) {
            tts.shutdown();
        }
    }

    public void speak(String language, String html) {
        if (!this.langTTS.containsKey(language)) {
            this.langTTS.put(language, new LanguageTTS(language));
        }
        this.langTTS.get(language).speak(html);
    }


    public class LanguageTTS {
        private boolean initialized;
        private TextToSpeech tts;
        private final String ttsLanguage;
        private Locale ttsLocale;
        private String ttsText;

        public LanguageTTS(String language) {
            this.ttsLanguage = language;
            Locale[] arr = Locale.getAvailableLocales();
            int len = arr.length;
            int i = 0;
            while (true) {
                if (i >= len) {
                    break;
                }
                Locale l = arr[i];
                if (this.ttsLanguage.equals(l.getLanguage())) {
                    this.ttsLocale = l;
                    break;
                }
                i++;
            }
            if (this.ttsLocale != null) {
                this.tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int p1) {
                        initialized = true;
                        if (tts != null) {
                            int isLanguageAvailable = tts.isLanguageAvailable(ttsLocale);
                            AppLog.d(ttsLocale + " TTS available: " + isLanguageAvailable);
                            if (isLanguageAvailable == -1) {
                                tts.shutdown();
                                tts = null;
                            } else if (isLanguageAvailable == -2) {
                                tts.shutdown();
                                tts = null;
                            } else {
                                tts.getDefaultEngine();
                                tts.setLanguage(ttsLocale);
                                if (ttsText != null) {
                                    tts.speak(ttsText, 0, null);
                                    ttsText = null;
                                }
                            }
                        }
                    }
                });
            }
        }

        public void shutdown() {
            if (this.tts != null) {
                this.tts.shutdown();
            }
        }

        public void speak(String html) {
            String text = cleanHtml(html);
            if (!this.initialized || this.tts == null) {
                this.ttsText = text;
            } else {
                this.tts.speak(text, 0, null);
            }
        }

        private String cleanHtml(String html) {
            String text = html.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "");
            Map<String, String> words = (Map) langOperatorWords.get(this.ttsLanguage);
            if (words != null) {
                for (Map.Entry<String, String> entry : words.entrySet()) {
                    text = text.replace("<b>" + entry.getKey() + "</b>", entry.getValue()).replace(" " + entry.getKey() + " ", entry.getValue());
                }
            }
            return replaceUnderscores(text.replace("<b>a</b>", "A").replace("<b>b</b>", "B").replace("<b>c</b>", "C").replace("<b>i</b>", "I").replace("<b>j</b>", "J").replace("<b>k</b>", "K").replace("<b>android:", "<b>").replace("<i>", "").replace("</i>", "").replace("</b>", "").replace("<b>", ""));
        }

        private String replaceUnderscores(String text) {
            char[] newText = text.toCharArray();
            for (int p = 1; p < newText.length - 1; p++) {
                if (newText[p] == '_' && Character.isLetterOrDigit(newText[p - 1]) && Character.isLetterOrDigit(newText[p + 1])) {
                    newText[p] = ' ';
                }
            }
            return new String(newText);
        }
    }
}
