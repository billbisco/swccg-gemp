package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Droid Merchant
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print: "Cumulatively activates 1 Force each time you deploy a droid (or retrieves 1 Force when you deploy an
 * astromech to a starfighter). Spaceport Speeders may be played at same site (place droid in Used Pile instead of Lost
 * Pile)." The Players Committee Advanced Rulebook (Appendix A) made it once per game and dropped the Used Pile clause
 * (the bare card, Card7_015). This state activates (or retrieves) every time, for each Droid Merchant on table, and
 * Spaceport Speeders (Card1_112) places a droid sold at this Droid Merchant's site in the Used Pile.
 */
public class Card7_015o extends Card7_015 {
    /** An active printed-text Droid Merchant (Spaceport Speeders checks it for the Used Pile clause). */
    public static final Filter PRINTED_DROID_MERCHANT = Filters.and(Filters.Droid_Merchant, Filters.not(Filters.isGameTextCanceled),
            new Filter() {
                @Override
                public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                    return physicalCard.getBlueprint() instanceof Card7_015o;
                }
            });

    public Card7_015o() {
        super();
        setGameText("Cumulatively activates 1 Force each time you deploy a droid (or retrieves 1 Force when you deploy an astromech to a starfighter). Spaceport Speeders may be played at same site (place droid in Used Pile instead of Lost Pile).");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_15", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // The printed text is not optional and not once per game (see the required trigger below)
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        // Astromech deployed to a starfighter: retrieve 1 Force instead of activating
        if (TriggerConditions.justDeployedAboard(game, effectResult, Filters.and(Filters.your(self), Filters.astromech_droid), Filters.starfighter)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            action.setActionMsg("Have " + playerId + " retrieve 1 Force");
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        // Any other droid of yours deployed: activate 1 Force
        if (TriggerConditions.justDeployed(game, effectResult, Filters.and(Filters.your(self), Filters.droid))
                && GameConditions.canActivateForce(game, playerId)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Activate 1 Force");
            action.setActionMsg("Have " + playerId + " activate 1 Force");
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
