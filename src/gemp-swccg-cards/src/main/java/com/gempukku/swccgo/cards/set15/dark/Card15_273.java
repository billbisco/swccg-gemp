package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.set5.dark.Card5_147;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceTopCardFromCardPileOnBottomOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.LinkedList;
import java.util.List;

public class Card15_273 extends Card5_147 {
    public Card15_273() {
        setCardSubtype(CardSubtype.USED);
        setAlternateDestiny(4);
        setLore("");
        setGameText("Relocate a card from the top of any Deck or Pile to the bottom (without looking). OR Cancel Demotion, Restricted Deployment, Commence Recharging, Logistical Delay, They're On Dantooine, Asteroid Sanctuary, Scramble, Order To Engage, No Disintegrations, Asteroids Do Not Concern Me, Report To Lord Vader or What Is Thy Bidding, My Master?");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Point Man", "Slip Sliding Away");
    }
    @Override public String getTitle() { return "Point Man & Slip Sliding Away"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private List<PlayInterruptAction> getGameTextTopLevelActionsFromCombinedCard(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final PlayInterruptAction action1 = new PlayInterruptAction(game, self);
        action1.setText("Relocate card to bottom of Deck or Pile");
        // Choose target(s)
        action1.appendCost(
                new ChooseExistingCardPileEffect(action1, playerId, true) {
                    @Override
                    protected void pileChosen(SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                        // Allow response(s)
                        action1.allowResponses("Place top card of " + cardPileOwner + "'s " + cardPile.getHumanReadable() + " on bottom",
                                new RespondablePlayCardEffect(action1) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        action1.appendEffect(
                                                new PlaceTopCardFromCardPileOnBottomOfCardPileEffect(action1, cardPileOwner, cardPile, cardPile));
                                    }
                                }
                        );
                    }
                }
        );
        actions.add(action1);

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> result = new java.util.ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextTopLevelActions(playerId, game, self);
        if (first != null) result.addAll(first);
        List<PlayInterruptAction> second = getGameTextTopLevelActionsFromCombinedCard(playerId, game, self);
        if (second != null) result.addAll(second);
        return result;
    }
}
