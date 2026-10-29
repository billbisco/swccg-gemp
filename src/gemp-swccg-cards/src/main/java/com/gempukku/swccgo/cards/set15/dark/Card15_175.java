package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set1.dark.Card1_210;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnUsedPileEffect;
import java.util.Collections;
import java.util.List;

public class Card15_175 extends Card1_210 {
    public Card15_175() {
        setLore("");
        setGameText("Deploy on table. Cancels: Into The Garbage Chute, Flyboy, Narrow Escape, Blast The Door Kid!, and Rebel Barrier. Also, during your control phase, you may return any cards from your hand to the top of your Used Pile.");
        setAlternateDestiny(3);
        addComboCardTitles("Blast Door Controls", "Reactor Terminal");
        addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Blast Door Controls & Reactor Terminal"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int gameTextSourceCardId) {
        if (!GameConditions.isDuringYourPhase(game, self, Phase.CONTROL) || !GameConditions.hasHand(game, playerId)) return null;
        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
        action.setText("Place cards from hand on Used Pile");
        action.appendEffect(new PutCardsFromHandOnUsedPileEffect(action, playerId, 1, Integer.MAX_VALUE));
        return Collections.singletonList(action);
    }
}
