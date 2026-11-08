package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: I Want That Ship
 *
 * Original version of 1220_35 (Virtual Block 2 release sheet, Dec 2009, card #35): every hit on a different opponent's
 * card during battle makes opponent's total power cumulatively -4 (-2 if by a lightsaber), not once per battle and
 * not optional as on the Virtual Cards Master List (2013-04-27).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_035 extends AbstractNormalEffect {
    public Card2220_035() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.I_Want_That_Ship, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Like nerf herders herding nerfs, Imperial commanders often use TIE fighters to drive fleeing Rebel ships into tractor beam range.");
        setGameText("Deploy on your battleground. During battle, whenever opponent's card is hit by a non-[Permanent Weapon] weapon (once per card targeted), opponent's total power is cumulatively -4 (-2 if hit by a lightsaber).");
        setLegacyVersion(new LegacyVersion("1220_35", "",
                "Virtual Block 2 release sheet (2009-12-07), card #35", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
        addIcons(Icon.DAGOBAH);
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
        return List.of(totalPowerModifier(game, self));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return hitTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    // Shared with the original Advantage (2220_111), which has the same text.
    // The card's while-in-play data holds the battle's running total (float) and the ids of the opponent's cards
    // already counted (once per card targeted). It is reset when the battle ends.

    public static Modifier totalPowerModifier(SwccgGame game, final PhysicalCard self) {
        return new TotalPowerModifier(self, Filters.battleLocation, new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                WhileInPlayData data = self.getWhileInPlayData();
                return (data != null && data.getFloatValue() != null) ? data.getFloatValue() : 0;
            }
        }, game.getOpponent(self.getOwner()));
    }

    public static List<RequiredGameTextTriggerAction> hitTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.battleEnded(game, effectResult) && self.getWhileInPlayData() != null) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reset opponent's total power modifier");
            action.setActionMsg(null);
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setWhileInPlayData(null);
                        }
                    });
            return Collections.singletonList(action);
        }
        if (!GameConditions.isDuringBattle(game)
                || !TriggerConditions.justHitBy(game, effectResult, Filters.opponents(self), Filters.and(Filters.weapon, Filters.not(Icon.PERMANENT_WEAPON)))
                || ((HitResult) effectResult).getHitByPermanentWeapon() != null)
            return null;
        HitResult hitResult = (HitResult) effectResult;
        final String hitId = String.valueOf(hitResult.getCardHit().getCardId());
        WhileInPlayData data = self.getWhileInPlayData();
        if (data != null && data.getTextValues().contains(hitId))
            return null;
        PhysicalCard hitBy = hitResult.getHitByCard();
        final float amount = (hitBy != null && Filters.lightsaber.accepts(game, hitBy)) ? -2 : -4;
        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Make opponent's total power " + (int) amount);
        action.setActionMsg("Make opponent's total power cumulatively " + (int) amount);
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        WhileInPlayData current = self.getWhileInPlayData();
                        float total = (current != null && current.getFloatValue() != null) ? current.getFloatValue() : 0;
                        WhileInPlayData next = new WhileInPlayData(total + amount);
                        if (current != null)
                            next.getTextValues().addAll(current.getTextValues());
                        next.getTextValues().add(hitId);
                        self.setWhileInPlayData(next);
                    }
                });
        return Collections.singletonList(action);
    }
}
