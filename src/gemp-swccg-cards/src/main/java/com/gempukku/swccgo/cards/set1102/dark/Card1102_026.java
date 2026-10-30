package com.gempukku.swccgo.cards.set1102.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Character
 * Subtype: Imperial
 * Title: Admiral Motti (V)
 */
public class Card1102_026 extends AbstractImperial {
    public Card1102_026() {
        super(Side.DARK, 1, 3, 3, 3, 5, "Admiral Motti", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Senior Navy Commander of Death Star. Believes in technology. Ridiculed the Force. Ambitious leader. Promoted due to support of New Order, not military skills. Hates Vader.");
        setGameText("Adds 2 to power of anything he pilots. Imperial-class Star Destroyers are deploy -2 to same system.");
        addPersona(Persona.MOTTI);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.ADMIRAL, Keyword.COMMANDER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Imperial_class_Star_Destroyer, -2, Filters.sameSystemAs(self, Filters.sameCardId(self))));
        return modifiers;
    }
}
