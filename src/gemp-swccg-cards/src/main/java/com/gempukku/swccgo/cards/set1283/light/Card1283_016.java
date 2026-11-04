package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DrivingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToDrivenBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Character
 * Subtype: Alien
 * Title: Palace Raider (V)
 */
public class Card1283_016 extends AbstractAlien {
    public Card1283_016() {
        super(Side.LIGHT, 2, 2, 1, 2, 3, Title.Palace_Raider, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Smugglers from many worlds are hunted by the Empire for providing arms and supplies to the Alliance. The Outer Rim is their refuge.");
        setGameText("Adds 2 to power of anything he pilots. Adds 3 to power of anything he drives. When driving a transport vehicle, it moves for free and he adds one battle destiny. [Reflections 3] Wipe Them Out, All Of Them ignores your non-unique aliens.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsPowerToDrivenBySelfModifier(self, 3));
        modifiers.add(new MovesForFreeModifier(self, Filters.and(Filters.transport_vehicle, Filters.hasDriving(self))));
        modifiers.add(new AddsBattleDestinyModifier(self, new DrivingCondition(self, Filters.transport_vehicle), 1));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Icon.REFLECTIONS_III, Filters.title("Wipe Them Out, All Of Them")),
                ModifyGameTextType.LEGACY__REF_III_WIPE_THEM_OUT_ALL_OF_THEM__IGNORES_YOUR_NONUNIQUE_ALIENS));
        return modifiers;
    }
}
