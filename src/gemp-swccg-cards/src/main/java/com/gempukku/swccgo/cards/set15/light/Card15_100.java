package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set8.light.Card8_057;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_100 extends Card8_057 {
    public Card15_100() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(5);
        setLore("");
        setGameText("USED: Add 1 to forfeit of each of your Ewoks for remainder of turn. LOST: Cancel a Force drain at an exterior planet site where opponent has no vehicles or starships. Draw destiny. If destiny > number of opponent's characters at that site, choose one of those characters to be missing.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("Lost In The Wilderness", Uniqueness.UNIQUE), new TitleComponent("Yub Yub!", Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "Lost In The Wilderness & Yub Yub!"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private static final YubYubActions YUB_YUB = new YubYubActions();
    private static final class YubYubActions extends com.gempukku.swccgo.cards.set8.light.Card8_066 {
        List<PlayInterruptAction> usedActions(String player, SwccgGame game, PhysicalCard self) {
            // Only the Used mode is printed on this combination.
            return super.getGameTextTopLevelActions(player, game, self).stream()
                    .filter(action -> action.getPlayedAsSubtype() == CardSubtype.USED).toList();
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String player, SwccgGame game, PhysicalCard self) {
        return YUB_YUB.usedActions(player, game, self);
    }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
        List<PlayInterruptAction> actions = super.getGameTextOptionalAfterActions(player, game, result, self);
        if (actions != null) actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST));
        return actions;
    }
}
