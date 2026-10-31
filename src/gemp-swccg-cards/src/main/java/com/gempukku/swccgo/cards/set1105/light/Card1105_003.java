package com.gempukku.swccgo.cards.set1105.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Character
 * Subtype: Alien
 * Title: Bron Burs
 */
public class Card1105_003 extends AbstractAlien {
    public Card1105_003() {
        super(Side.LIGHT, 3, 2, 2, 3, 4, "Bron Burs", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Ugly scars cover this former commando from southern Nentan. Likes to be on his own. Crack shot. Off and on partner of Debnoli. Relies on intuition to survive.");
        setGameText("Draws one battle destiny if unable to otherwise. Where present, cancels a non-docking bay site's Dark side game text. While armed with a Blaster Rifle (which is a matching weapon for Bron), it fires for free and Bron is immune to attrition < 4.");
        addIcons(Icon.WARRIOR, Icon.SPECIAL_EDITION);
        setMatchingWeaponFilter(Filters.blaster_rifle);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.sameSite(self),
                new PresentAtCondition(self, Filters.and(Filters.site, Filters.not(Filters.docking_bay))), opponent));
        modifiers.add(new FiresForFreeModifier(self, Filters.and(Filters.blaster_rifle, Filters.attachedTo(self))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ArmedWithCondition(self, Filters.blaster_rifle), 4));
        return modifiers;
    }
}
