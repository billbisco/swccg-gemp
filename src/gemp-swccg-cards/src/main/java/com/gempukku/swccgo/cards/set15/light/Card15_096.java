package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LookAtLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardsAwayEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

import com.gempukku.swccgo.common.TitleComponent;
import com.gempukku.swccgo.cards.set4.light.Card4_059;

public class Card15_096 extends Card4_059 {
    public Card15_096() {
        setLore(""); addIcon(Icon.DAGOBAH, 0); addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent(Title.Keep_Your_Eyes_Open, Uniqueness.UNRESTRICTED),
                new TitleComponent("Shoo! Shoo!", Uniqueness.UNIQUE));
        setGameText("Glance at the cards in any Lost Pile and replace unshuffled. OR If you have a droid on table: Cancel an attack just initiated by a creature. OR Relocate one opponent's creature (except an attached creature) to an adjacent location (habitat permitting).");
    }
    @Override public String getTitle() { return "Keep Your Eyes Open & Shoo! Shoo!"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new java.util.ArrayList<>();
        List<PlayInterruptAction> shoo = super.getGameTextTopLevelActions(playerId, game, self);
        if (shoo != null) actions.addAll(shoo);
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasLostPile(game, playerId) || GameConditions.hasLostPile(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Look at Lost Pile");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.LOST_PILE) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, Zone cardPile) {
                            // Allow response(s)
                            action.allowResponses("Look at " + cardPileOwner + "'s " + cardPile.getHumanReadable(),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LookAtLostPileEffect(action, playerId, cardPileOwner));
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
