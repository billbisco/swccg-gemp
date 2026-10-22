package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Commander Evram Lajaie
 */
public class Card1210_097 extends AbstractRebel {
    public Card1210_097() {
        super(Side.LIGHT, 2, 2, 2, 2, 5, "Commander Evram Lajaie", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Popular leader whose expertise in space defense and orbital battle stations enabled the Alliance to uncover a fatal flaw in the Death Star's design.");
        setGameText("While at your War Room, forfeit +3. Your starships with hyperspeed have a [Nav Computer]. Your piloted starships are forfeit +1.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, new AtCondition(self, Filters.and(Filters.your(self), Filters.war_room)), 3));
        modifiers.add(new IconModifier(self, Filters.and(Filters.your(self), Filters.starship, Filters.hasHyperdrive), Icon.NAV_COMPUTER));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.starship, Filters.piloted), 1));
        return modifiers;
    }
}
