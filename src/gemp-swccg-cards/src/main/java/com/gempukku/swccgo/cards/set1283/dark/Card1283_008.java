package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.EatenByIsPlacedOutOfPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.EatenResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Creature
 * Subtype: Ancient Desert
 * Title: Sarlacc (V)
 */
public class Card1283_008 extends AbstractCreature {
    public Card1283_008() {
        super(Side.DARK, 2, 4, null, 12, 0, "Sarlacc", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Very patient predator in the Dune Sea. Tentacles can grab prey up to four meters away. Digests victims for 1000 years. Often fed prisoners by Jabba the Hutt.");
        setGameText("* Ferocity = 4 + destiny. Habitat: Great Pit Of Carkoon. X = number of your aliens here. Captives here are power -X. Anything eaten by Sarlacc is placed out of play (if a captive, retrieve up to X Force).");
        addModelType(ModelType.ANCIENT_DESERT);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.Great_Pit_Of_Carkoon;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextFerocityModifier(self, 4, 1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.captive, Filters.here(self)),
                new NegativeEvaluator(new HereEvaluator(self, Filters.and(Filters.your(self), Filters.alien)))));
        modifiers.add(new EatenByIsPlacedOutOfPlayModifier(self));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justEatenBy(game, effectResult, Filters.any, self)
                && ((EatenResult) effectResult).wasCaptive()) {
            final int x = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.alien, Filters.here(self), Filters.mayContributeToForceRetrieval));
            if (x > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Retrieve up to " + x + " Force");
                // Perform result(s)
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to retrieve", 0, x, x) {
                                    @Override
                                    public void decisionMade(final int amountToRetrieve) throws DecisionResultInvalidException {
                                        if (amountToRetrieve > 0) {
                                            action.appendEffect(
                                                    new RetrieveForceEffect(action, playerId, amountToRetrieve));
                                        }
                                    }
                                }));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
