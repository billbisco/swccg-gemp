package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainBonusesMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Alien
 * Title: Bane Malar
 */
public class Card1230_001 extends AbstractAlien {
    public Card1230_001() {
        super(Side.DARK, 1, 3, 2, 4, 4, Title.Bane_Malar, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Mysterious bounty hunter of an unknown species. Rumored to be somewhat telepathic. Infrequent member of Jabba's court. Plotting to kill Jabba.");
        setGameText("Blaster Rifle is a matching weapon for Bane. While at a battleground planet site, your Force drains and Force drain bonuses here and at same site as Ree-Yees may not be canceled. Immune to attrition < 3.");
        addIcons(Icon.WARRIOR, Icon.JABBAS_PALACE);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setMatchingWeaponFilter(Filters.blaster_rifle);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition atBattlegroundPlanetSite = new AtCondition(self, Filters.and(Filters.battleground, Filters.planet_site));
        Filter locations = Filters.or(Filters.here(self), Filters.sameSiteAs(self, Filters.title(Title.ReeYees)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, locations, atBattlegroundPlanetSite, playerId));
        modifiers.add(new ForceDrainBonusesMayNotBeCanceledModifier(self, atBattlegroundPlanetSite, Filters.your(self), locations));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
