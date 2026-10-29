package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set1.light.Card1_100;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections Gold
 * Type: Interrupt
 * Subtype: Lost
 * Title: Old Ben & Noble Sacrifice
 */
public class Card15_115 extends Card1_100 {
    public Card15_115() {
        setLore("");
        setAlternateDestiny(5);
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Old Ben", "Noble Sacrifice");
        setGameText("Use 1 Force if any of your characters (except Obi-Wan) was just forfeited from a Tatooine site. Mysterious \"crazy wizard\" steps in and revives (relocates) that character back to same site. OR If opponent just deployed a character, sacrifice (place out of play) from table one of your characters with the same power (even a captured character). Retrieve Force equal to your sacrificed character's forfeit value.");
    }
    @Override public String getTitle() { return "Old Ben & Noble Sacrifice"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> revive = super.getGameTextOptionalAfterActions(playerId, game, effectResult, self);
        if (revive != null) return revive;
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.and(Filters.character, Filters.canBeTargetedBy(self)))) {
            PhysicalCard justDeployedCharacter = ((PlayCardResult) effectResult).getPlayedCard();
            float power = game.getModifiersQuerying().getPower(game.getGameState(), justDeployedCharacter);
            final Filter characterToSacrificeFilter = Filters.and(Filters.your(self), Filters.character, Filters.powerEqualTo(power));
            final TargetingReason targetingReason = TargetingReason.TO_BE_PLACED_OUT_OF_PLAY;
            if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, targetingReason, characterToSacrificeFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Sacrifice a character");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose deployed character", justDeployedCharacter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedDeployedCharacter) {
                                action.addAnimationGroup(targetedDeployedCharacter);
                                // Choose target(s)
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose character to sacrifice", SpotOverride.INCLUDE_CAPTIVE, targetingReason, characterToSacrificeFilter) {
                                            @Override
                                            protected void cardTargeted(int targetGroupId, PhysicalCard targetedCharacterToSacrifice) {
                                                final boolean mayNotContributeToForceRetrieval = !Filters.mayContributeToForceRetrieval.accepts(game, targetedCharacterToSacrifice);
                                                final float forfeitValue = game.getModifiersQuerying().getForfeit(game.getGameState(), targetedCharacterToSacrifice);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new PlaceCardOutOfPlayFromTableEffect(action, targetedCharacterToSacrifice));
                                                // Allow response(s)
                                                action.allowResponses("Retrieve " + GuiUtils.formatAsString(forfeitValue) + " Force by targeting " + GameUtils.getCardLink(targetedDeployedCharacter),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                if (mayNotContributeToForceRetrieval) {
                                                                    game.getGameState().sendMessage("Force retrieval not allowed due to including cards not allowed to contribute to Force retrieval");
                                                                    return;
                                                                }
                                                                action.appendEffect(
                                                                        new RetrieveForceEffect(action, playerId, forfeitValue));
                                                            }
                                                        }
                                                );
                                            }
                                        });
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
