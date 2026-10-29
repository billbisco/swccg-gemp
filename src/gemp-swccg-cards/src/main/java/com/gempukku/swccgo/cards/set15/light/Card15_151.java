package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set5.light.Card5_075;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ReduceAttritionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Reflections Gold combination; does not include the original We'll Find Han deployment action. */
public class Card15_151 extends Card5_075 {
    public Card15_151() {
        setLore("");
        setGameText("Use 2 Force to target an opponent's non-droid character present with one of your Wookiees. Both players draw destiny. Add ability and power. Target is lost if Wookiee's total destiny > target's total destiny. OR If Chewie and your Lando are in a battle together, subtract 3 from attrition against you (cannot fall below zero). OR Cancel I Had No Choice.");
        setAlternateDestiny(5);
        addIcon(Icon.CLOUD_CITY, 0);
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Wookiee Strangle", "We'll Find Han");
    }
    @Override public String getTitle() { return "Wookiee Strangle & We'll Find Han"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        List<PlayInterruptAction> strangleActions = super.getGameTextTopLevelActions(playerId, game, self);
        if (strangleActions != null) actions.addAll(strangleActions);
        if (GameConditions.canTargetToCancel(game, self, Filters.I_Had_No_Choice)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.I_Had_No_Choice, Title.I_Had_No_Choice);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Chewie)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Lando))) {
            int currentAttrition = (int) Math.ceil(GameConditions.getAttritionRemaining(game, playerId));
            if (currentAttrition > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Reduce attrition by 3");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ReduceAttritionEffect(action, playerId, 3));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.I_Had_No_Choice)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
