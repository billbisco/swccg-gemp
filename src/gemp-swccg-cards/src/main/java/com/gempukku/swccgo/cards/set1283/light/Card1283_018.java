package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Character
 * Subtype: Alien
 * Title: Trooper Utris M'Toc (V)
 */
public class Card1283_018 extends AbstractAlien {
    public Card1283_018() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Trooper Utris M'Toc", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imzig Cloud City trooper and Wing Guard leader. Attempts to protect Cloud City citizens from less reputable members of local constabulary. Never accepts a bribe.");
        setGameText("Deploys free to same site as Lando or Lobot. Power +2 while defending a battle. While present at a battleground, your aliens at same and related locations are forfeit +1 (+2 if a trooper).");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.CLOUD_CITY_TROOPER, Keyword.LEADER, Keyword.GUARD);
        setSpecies(Species.IMZIG);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.sameSiteAs(self, Filters.or(Filters.Lando, Filters.Lobot))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition presentAtBattleground = new PresentAtCondition(self, Filters.battleground);
        Filter yourAliensHere = Filters.and(Filters.your(self), Filters.alien, Filters.atSameOrRelatedLocation(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new DefendingBattleCondition(self), 2));
        modifiers.add(new ForfeitModifier(self, Filters.and(yourAliensHere, Filters.not(Filters.trooper)), presentAtBattleground, 1));
        modifiers.add(new ForfeitModifier(self, Filters.and(yourAliensHere, Filters.trooper), presentAtBattleground, 2));
        return modifiers;
    }
}
