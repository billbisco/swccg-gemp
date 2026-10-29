package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.choose.ChooseAndLoseCardsFromHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Vehicle
 * Subtype: Combat
 * Title: Blizzard 1
 */
public class Card1220_080 extends AbstractCombatVehicle {
    public Card1220_080() {
        super(Side.DARK, 2, 5, 7, 7, null, 1, 7, Title.Blizzard_1, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("General Veers' AT-AT. Enclosed. Equipped with highly sophisticated communications gear. Employs an experimental targeting system.");
        setGameText("May add 2 pilots and 8 passengers. During the power segment of a battle here, may lose X cards from hand to cause +X attrition against opponent. Immune to attrition < 4.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.HOTH, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        addPersona(Persona.BLIZZARD_1);
        setPilotCapacity(2);
        setPassengerCapacity(8);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        if (TriggerConditions.justBeforePlayersDrawBattleDestiny(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.here(self))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && !GameConditions.isDamageSegmentOfBattle(game)
                && GameConditions.hasHand(game, playerId)) {

            final int maxCards = GameConditions.numCardsInHand(game, playerId);
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
            action.setText("Lose cards from hand to add attrition");
            action.setActionMsg("Lose X cards from hand to cause +X attrition against opponent");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendCost(
                    new PlayoutDecisionEffect(action, playerId,
                            new IntegerAwaitingDecision("Choose X (cards to lose)", 1, maxCards, 1) {
                                @Override
                                public void decisionMade(int x) {
                                    action.appendCost(
                                            new ChooseAndLoseCardsFromHandEffect(action, playerId, x, x, Filters.any));
                                    action.appendEffect(
                                            new AddUntilEndOfBattleModifierEffect(action,
                                                    new AttritionModifier(self, x, opponent),
                                                    "Attrition against " + opponent + " is +" + x));
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }
}
