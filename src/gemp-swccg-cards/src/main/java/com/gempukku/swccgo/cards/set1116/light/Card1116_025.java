package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Starship
 * Subtype: Starfighter
 * Title: BoShek's Modified Light Freighter
 */
public class Card1116_025 extends AbstractStarfighter {
    public Card1116_025() {
        super(Side.LIGHT, 3, 3, 3, null, 3, 4, 4, "BoShek's Modified Light Freighter", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("May add 2 pilots. While BoShek piloting: maneuver and hyperspeed +2; immune to attrition < 4; and if at a battleground system, may add 1 to Force drains here.");
        addIcons(Icon.SPECIAL_EDITION, Icon.INDEPENDENT, Icon.NAV_COMPUTER);
        addModelType(ModelType.MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(2);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        HasPilotingCondition boshek = new HasPilotingCondition(self, Filters.persona(Persona.BOSHEK));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, self, boshek, 2));
        modifiers.add(new HyperspeedModifier(self, self, boshek, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, self, boshek, 4));
        return modifiers;
    }
}
