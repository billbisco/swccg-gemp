package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set12.dark.Card12_163;
import com.gempukku.swccgo.cards.set12.dark.Card12_162;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import java.util.List;

public class Card15_304 extends Card12_163 {
    public Card15_304() {
        setCardSubtype(CardSubtype.USED);
        setAlternateDestiny(4);
        setLore("");
        setGameText("Use 3 Force to take one Effect of any kind into hand from Reserve Deck; reshuffle. OR Deploy a Blockade Flagship site from your Reserve Deck; reshuffle. OR Take one Interrupt with the word \"Podracer(s)\" in its game text into hand from Reserve Deck; reshuffle. OR During your control phase, draw destiny. If destiny > 2, draw up to X cards (maximum 4) from Reserve Deck, where X = your total politics at Galactic Senate minus opponent's total politics at Galactic Senate.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        addComboCardTitles("We Must Accelerate Our Plans", "Vote Now!");
    }
    @Override public String getTitle() { return "We Must Accelerate Our Plans & Vote Now!"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private static final VoteNowActions VOTE_NOW = new VoteNowActions();

    // Retain this combination as the source of the unchanged Vote Now! action.
    private static final class VoteNowActions extends Card12_162 {
        List<PlayInterruptAction> actions(String playerId, SwccgGame game, PhysicalCard self) {
            return super.getGameTextTopLevelActions(playerId, game, self);
        }
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> result = new java.util.ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextTopLevelActions(playerId, game, self);
        if (first != null) result.addAll(first);
        List<PlayInterruptAction> second = VOTE_NOW.actions(playerId, game, self);
        if (second != null) result.addAll(second);
        return result;
    }
}
