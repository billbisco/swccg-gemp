package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Tamizander Rey
 */
public class Card1104_018 extends AbstractRebel {
    public Card1104_018() {
        super(Side.LIGHT, 2, 2, 1, 2, 3, "Tamizander Rey", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("A native of Esseles. Resigned from Esselian defense force after the Imperial Senate was disbanded. Senior deck officer of Echo Base responsible for docking bay operations.");
        setGameText("Adds 2 to power of anything he pilots. While at a docking bay, power and defense value +2. Tamizander Rey's docking bay transit requires -3 Force.");
        addIcons(Icon.PILOT, Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atDockingBay = new AtCondition(self, Filters.docking_bay);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, atDockingBay, 2));
        modifiers.add(new DefenseValueModifier(self, atDockingBay, 2));
        modifiers.add(new DockingBayTransitCostModifier(self, -3));
        return modifiers;
    }
}
