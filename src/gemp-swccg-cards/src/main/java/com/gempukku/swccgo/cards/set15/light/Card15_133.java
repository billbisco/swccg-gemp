package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.cards.set12.light.Card12_071;

import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;

import java.util.List;


import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.cards.set12.light.Card12_069;

public class Card15_133 extends Card12_069 {
    public Card15_133() {
        setLore(""); addIcon(Icon.CORUSCANT, 0); addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Speak With The Jedi Council", "Vote Now!");
        setGameText("USED: If the Jedi Council Chamber is not on table, you may deploy it from Reserve Deck; reshuffle. OR During your control phase, draw destiny. If destiny > 2, draw up to X cards (maximum 4) from Reserve Deck, where X = your total politics at Galactic Senate minus opponent's total politics at Galactic Senate. LOST: Deploy one Jedi Council member to the Jedi Council Chamber from Reserve Deck; reshuffle.");
    }
    @Override public String getTitle() { return "Speak With The Jedi Council & Vote Now!"; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new java.util.ArrayList<>();
        List<PlayInterruptAction> council = super.getGameTextTopLevelActions(playerId, game, self);
        if (council != null) actions.addAll(council);
        List<PlayInterruptAction> second = SECOND.actions(playerId, game, self);
        if (second != null) {
            second.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
            actions.addAll(second);
        }
        return actions;
    }
    private static final VoteNowActions SECOND = new VoteNowActions();

    // Expose the unchanged component hook while retaining this combination as its source.
    private static final class VoteNowActions extends Card12_071 {
        List<PlayInterruptAction> actions(String playerId, SwccgGame game, PhysicalCard self) {
            return super.getGameTextTopLevelActions(playerId, game, self);
        }
    }
}
