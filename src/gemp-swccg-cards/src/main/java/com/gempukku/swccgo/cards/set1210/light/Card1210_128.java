package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.CanAddDestinyToPowerCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Jawa Siesta
 */
public class Card1210_128 extends AbstractNormalEffect {
    public Card1210_128() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Jawa_Siesta, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Three Jawas take a cool, energy-saving nap in the noon-day suns at Mos Eisley.");
        setGameText("Deploy on table. Your unique (•) Jawas are defense value +1. During battles at exterior Tatooine sites where your unique (•) Jawa is participating, may add one destiny to your total power.");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourUniqueJawas = Filters.and(Filters.your(self), Filters.unique, Filters.Jawa);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, yourUniqueJawas, 1));
        modifiers.add(new AddsDestinyToPowerModifier(self,
                new AndCondition(
                        new DuringBattleAtCondition(Filters.exterior_Tatooine_site),
                        new InBattleCondition(self, yourUniqueJawas),
                        new CanAddDestinyToPowerCondition(playerId)),
                1, playerId));
        return modifiers;
    }
}
