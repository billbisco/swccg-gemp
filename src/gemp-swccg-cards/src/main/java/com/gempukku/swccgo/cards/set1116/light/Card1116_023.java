package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
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
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Droid
 * Title: Artoo, Brave Little Droid (V)
 */
public class Card1116_023 extends AbstractDroid {
    public Card1116_023() {
        super(Side.LIGHT, 6, 3, 2, 4, "Artoo, Brave Little Droid", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        addIcons(Icon.THEED_PALACE, Icon.NAV_COMPUTER);
        addModelType(ModelType.ASTROMECH);
        addPersona(Persona.R2D2);
        setGameText("May 'fly' (landspeed = 2). Once per game, may target up to two opponent's droids present; owner must place targets in Used Pile or lose 4 Force. While aboard an [Episode I] starship, it is power +3 and immune to attrition < 4.");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        AboardCondition aboard = new AboardCondition(self, Filters.and(Filters.starship, Icon.EPISODE_I));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 2));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.starship, Icon.EPISODE_I, Filters.hasAboard(self)), aboard, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.starship, Icon.EPISODE_I, Filters.hasAboard(self)), aboard, 4));
        return modifiers;
    }
}
