package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.set4.dark.Card4_118;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReduceBattleDamageEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 6.2: Broken Concentration (V). */
public class Card1262_005 extends Card4_118 {
    public Card1262_005() {
        setGameText("Deploy on table. Cancels game text of [Reflections 3] Naboo sites. Training destiny draws are -X, where X = apprentice's ability - Jedi Test Number. Once per game, may place this Effect out of play to reduce your battle damage by 4. Immune to NOOOOOOOOOOOO!. (Immune to Alter.)");
        setVirtualSuffix(true);
        addIcon(Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
        addImmuneToCardTitle(Title.Alter);
        addImmuneToCardTitle(Title.NOOOOOOOOOOOO);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_6_2;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.V;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new CancelsGameTextModifier(self, Filters.and(Icon.REFLECTIONS_III, Filters.Naboo_site)),
                new EachTrainingDestinyModifier(self, Filters.any, new BaseEvaluator() {
                    @Override
                    public float evaluateExpression(GameState state, ModifiersQuerying querying, PhysicalCard affected) {
                        // During a draw the modifier receives the drawn card; the live draw owns the test.
                        var draw = state.getTopDrawDestinyState();
                        var jediTest = draw != null && draw.getDrawDestinyEffect().getDestinyType() == DestinyType.TRAINING_DESTINY
                                ? draw.getDrawDestinyEffect().getAction().getActionSource() : affected;
                        if (jediTest == null) return 0;
                        var apprentice = jediTest.getTargetedCard(state, TargetId.JEDI_TEST_APPRENTICE);
                        if (apprentice == null) return 0;
                        return querying.getJediTestNumber(state, jediTest) - querying.getAbility(state, apprentice);
                    }
                }));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int sourceId) {
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
                                                                                  EffectResult result, PhysicalCard self,
                                                                                  int sourceId) {
        GameTextActionId id = GameTextActionId.LEGACY__BROKEN_CONCENTRATION_V__REDUCE_BATTLE_DAMAGE;
        if (!TriggerConditions.isResolvingBattleDamageAndAttrition(game, result, playerId)
                || !GameConditions.isBattleDamageRemaining(game, playerId)
                || !GameConditions.isOncePerGame(game, self, id)) {
            return null;
        }
        var action = new OptionalGameTextTriggerAction(self, sourceId, id);
        action.setText("Place out of play to reduce battle damage by 4");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendCost(new PlaceCardOutOfPlayFromTableEffect(action, self));
        action.appendEffect(new ReduceBattleDamageEffect(action, playerId, 4));
        return List.of(action);
    }
}
