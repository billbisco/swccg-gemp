package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 6.1 Captain Mod Terrik. */
public class Card1261_007 extends AbstractImperial {
    public Card1261_007() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Captain Mod Terrik",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setArmor(4);
        setLore("Leader. Captain Mod Terrik, a typical officer of the Desert Sands sandtrooper unit. Fearless and highly motivated. Willing to sacrifice as many troops as necessary.");
        setGameText("Other sandtroopers are deploy -1. Your ability-1 troopers may not have their forfeit reduced and (unless they have another forfeit bonus) are forfeit +2. At sites where you have two troopers, they may draw one battle destiny if not able to otherwise.");
        addIcons(Icon.WARRIOR, Icon.A_NEW_HOPE);
        addKeywords(Keyword.SANDTROOPER, Keyword.LEADER, Keyword.CAPTAIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter yourTroopers = Filters.and(Filters.your(self), Filters.trooper);
        Filter troopersAtBattleSite = Filters.and(yourTroopers, Filters.participatingInBattle,
                Filters.presentAt(Filters.and(Filters.site, Filters.battleLocation)));
        Filter yourAbilityOneTroopers = Filters.and(yourTroopers, Filters.abilityEqualTo(1));
        Modifier forfeitBonus = new ForfeitModifier(self, yourAbilityOneTroopers, 2);
        forfeitBonus.setOnlyIfNoOtherBonus(true);
        return List.of(
                new DeployCostModifier(self, Filters.and(Filters.other(self), Filters.sandtrooper), -1),
                new MayNotHaveForfeitValueReducedModifier(self, yourAbilityOneTroopers),
                forfeitBonus,
                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, troopersAtBattleSite,
                        (state, modifiers) -> Filters.countActive(state.getGame(), self, troopersAtBattleSite) >= 2, 1));
    }
}
