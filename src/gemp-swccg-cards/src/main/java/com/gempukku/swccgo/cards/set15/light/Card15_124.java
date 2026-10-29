package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set8.light.Card8_063;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_124 extends Card8_063 {
    public Card15_124() {
        setCardSubtype(CardSubtype.USED);
        setAlternateDestiny(5);
        setLore("");
        setGameText("If your admiral is in battle at a system (or your general, except Obi-Wan, is in battle at a site), you may either add one battle destiny or prevent opponent from drawing more than one battle destiny. OR Take one automated weapon into hand from Reserve Deck; reshuffle. OR Add 1 to Deactivate The Shield Generator total. OR Cancel Trooper Charge, Blasted Droid, Trooper Assault, or Dark Jedi Presence.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("Rebel Leadership", Uniqueness.UNIQUE), new TitleComponent("Throw Me Another Charge", Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "Rebel Leadership & Throw Me Another Charge"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }


    private static final LeadershipActions LEADERSHIP = new LeadershipActions();
    private static final class LeadershipActions extends com.gempukku.swccgo.cards.set9.light.Card9_054 {
        @Override protected boolean includesForfeitBonus() { return false; }
        List<PlayInterruptAction> actions(String player, SwccgGame game, PhysicalCard self) {
            return super.getGameTextTopLevelActions(player, game, self);
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String player, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>(super.getGameTextTopLevelActions(player, game, self));
        actions.addAll(LEADERSHIP.actions(player, game, self));
        return actions;
    }
}
