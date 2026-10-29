package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set11.light.Card11_039;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.filters.Filters;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_121 extends Card11_039 {
    public Card15_121() {
        setCardSubtype(CardSubtype.LOST_OR_STARTING);
        setAlternateDestiny(3);
        setLore("");
        setGameText("LOST: Cancel Hit Racer or Podracer Collision. STARTING: Deploy Podrace Arena (with a Podracer, opponent may also deploy a Podracer there), Boonta Eve Podrace, and any Effect that deploys for free. Place Interrupt in Reserve Deck.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        setTitleComponents(new TitleComponent("Podrace Prep", Uniqueness.UNIQUE), new TitleComponent("Too Close For Comfort", Uniqueness.UNIQUE));
    }
    @Override public String getTitle() { return "Podrace Prep & Too Close For Comfort"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }


    private static final ComfortActions COMFORT = new ComfortActions();
    private static final class ComfortActions extends com.gempukku.swccgo.cards.set11.light.Card11_040 {
        List<PlayInterruptAction> before(String player, SwccgGame game, Effect effect, PhysicalCard self) {
            if (!TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Hit_Racer, Filters.Podracer_Collision))) return null;
            return super.getGameTextOptionalBeforeActions(player, game, effect, self);
        }
        List<PlayInterruptAction> actions(String player, SwccgGame game, PhysicalCard self) {
            return super.getGameTextTopLevelActions(player, game, self);
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String player, SwccgGame game, PhysicalCard self) {
        return COMFORT.actions(player, game, self);
    }
    @Override protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String player, SwccgGame game, Effect effect, PhysicalCard self) {
        return COMFORT.before(player, game, effect, self);
    }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
        return null; // The remaster does not include Podrace Prep's hand-for-race-destiny mode.
    }
}
