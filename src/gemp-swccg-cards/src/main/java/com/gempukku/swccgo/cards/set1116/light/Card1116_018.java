package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Alien
 * Title: Wookiee (V)
 */
public class Card1116_018 extends AbstractAlien {
    public Card1116_018() {
        super(Side.LIGHT, 2, 4, 4, 2, 4, "Wookiee", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setSpecies(Species.WOOKIEE);
        setGameText("Deploys -1 to Kashyyyk. May move as a 'react.' During battle, if he fires a bowcaster, adds one destiny to your total power. If about to be lost from table, may place any cards on him in owner's Used Pile and may draw top card from Reserve Deck.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Kashyyyk_site));
        modifiers.add(new MayMoveAsReactModifier(self));
        return modifiers;
    }
}
