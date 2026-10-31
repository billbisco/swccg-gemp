package com.gempukku.swccgo.cards.set1111.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Effect
 * Title: Advantage
 */
public class Card1111_001 extends AbstractNormalEffect {
    public Card1111_001() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Advantage, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("With only a few Jedi remaining in the galaxy, each confrontation between them has far reaching consequences.");
        setGameText("Deploy on your battleground. During battle, whenever opponent's card is hit by a non-[Permanent Weapon] (once per card targeted), opponent's total power is cumulatively -4 (-2 if hit by a lightsaber).");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.battleground, Filters.occupies(self.getOwner()));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.battleground;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.battleLocation, new AdvantagePowerEvaluator(self), opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.battleEnded(game, effectResult)) {
            self.setWhileInPlayData(null);
            return null;
        }

        if (!GameConditions.isDuringBattle(game)
                || !TriggerConditions.justHitBy(game, effectResult, Filters.opponents(self), Filters.and(Filters.weapon, Filters.not(Icon.PERMANENT_WEAPON)))
                || ((HitResult) effectResult).getHitByPermanentWeapon() != null) {
            return null;
        }

        HitResult hitResult = (HitResult) effectResult;
        PhysicalCard cardHit = hitResult.getCardHit();
        String hitId = String.valueOf(cardHit.getCardId());
        WhileInPlayData data = self.getWhileInPlayData();
        if (data != null && data.getTextValues().contains(hitId)) {
            return null;
        }

        PhysicalCard hitBy = hitResult.getHitByCard();
        final float amount = (hitBy != null && Filters.lightsaber.accepts(game, hitBy)) ? -2f : -4f;
        if (data == null) {
            data = new WhileInPlayData(0f);
            self.setWhileInPlayData(data);
        }
        data.getTextValues().add(hitId);

        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Make opponent's total power " + (int) amount);
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        WhileInPlayData current = self.getWhileInPlayData();
                        float running = (current != null && current.getFloatValue() != null) ? current.getFloatValue() : 0f;
                        WhileInPlayData next = new WhileInPlayData(running + amount);
                        if (current != null) {
                            next.getTextValues().addAll(current.getTextValues());
                        }
                        self.setWhileInPlayData(next);
                    }
                });
        return Collections.singletonList(action);
    }

    private static class AdvantagePowerEvaluator extends BaseEvaluator {
        private final PhysicalCard _source;

        private AdvantagePowerEvaluator(PhysicalCard source) {
            _source = source;
        }

        @Override
        public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
            WhileInPlayData data = _source.getWhileInPlayData();
            return (data != null && data.getFloatValue() != null) ? data.getFloatValue() : 0;
        }
    }
}
