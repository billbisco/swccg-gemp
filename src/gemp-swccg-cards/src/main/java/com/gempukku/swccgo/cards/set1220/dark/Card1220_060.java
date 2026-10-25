package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayMakeAdditionalMovesModifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: He's All Yours, Bounty Hunter
 */
public class Card1220_060 extends AbstractUsedOrLostInterrupt {
    public Card1220_060() {
        super(Side.DARK, 4, Title.Hes_All_Yours_Bounty_Hunter, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Once Skywalker had taken the bait, Han was of no use to the Empire.");
        setGameText("USED: For remainder of turn, your bounty hunter escorting a captive may make two additional moves (for free if escorting Han). LOST: Your just drawn Carbon-Freezing (or bounty hunter's character weapon) destiny draw is +2.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter bountyHunterEscorting = Filters.and(Filters.your(self), Filters.bounty_hunter, Filters.escorting(Filters.any));
        if (GameConditions.canSpot(game, self, bountyHunterEscorting)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Grant two additional moves");
            action.allowResponses("Your bounty hunter escorting a captive may make two additional moves (for free if escorting Han)",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            Filter escortingCaptive = Filters.and(Filters.your(self), Filters.bounty_hunter, Filters.escorting(Filters.any));
                            Filter escortingHan = Filters.and(Filters.your(self), Filters.bounty_hunter, Filters.escorting(Filters.Han));
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new MayMakeAdditionalMovesModifier(self, escortingCaptive, 2),
                                            "Bounty hunter escorting a captive may make two additional moves"));
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new MovesForFreeModifier(self, escortingHan),
                                            "Moves for free if escorting Han"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && (TriggerConditions.isDestinyJustDrawnFor(game, effectResult, Filters.title(Title.Carbon_Freezing))
                || TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, playerId, Filters.character_weapon, Filters.bounty_hunter))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Add 2 to destiny");
            action.allowResponses("Add 2 to just drawn destiny",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, 2));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
