package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set5.dark.Card5_162;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddToAttritionEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.List;

public class Card15_282 extends Card5_162 {
    public Card15_282() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(3);
        setLore("");
        setGameText("USED: Cancel Noble Sacrifice, Gift Of The Mentor, or Old Ben. LOST: If Luke is defending a battle, draw one destiny. Subtract that amount from opponent's attrition and total power (cannot fall below zero). OR During a battle, lose 1 Force. Cumulatively adds 1 to attrition against opponent.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("Something Hit Us!",Uniqueness.UNRESTRICTED),new TitleComponent("Why Didn't You Tell Me?",Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "Something Hit Us! & Why Didn't You Tell Me?"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private List<PlayInterruptAction> getGameTextOptionalAfterActionsFromCombinedCard(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Add 1 to attrition against opponent");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddToAttritionEffect(action, opponent, 1, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> result = new java.util.ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextOptionalAfterActions(playerId, game, effectResult, self);
        if (first != null) result.addAll(first);
        List<PlayInterruptAction> second = getGameTextOptionalAfterActionsFromCombinedCard(playerId, game, effectResult, self);
        if (second != null) result.addAll(second);
        return result;
    }
}
