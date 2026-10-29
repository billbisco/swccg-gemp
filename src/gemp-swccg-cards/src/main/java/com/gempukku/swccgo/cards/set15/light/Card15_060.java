package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set1.light.Card1_070;
import com.gempukku.swccgo.common.Icon;
import java.util.ArrayList;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections Gold
 * Type: Interrupt
 * Subtype: Used
 * Title: A Few Maneuvers & Han's Dice
 */
public class Card15_060 extends Card1_070 {
    public Card15_060() {
        setLore("");
        setAlternateDestiny(5);
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("A Few Maneuvers", "Han's Dice");
        setGameText("Add 2 to hyperspeed and maneuver of any starfighter for the remainder of this turn. (Interrupt may even affect the result immediately after a destiny draw targeting the starfighter's maneuver.) OR If one of your characters of ability > 2 is in a battle, use 1 Force to draw another battle destiny instead of the one you just drew.");
    }
    @Override public String getTitle() { return "A Few Maneuvers & Han's Dice"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        List<PlayInterruptAction> maneuvers = super.getGameTextOptionalAfterActions(playerId, game, effectResult, self);
        if (maneuvers != null) actions.addAll(maneuvers);
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;
        Filter targetFilter = Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(2), Filters.participatingInBattle);

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Cancel and re-draw battle destiny");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetFilter) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard characterTargeted) {
                            action.addAnimationGroup(characterTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Cancel and re-draw battle destiny by targeting " + GameUtils.getCardLink(characterTargeted),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelDestinyAndCauseRedrawEffect(action));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
