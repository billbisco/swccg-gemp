package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
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
 * Title: Dagobah
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print cancels Force drains instead of forbidding them: Light Side "Opponent's Force drains here are canceled. You
 * may not Force drain here." and Dark Side "If you occupy, opponent's Force drains at related locations are canceled."
 * The Players Committee Advanced Rulebook (Appendix A) rewrote them as "Neither player may Force drain here." and
 * "If you occupy, opponent may not Force drain at related locations." (the bare card). A canceled drain is still
 * initiated, so cards that respond to a Force drain being initiated can act.
 */
public class Card7_279o extends Card7_279 {
    public Card7_279o() {
        super();
        setLocationDarkSideGameText("If you occupy, opponent's Force drains at related locations are canceled.");
        setLocationLightSideGameText("Opponent's Force drains here are canceled. You may not Force drain here.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_279", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        // You may not Force drain here (opponent's drains here are canceled by the trigger below)
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Opponent's Force drains here are canceled
        RequiredGameTextTriggerAction action = cancelForceDrainAction(game, effectResult, self, gameTextSourceCardId,
                playerOnLightSideOfLocation, game.getOpponent(playerOnLightSideOfLocation), Filters.sameCardId(self));
        return action != null ? Collections.singletonList(action) : null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        // Printed: drains at related locations are canceled (trigger below), not prevented
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // If you occupy, opponent's Force drains at related locations are canceled
        if (!GameConditions.occupies(game, playerOnDarkSideOfLocation, self))
            return null;
        RequiredGameTextTriggerAction action = cancelForceDrainAction(game, effectResult, self, gameTextSourceCardId,
                playerOnDarkSideOfLocation, game.getOpponent(playerOnDarkSideOfLocation), Filters.relatedLocation(self));
        return action != null ? Collections.singletonList(action) : null;
    }

    /** Cancels a Force drain the given player initiates at the given locations (the printed "are canceled" wording). */
    public static RequiredGameTextTriggerAction cancelForceDrainAction(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId,
                                                               String cancelingPlayer, String drainingPlayer, Filter locations) {
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, drainingPlayer, locations)
                && GameConditions.canCancelForceDrain(game, cancelingPlayer, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Force drain");
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            return action;
        }
        return null;
    }
}
