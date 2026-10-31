package com.gempukku.swccgo.cards.set1107.light;

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
 * Set: Virtual Set 7: Shadows Of The Empire
 * Type: Character
 * Subtype: Alien
 * Title: Tawss Khaa
 */
public class Card1107_010 extends AbstractAlien {
    public Card1107_010() {
        super(Side.LIGHT, 2, 3, 3, 4, 4, Title.Tawss_Khaa, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_7, Rarity.V);
        setVirtualSuffix(true);
        setLore("Female Nimbanel fortune teller. Roams the wastes of Tatooine. Trained in the ways of a Sakiyan hunter. Now tracks them for the Alliance. Risk-taker.");
        setGameText("While at a battleground planet site, your Force drains and Force drain bonuses here and at same site as Sergeant Doallyn may not be canceled. Immune to attrition < 3.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition atBattlegroundPlanetSite = new AtCondition(self, Filters.and(Filters.battleground, Filters.planet_site));
        Filter locations = Filters.or(Filters.here(self), Filters.sameSiteAs(self, Filters.Doallyn));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, locations, atBattlegroundPlanetSite, null, playerId));
        modifiers.add(new ForceDrainBonusesMayNotBeCanceledModifier(self, atBattlegroundPlanetSite, Filters.your(self), locations));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
