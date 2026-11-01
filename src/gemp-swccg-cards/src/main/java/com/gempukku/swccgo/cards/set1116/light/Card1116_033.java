package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractCharacter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Title: Jedi Survivor
 */
public class Card1116_033 extends AbstractCharacter {
    public Card1116_033() {
        super(Side.LIGHT, 3f, 5f, 5f, 6, 5f, "Jedi Survivor", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("When deployed, you may not initiate battle here this turn. While at opponent's site, adds one [Light] and one [Dark]. May 'swing' lightsabers for free. May never be won in sabacc. Immune to attrition < 5.");
        addIcons(Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        AtCondition atOpponentsSite = new AtCondition(self, Filters.and(Filters.site, Filters.opponents(self)));
        modifiers.add(new IconModifier(self, Filters.here(self), atOpponentsSite, Icon.LIGHT_FORCE));
        modifiers.add(new IconModifier(self, Filters.here(self), atOpponentsSite, Icon.DARK_FORCE));
        return modifiers;
    }
}
