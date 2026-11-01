package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireRepeatedlyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Alien
 * Title: Scout Mercenary (V)
 */
public class Card1115_059 extends AbstractAlien {
    public Card1115_059() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Scout Mercenary", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Opportunists work with corrupt governments and Raithal casino operators. Keep the Empire informed of local Alliance activity, for a price.");
        setGameText("Adds 2 to power of anything she pilots (3 if a speeder bike). During battle, unless Gall on table, may use 1 Force to fire a DH-17 blaster twice (for free), separately or combined. While piloting a vehicle, she may use its defense value and may not go missing (except from your card).");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition battleUnlessGall = new AndCondition(new DuringBattleCondition(),
                new NotCondition(new OnTableCondition(self, Filters.title("Gall"))));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1, Filters.speeder_bike));
        modifiers.add(new FiresForFreeModifier(self, Filters.titleContains("DH-17"), battleUnlessGall));
        modifiers.add(new MayFireRepeatedlyModifier(self, Filters.titleContains("DH-17"), battleUnlessGall, 1));
        return modifiers;
    }
}
