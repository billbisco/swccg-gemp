package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.cards.set4.light.Card4_053;

import com.gempukku.swccgo.cards.set4.light.Card4_055;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.logic.timing.Effect;
import java.util.ArrayList;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;


/**
 * Set: Reflections Gold
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Light Maneuvers & Jedi Levitation
 */
public class Card15_097 extends Card4_055 {
    public Card15_097() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setLore("");
        setAlternateDestiny(5);
        addIcon(Icon.DAGOBAH, 0);
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Light Maneuvers", "Jedi Levitation");
        setGameText("USED: Cancel Dark Maneuvers, Emergency Deployment, Lone Pilot, I Want That Ship, Defensive Fire, Rogue Asteroid, or Take Evasive Action. OR Double Special Modifications until end of turn. LOST: Use X Force, where X = (7 - ability of your highest ability character on table). Search through your Used Pile and take one card into hand. Shuffle, Cut and replace.");
    }
    @Override public String getTitle() { return "Light Maneuvers & Jedi Levitation"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = super.getGameTextOptionalBeforeActions(playerId, game, effect, self);
        if (actions != null) actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        List<PlayInterruptAction> used = super.getGameTextTopLevelActions(playerId, game, self);
        if (used != null) {
            used.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
            actions.addAll(used);
        }
        List<PlayInterruptAction> second = SECOND.actions(playerId, game, self);
        if (second != null) {
            second.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST));
            actions.addAll(second);
        }
        return actions;
    }
    private static final JediLevitationActions SECOND = new JediLevitationActions();

    // Expose the unchanged component hook while retaining this combination as its source.
    private static final class JediLevitationActions extends Card4_053 {
        List<PlayInterruptAction> actions(String playerId, SwccgGame game, PhysicalCard self) {
            return super.getGameTextTopLevelActions(playerId, game, self);
        }
    }
}
