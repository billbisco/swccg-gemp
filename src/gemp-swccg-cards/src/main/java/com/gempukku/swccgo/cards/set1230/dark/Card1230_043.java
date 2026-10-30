package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.effects.PlaceCardInForcePileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used
 * Title: None Shall Pass
 */
public class Card1230_043 extends AbstractUsedInterrupt {
    public Card1230_043() {
        super(Side.DARK, 5, Title.None_Shall_Pass, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jabba's Gamorrean guards keep a watchful eye for unwelcome guests.");
        setGameText("If opponent just deployed a character (except Leia) to a Jabba's Palace site, use 1 Force to place that character on bottom of opponent's Force Pile (opponent may draw two cards from Reserve Deck).");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        Filter justDeployed = Filters.and(Filters.opponents(self), Filters.character, Filters.not(Filters.Leia));
        if (TriggerConditions.justDeployedToLocation(game, effectResult, opponent, justDeployed, Filters.Jabbas_Palace_site)
                && GameConditions.canUseForce(game, playerId, 1)
                && effectResult instanceof PlayCardResult) {
            final PhysicalCard deployed = ((PlayCardResult) effectResult).getPlayedCard();
            if (deployed != null) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Place character on bottom of Force Pile");
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                action.allowResponses("Place " + deployed.getTitle() + " on bottom of opponent's Force Pile",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new PlaceCardInForcePileFromTableEffect(action, deployed, true));
                                action.appendEffect(
                                        new DrawCardsIntoHandFromReserveDeckEffect(action, opponent, 2));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
