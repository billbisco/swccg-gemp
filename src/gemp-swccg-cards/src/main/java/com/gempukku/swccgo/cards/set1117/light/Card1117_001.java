package com.gempukku.swccgo.cards.set1117.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Effect
 * Title: A Jedi's Cunning
 */
public class Card1117_001 extends AbstractNormalEffect {
    public Card1117_001() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Jedi's Cunning", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("");
        setGameText("Deploy on table. While Sidious or an [Episode I] hologram on table, opponent may not cancel immunity to attrition of your Jedi and draws no more than two battle destiny at sites where you have a Jedi. Opponent draws no more than two battle destiny where Luke present. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter battlesWhereYourJediIsPresent = Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Jedi));
        OrCondition sidiousOrEpisodeOneHologram = new OrCondition(
                new OnTableCondition(self, Filters.Sidious),
                new OnTableCondition(self, Filters.and(Icon.EPISODE_I, Filters.hologram)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, battlesWhereYourJediIsPresent, sidiousOrEpisodeOneHologram, 2, opponent));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.sameLocationAs(self, Filters.Luke), 2, opponent));
        return modifiers;
    }
}
