package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeLostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Rogue Squadron X-wing, Virtual Block 6.3. */
public class Card1263_034 extends AbstractStarfighter {
    public Card1263_034() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 5, 4, "Rogue Squadron X-wing",
                Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setGameText("May add 1 pilot and 1 astromech. While Rebel Aces on table, pilot is forfeit +2 and, if in battle, this card may not be lost before the damage segment. Matching starship for any Rogue Squadron pilot.");
        addIcons(Icon.SPECIAL_EDITION, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.X_WING); addKeywords(Keyword.ROGUE_SQUADRON);
        setPilotCapacity(1); setAstromechCapacity(1); setMatchingPilotFilter(Filters.Rogue_Squadron_pilot);
    }

    private boolean protectedBeforeDamage(SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringBattle(game) && !GameConditions.isDamageSegmentOfBattle(game)
                && GameConditions.canSpot(game, self, Filters.title("Rebel Aces"));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition protection = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return protectedBeforeDamage(gameState.getGame(), self);
            }
        };
        return Arrays.asList(new ForfeitModifier(self, Filters.piloting(self),
                        new OnTableCondition(self, Filters.title("Rebel Aces")), 2),
                new MayNotTargetToBeLostModifier(self, self, protection));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult,
            PhysicalCard self, int gameTextSourceCardId) {
        if (protectedBeforeDamage(game, self) && TriggerConditions.isAboutToBeLost(game, effectResult, self)) {
            AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            if (result.getPreventableCardEffect() != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Prevent loss before damage segment");
                action.appendEffect(new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        result.getPreventableCardEffect().preventEffectOnCard(self);
                    }
                });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
