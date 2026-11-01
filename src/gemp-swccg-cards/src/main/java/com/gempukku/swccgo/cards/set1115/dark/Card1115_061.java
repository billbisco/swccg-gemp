package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Alien
 * Title: Sebulba (V)
 */
public class Card1115_061 extends AbstractAlien {
    public Card1115_061() {
        super(Side.DARK, 2, 3, 3, 2, 5, "Sebulba", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Bad tempered Dug from Pixelito. He was about to turn Jar Jar into orange goo, until Anakin intervened.");
        setGameText("Adds 2 to power of any combat vehicle he pilots. Brisky Morning Munchen is canceled. Draws one battle destiny if unable to otherwise. Once per turn, if any podrace total > 0, may take your just drawn destiny into hand to cancel and redraw that destiny.");
        addIcons(Icon.TATOOINE, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.combat_vehicle));
        modifiers.add(new CancelsGameTextModifier(self, Filters.title(Title.Brisky_Morning_Munchen)));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        return modifiers;
    }
}
