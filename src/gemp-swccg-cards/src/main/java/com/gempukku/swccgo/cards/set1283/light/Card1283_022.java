package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationWithoutPresenceOrForceIconsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Interrupt
 * Subtype: Lost
 * Title: Out Of Nowhere (V)
 */
public class Card1283_022 extends AbstractLostInterrupt {
    public Card1283_022() {
        super(Side.LIGHT, 5, Title.Out_Of_Nowhere, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Black market sensor-stealth arrays can be used to hide starships from enemy sensor scans. Stellar interference can naturally shield a starship for surprise attacks.");
        setGameText("Use 2 Force to target a system or planet site. For remainder of turn, you may deploy there even without presence or Force icons. (Immune to Sense.) OR If you just deployed a non-Jedi character to a location opponent occupies, take any one card from Used Pile into hand; reshuffle.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter targetFilter = Filters.or(Filters.system, Filters.planet_site);

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy to a location without presence or Force icons");
            action.setImmuneTo(Title.Sense);
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose system or planet site", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard location) {
                            action.addAnimationGroup(location);
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 2));
                            action.allowResponses("Allow deploying to " + GameUtils.getCardLink(location) + " without presence or Force icons for remainder of turn",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalLocation = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new MayDeployToLocationWithoutPresenceOrForceIconsModifier(self, Filters.your(playerId), null, Filters.sameLocationId(finalLocation)),
                                                            "Allows " + playerId + " to deploy to " + GameUtils.getCardLink(finalLocation) + " without presence or Force icons"));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.justDeployedTo(game, effectResult, playerId, Filters.and(Filters.character, Filters.not(Filters.Jedi)), Filters.occupies(opponent))
                && GameConditions.hasUsedPile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Take a card from Used Pile into hand");
            action.allowResponses("Take any one card from Used Pile into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromUsedPileEffect(action, playerId, true));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
