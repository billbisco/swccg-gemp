package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set12.light.Card12_063;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import java.util.ArrayList;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_105 extends Card12_063 {
    public Card15_105() {
        setCardSubtype(CardSubtype.USED);
        setAlternateDestiny(4);
        setLore("");
        setGameText("Once per game, retrieve 1 Force for each of your senators at Galactic Senate. (Immune to Sense.) OR Deploy a handmaiden to Amidala's location from Reserve Deck; reshuffle. OR If opponent just initiated battle at same site as your handmaiden and Amidala, add one battle destiny. OR Take one senator (or Coruscant Guard, Sei Taria or Mas Amedda) into hand from Reserve Deck; reshuffle.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        setTitleComponents(new TitleComponent("My Loyal Bodyguard", Uniqueness.UNIQUE), new TitleComponent("Might Of The Republic", Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "My Loyal Bodyguard & Might Of The Republic"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private static final RepublicActions REPUBLIC = new RepublicActions();
    private static final class RepublicActions extends com.gempukku.swccgo.cards.set12.light.Card12_061 {
        List<PlayInterruptAction> actions(String player, SwccgGame game, PhysicalCard self) {
            return super.getGameTextTopLevelActions(player, game, self);
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String player, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>(REPUBLIC.actions(player, game, self));
        List<PlayInterruptAction> bodyguard = super.getGameTextTopLevelActions(player, game, self);
        if (bodyguard != null) actions.addAll(bodyguard);
        return actions;
    }
}
