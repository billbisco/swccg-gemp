package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set11.light.Card11_038;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_109 extends Card11_038 {
    public Card15_109() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(5);
        setLore("");
        setGameText("USED: If opponent just drew battle destiny (or destiny for Watto's Chance Cube), subtract one from that destiny. OR If you just verified opponent's Reserve Deck, search that Reserve Deck and place one Interrupt found there out of play. LOST: If difference between opponent's and your highest race totals < 4, lose 1 Force. Opponent draws no race destiny next control phase. OR If both players have < 10 Life Force remaining, both players retrieve 4 Force.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        setTitleComponents(new TitleComponent("Neck And Neck", Uniqueness.UNIQUE), new TitleComponent("Changing The Odds", Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "Neck And Neck & Changing The Odds"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private static final ChangingOddsActions ODDS = new ChangingOddsActions();
    private static final class ChangingOddsActions extends com.gempukku.swccgo.cards.set11.light.Card11_031 {
        List<PlayInterruptAction> actions(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
            List<PlayInterruptAction> actions = super.getGameTextOptionalAfterActions(player, game, result, self);
            actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
            return actions;
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String player, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = super.getGameTextTopLevelActions(player, game, self);
        actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST));
        return actions;
    }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
        return ODDS.actions(player, game, result, self);
    }
}
