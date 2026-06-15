package fcu.app.i_ching.data;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HexagramRepositoryFilterTest {
    @Test
    public void searchMatchesNameFullNameTagAndSummary() {
        assertEquals(15, HexagramRepository.filter("謙", HexagramRepository.FILTER_ALL, Collections.emptySet()).get(0).number);
        assertEquals(15, HexagramRepository.filter("地山謙", HexagramRepository.FILTER_ALL, Collections.emptySet()).get(0).number);
        assertEquals(15, HexagramRepository.filter("內斂的力量", HexagramRepository.FILTER_ALL, Collections.emptySet()).get(0).number);
        assertEquals(29, HexagramRepository.filter("重重險陷", HexagramRepository.FILTER_ALL, Collections.emptySet()).get(0).number);
    }

    @Test
    public void upperAndLowerCanonFiltersSplitTheList() {
        List<Hexagram> upper = HexagramRepository.filter("", HexagramRepository.FILTER_UPPER_CANON, Collections.emptySet());
        List<Hexagram> lower = HexagramRepository.filter("", HexagramRepository.FILTER_LOWER_CANON, Collections.emptySet());

        assertEquals(30, upper.size());
        assertEquals(1, upper.get(0).number);
        assertEquals(30, upper.get(29).number);
        assertEquals(34, lower.size());
        assertEquals(31, lower.get(0).number);
        assertEquals(64, lower.get(33).number);
    }

    @Test
    public void favoritesFilterUsesStoredFavoriteNumbers() {
        HashSet<String> favorites = new HashSet<>();
        favorites.add("15");
        favorites.add("29");

        List<Hexagram> allFavorites = HexagramRepository.filter("", HexagramRepository.FILTER_FAVORITES, favorites);
        List<Hexagram> searchedFavorites = HexagramRepository.filter("坎", HexagramRepository.FILTER_FAVORITES, favorites);

        assertEquals(2, allFavorites.size());
        assertTrue(allFavorites.stream().anyMatch(hex -> hex.number == 15));
        assertTrue(allFavorites.stream().anyMatch(hex -> hex.number == 29));
        assertEquals(1, searchedFavorites.size());
        assertEquals(29, searchedFavorites.get(0).number);
    }
}
