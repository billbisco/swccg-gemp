package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
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
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueIncreasedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Imperial
 * Title: Sergeant Wallen
 */
public class Card1230_017 extends AbstractImperial {
    public Card1230_017() {
        super(Side.DARK, 3, 2, 2, 2, 3, Title.Sergeant_Wallen, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Stormtrooper assigned to Colonel Dyer's command. His unit was recommended to Commander Igar by Governor Yount of the Wakeelmui garrison.");
        setGameText("During battle here, your Imperial leaders may not be targeted by weapons. Your Imperials of ability < 4 at same site (except Galactic Senate) may not have their game text canceled or their forfeit value modified or reset.");
        addIcons(Icon.WARRIOR, Icon.ENDOR);
        addKeywords(Keyword.STORMTROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourAbilityLt4AtSameSiteExceptSenate = Filters.and(Filters.your(self), Filters.Imperial, Filters.abilityLessThan(4),
                Filters.at(Filters.and(Filters.sameSite(self), Filters.not(Filters.Galactic_Senate))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.and(Filters.your(self), Filters.Imperial_leader, Filters.here(self)),
                new DuringBattleAtCondition(Filters.here(self))));
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, yourAbilityLt4AtSameSiteExceptSenate));
        modifiers.add(new MayNotHaveForfeitValueReducedModifier(self, yourAbilityLt4AtSameSiteExceptSenate));
        modifiers.add(new MayNotHaveForfeitValueIncreasedModifier(self, yourAbilityLt4AtSameSiteExceptSenate));
        return modifiers;
    }
}
