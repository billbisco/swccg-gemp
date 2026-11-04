package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Locale;

/**
 * Filter that accepts "a device that deploys on a starship" (Tatooine: Imperial Vanguard Camp 1283_13): a device
 * whose printed deployment sentence ("Deploy on ...", the first sentence of its game text) names a starship
 * (starship, starfighter, capital, freighter, Star Destroyer, TIE, Falcon). GEMP has no keyword for this and the
 * blueprints' deploy-target filters are not reachable from here, so the printed deploy restriction is read.
 */
public class DeviceThatDeploysOnStarshipFilter extends Filter {
    private static final String[] STARSHIP_WORDS = {"starship", "starfighter", "capital", "freighter", "star destroyer", "tie", "falcon"};

    public static final DeviceThatDeploysOnStarshipFilter INSTANCE = new DeviceThatDeploysOnStarshipFilter();

    private DeviceThatDeploysOnStarshipFilter() {
    }

    @Override
    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        if (physicalCard.getBlueprint().getCardCategory() != CardCategory.DEVICE)
            return false;
        return deploysOnStarship(physicalCard.getBlueprint().getGameText());
    }

    static boolean deploysOnStarship(String gameText) {
        if (gameText == null)
            return false;
        String text = gameText.trim();
        if (!text.startsWith("Deploy on"))
            return false;
        int end = text.indexOf('.');
        String deploySentence = (end >= 0 ? text.substring(0, end) : text).toLowerCase(Locale.ROOT);
        for (String word : STARSHIP_WORDS) {
            if (deploySentence.matches(".*\\b" + word + "s?\\b.*"))
                return true;
        }
        return false;
    }
}
