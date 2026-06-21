package fcu.app.i_ching.ui.presentation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class QuestionPresetPresentation {
    public final String label;
    public final String question;

    private QuestionPresetPresentation(String label, String question) {
        this.label = label;
        this.question = question;
    }

    public static List<QuestionPresetPresentation> all() {
        return PRESETS;
    }

    private static final List<QuestionPresetPresentation> PRESETS = Collections.unmodifiableList(Arrays.asList(
            new QuestionPresetPresentation("職涯", "職涯發展的方向"),
            new QuestionPresetPresentation("感情", "這段感情未來的發展"),
            new QuestionPresetPresentation("人際", "如何改善當前的人際關係"),
            new QuestionPresetPresentation("財務", "近期的財務狀況與建議"),
            new QuestionPresetPresentation("學業", "學業上的挑戰與突破"),
            new QuestionPresetPresentation("家庭", "家庭關係的調和"),
            new QuestionPresetPresentation("健康", "健康狀態的保養建議"),
            new QuestionPresetPresentation("自我成長", "近期自我成長的重點"),
            new QuestionPresetPresentation("決策", "面對這個選擇，我該如何決定")
    ));
}
