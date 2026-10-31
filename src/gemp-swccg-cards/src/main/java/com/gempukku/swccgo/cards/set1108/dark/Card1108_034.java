package com.gempukku.swccgo.cards.set1108.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
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
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Character
 * Subtype: Alien
 * Title: Gela Yeens
 */
public class Card1108_034 extends AbstractAlien {
    public Card1108_034() {
        super(Side.DARK, 1, 3, 2, 2, 3, Title.Gela_Yeens, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Bad-tempered smuggler. Makes a few credits more helping Jabba collect debts. Highly regarded for his uncanny anticipation in battle. Searching for Debnoli.");
        setGameText("Adds 3 to power of anything he pilots. Opponent may not target Gela Yeens (or a starship he pilots) with weapons. While in battle with Jabba, opponent's characters at same site are power and forfeit -2.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.SPECIAL_EDITION);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition inBattleWithJabba = new InBattleWithCondition(self, Filters.Jabba);
        Filter opponentCharactersHere = Filters.and(Filters.opponents(self), Filters.character, Filters.atSameSite(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.and(Filters.starship, Filters.hasPiloting(self))));
        modifiers.add(new PowerModifier(self, opponentCharactersHere, inBattleWithJabba, -2));
        modifiers.add(new ForfeitModifier(self, opponentCharactersHere, inBattleWithJabba, -2));
        return modifiers;
    }
}
