package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set1.light.Card1_098;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardsAwayEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;



/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_107 extends Card1_098 {
    public Card15_107() {
        setCardSubtype(CardSubtype.USED);
        setAlternateDestiny(5);
        setLore("");
        setGameText("If opponent just initiated battle at a site where you have a Rebel of ability > 2 present, move all of your cards with ability there away (using their landspeed at normal use of Force) and cancel battle.");
        setCredits(3);
        setAsHorizontal(true);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Narrow Escape"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.and(Filters.site,
                Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.Rebel, Filters.abilityMoreThan(2),
                        Filters.presentInBattle, Filters.canBeTargetedBy(self))), Filters.canBeTargetedBy(self)))
                && GameConditions.canSpotLocation(game, Filters.adjacentSite(game.getGameState().getBattleLocation()))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Move cards with ability away");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a Rebel of ability > 2", Filters.and(Filters.your(self), Filters.Rebel, Filters.abilityMoreThan(2), Filters.presentInBattle)) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardTargeted(final int targetGroupId1, PhysicalCard targetedRebel) {
                            action.addAnimationGroup(targetedRebel);
                            // Allow response(s)
                            action.allowResponses("Move cards with ability away by targeting " + GameUtils.getCardLink(targetedRebel),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MoveCardsAwayEffect(action, playerId, Filters.and(Filters.your(self),
                                                            Filters.hasAbility, Filters.participatingInBattle, Filters.canBeTargetedBy(self))));
                                            action.appendEffect(new com.gempukku.swccgo.cards.effects.CancelBattleEffect(action));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
