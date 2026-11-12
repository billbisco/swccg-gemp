package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.set7.dark.Card7_279o;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedWhenMovingFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedWhenMovingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location (System)
 * Title: Rendezvous Point
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print says "Opponent's Force drains here are canceled. You may not Force drain here."; the Players Committee
 * Advanced Rulebook (Appendix A) rewrote it as "Neither player may Force drain here." (the bare card). Opponent may
 * initiate a Force drain here, and it is canceled.
 */
public class Card7_123o extends Card7_123 {
    public Card7_123o() {
        super();
        setLocationLightSideGameText("Your starships deploy -2 here and are hyperspeed +2 when moving to or from here. Opponent's Force drains here are canceled. You may not Force drain here.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_123", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourStarships = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourStarships, -2, self));
        modifiers.add(new HyperspeedWhenMovingToLocationModifier(self, yourStarships, 2, self));
        modifiers.add(new HyperspeedWhenMovingFromLocationModifier(self, yourStarships, 2, self));
        // You may not Force drain here (opponent's drains here are canceled by the trigger below)
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Opponent's Force drains here are canceled
        RequiredGameTextTriggerAction action = Card7_279o.cancelForceDrainAction(game, effectResult, self, gameTextSourceCardId,
                playerOnLightSideOfLocation, game.getOpponent(playerOnLightSideOfLocation), Filters.sameCardId(self));
        return action != null ? Collections.singletonList(action) : null;
    }
}
