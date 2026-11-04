package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerDuringBattleModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Starship
 * Subtype: Starfighter
 * Title: IG-2000 (V)
 */
public class Card1283_010 extends AbstractStarfighter {
    public Card1283_010() {
        super(Side.DARK, 4, 3, 3, null, 5, 5, 5, "IG-2000", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("IG-88's assault starfighter. Custom designed. Boasts a Kuat Galaxy-15 engine from a Nebulon-B frigate. Heavy ion cannon often used to disable starships before boarding.");
        setGameText("May add 1 pilot and 2 passengers. Rebel starships deploy +1 here. During battle, if IG-88 piloting, whenever you draw destiny cumulatively subtract 1 from opponent's total power. Immune to attrition < 3.");
        addIcons(Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.TRILON_AGGRESSOR);
        addPersona(Persona.IG2000);
        setPilotCapacity(1);
        setPassengerCapacity(2);
        setMatchingPilotFilter(Filters.IG88);
    }

    private static int destinyDrawsCounted(PhysicalCard self) {
        WhileInPlayData data = self.getWhileInPlayData();
        return data != null ? data.getIntValue() : 0;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final Condition ig88PilotingInBattle = new AndCondition(new HasPilotingCondition(self, Filters.IG88), new DuringBattleWithParticipantCondition(self));
        Evaluator minusOnePerDestinyDrawn = new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                return -destinyDrawsCounted(self);
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Rebel_starship, 1, Filters.sameLocation(self)));
        modifiers.add(new TotalPowerDuringBattleModifier(self, ig88PilotingInBattle, minusOnePerDestinyDrawn, game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Start each battle with no destiny draws counted
        if (TriggerConditions.battleInitiated(game, effectResult) || TriggerConditions.battleEnded(game, effectResult)) {
            if (destinyDrawsCounted(self) != 0) {
                self.setWhileInPlayData(null);
            }
            return null;
        }

        // Whenever you draw destiny during battle while IG-88 piloting, subtract 1 more from opponent's total power
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, self.getOwner())
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.hasPiloting(game, self, Filters.IG88)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Subtract 1 from opponent's total power");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            int count = destinyDrawsCounted(self) + 1;
                            self.setWhileInPlayData(new WhileInPlayData(count));
                            game.getGameState().sendMessage("Opponent's total power is -" + count + " from IG-2000");
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
