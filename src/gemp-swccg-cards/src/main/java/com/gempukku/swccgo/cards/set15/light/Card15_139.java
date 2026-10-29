package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set6.light.Card6_068;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_139 extends Card6_068 {
    public Card15_139() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(4);
        setLore("");
        setGameText("USED: Use 3 Force to search your Reserve Deck and take one Effect of any kind into hand; reshuffle. OR Cancel a Force drain at a site if Luke is at an adjacent site. LOST: If your character of ability > 4 is present with an opponent's leader, release a captive from a related prison (canceled if opponent loses 2 Force).");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("The Signal", Uniqueness.UNIQUE), new TitleComponent("Jedi Mind Trick", Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "The Signal & Jedi Mind Trick"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }


    private static final ComponentActions COMPONENT = new ComponentActions();
    private static final class ComponentActions extends com.gempukku.swccgo.cards.set6.light.Card6_077 {
        List<PlayInterruptAction> top(String player, SwccgGame game, PhysicalCard self) {
            List<PlayInterruptAction> actions = super.getGameTextTopLevelActions(player, game, self);
            if (actions != null) actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
            return actions;
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String player, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextTopLevelActions(player, game, self);
        if (first != null) actions.addAll(first);
        List<PlayInterruptAction> second = COMPONENT.top(player, game, self);
        if (second != null) actions.addAll(second);
        return actions;
    }
}
