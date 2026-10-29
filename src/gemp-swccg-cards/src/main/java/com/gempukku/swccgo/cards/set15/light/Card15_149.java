package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set12.light.Card12_055;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_149 extends Card12_055 {
    public Card15_149() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(4);
        setLore("");
        setGameText("USED: Take Radiant VII, or one Interrupt with the word \"Podracer(s)\" in its game text, into hand from Reserve Deck; reshuffle. OR Use 3 Force to take one Effect of any kind into hand from your Reserve Deck; reshuffle. OR Target your Episode I Jedi defending a battle. Target is immune to attrition for rest of turn (unless a Dark Jedi present). LOST: In a battle you lost, place a Jedi Council Member you just forfeited into Used Pile to cancel all battle damage and attrition against you.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        setTitleComponents(new TitleComponent("We Wish To Board At Once", Uniqueness.UNIQUE), new TitleComponent("Are You Brain Dead?!", Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "We Wish To Board At Once & Are You Brain Dead?!"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }


    private static final ComponentActions COMPONENT = new ComponentActions();
    private static final class ComponentActions extends com.gempukku.swccgo.cards.set12.light.Card12_072 {
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
