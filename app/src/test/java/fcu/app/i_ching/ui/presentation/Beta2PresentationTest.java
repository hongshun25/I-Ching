package fcu.app.i_ching.ui.presentation;

import org.junit.Test;

import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.HexagramRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Beta2PresentationTest {
    @Test
    public void questionPresetsExposeStableLabelsAndQuestions() {
        assertEquals(8, QuestionPresetPresentation.all().size());
        assertEquals("職涯", QuestionPresetPresentation.all().get(0).label);
        assertEquals("職涯發展的方向", QuestionPresetPresentation.all().get(0).question);
        assertEquals("決策", QuestionPresetPresentation.all().get(7).label);
    }

    @Test
    public void methodPresentationMarksSelectedStateWithoutColorOnly() {
        MethodOptionPresentation selected =
                MethodOptionPresentation.from(DivinationMethod.COINS, DivinationMethod.COINS);
        MethodOptionPresentation unselected =
                MethodOptionPresentation.from(DivinationMethod.SIMPLE, DivinationMethod.COINS);

        assertTrue(selected.selected);
        assertEquals("三枚銅錢，已選擇", selected.contentDescription);
        assertFalse(unselected.selected);
        assertEquals("簡易占法，未選擇", unselected.contentDescription);
    }

    @Test
    public void dailyPresentationUsesNightSpecificCopyAndSizing() {
        DailyCardPresentation light = DailyCardPresentation.from(HexagramRepository.get(15), false);
        DailyCardPresentation night = DailyCardPresentation.from(HexagramRepository.get(29), true);

        assertEquals("早安，今天想安靜一下嗎？", light.greetingText);
        assertEquals("「謙卑自守，則吉無不利。」", light.judgmentText);
        assertFalse(light.centeredGreeting);
        assertEquals(72, light.hexagramWidthDp);
        assertEquals("甲辰年 壬申月 丁卯日", night.greetingText);
        assertTrue(night.centeredGreeting);
        assertEquals(128, night.hexagramWidthDp);
    }

    @Test
    public void hexagramListItemIncludesFavoriteState() {
        HexagramListItemPresentation item =
                HexagramListItemPresentation.from(HexagramRepository.get(15), true);

        assertEquals("第15卦", item.numberText);
        assertEquals("謙", item.nameText);
        assertTrue(item.trigramsText.contains("上坤"));
        assertEquals("開啟第15卦地山謙詳情", item.openContentDescription);
        assertEquals("♥", item.favoriteSymbol);
        assertEquals("取消收藏第15卦", item.favoriteContentDescription);
    }

    @Test
    public void detailPresentationBuildsExpectedSections() {
        HexagramDetailPresentation detail =
                HexagramDetailPresentation.from(HexagramRepository.get(15), false);

        assertEquals("第15卦｜謙", detail.titleText);
        assertEquals("地山謙", detail.primaryChipText);
        assertEquals("♡", detail.favoriteSymbol);
        assertEquals(6, detail.sections.size());
        assertEquals("六爻爻辭", detail.sections.get(4).title);
        assertTrue(detail.sections.get(4).body.contains("初六"));
    }

    @Test
    public void ritualReduceMotionUsesShortNonCancelableTapFeedback() {
        RitualPresentation normal = RitualPresentation.from(false);
        RitualPresentation reduced = RitualPresentation.from(true);

        assertEquals(3000L, normal.finishDelayMs);
        assertTrue(normal.cancelOnRelease);
        assertEquals(450L, reduced.finishDelayMs);
        assertFalse(reduced.cancelOnRelease);
        assertTrue(reduced.pressedFocusScale < normal.pressedFocusScale);
    }
}
