package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Effect
 * Title: Access Denied
 */
public class Card1106_001 extends AbstractNormalEffect {
    public Card1106_001() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Access_Denied, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("The doors on Cloud City use a special computer controlled locking system, making them difficult to bypass without altering the security codes.");
        setGameText("Deploy on table. At Cloud City sites, Stunning Leader may not be played and your non-lightsaber Rebels of ability > 3 are immune to attrition < 4. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Cloud_City_site, Title.Stunning_Leader));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self,
                Filters.and(Filters.your(self), Filters.Rebel, Filters.abilityMoreThan(3), Filters.not(Filters.lightsaber), Filters.at(Filters.Cloud_City_site)), 4));
        return modifiers;
    }
}
