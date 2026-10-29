package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set1.light.Card1_101;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_117 extends Card1_101 {
    public Card15_117() {
        setCardSubtype(CardSubtype.LOST);
        setAlternateDestiny(3);
        setLore("");
        setGameText("Use 1 Force and target a Rebel of ability > 2. Choose a number from 1 to 6. You may retrieve that amount of Force if you now draw destiny > chosen number. If you fail, Rebel is lost. OR Use 1 Force to cancel an opponent's Force drain at a location. Draw one destiny for each character, starship, and vehicle the opponent has present (limit 2). Compare your destiny total to opponent's power total. Player with lowest total loses Force equal to the difference.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("On The Edge", Uniqueness.UNRESTRICTED), new TitleComponent("Surprise Assault", Uniqueness.UNRESTRICTED));
    }
    @Override public String getTitle() { return "On The Edge & Surprise Assault"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNRESTRICTED; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }


    private static final AssaultActions ASSAULT = new AssaultActions();
    private static final class AssaultActions extends com.gempukku.swccgo.cards.set1.light.Card1_113 {
        @Override protected int getDestinyDrawCount(int eligibleCards) { return Math.min(2, eligibleCards); }
        List<PlayInterruptAction> actions(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
            if (!com.gempukku.swccgo.logic.TriggerConditions.forceDrainInitiatedBy(game, result, game.getOpponent(player))) return null;
            return super.getGameTextOptionalAfterActions(player, game, result, self);
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
        return ASSAULT.actions(player, game, result, self);
    }
}
