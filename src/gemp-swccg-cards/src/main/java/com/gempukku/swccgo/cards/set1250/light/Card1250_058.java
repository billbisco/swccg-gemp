package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
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
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 5 A Jedi's Cunning, printing 58. */
public class Card1250_058 extends AbstractNormalEffect {
    public Card1250_058() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Jedi's Cunning", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setLore("Luke's sudden reversal of fortune allowed him to turn the tables on Vader.");
        setGameText("Deploy on table. While Sidious or an [Episode I] hologram on table, opponent may not cancel immunity to attrition of your Jedi and draws no more than two battle destiny at sites where you have a Jedi. Opponent draws no more than two battle destiny where Luke present. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        var siteWithYourJedi = Filters.and(Filters.site, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Jedi)));
        var sidiousOrHologram = new OrCondition(new OnTableCondition(self, Filters.Sidious),
                new OnTableCondition(self, Filters.and(Icon.EPISODE_I, Filters.hologram)));
        return List.of(new ImmunityToAttritionMayNotBeCanceledModifier(self, Filters.and(Filters.your(self), Filters.Jedi), sidiousOrHologram, opponent),
                new MayNotDrawMoreThanBattleDestinyModifier(self, siteWithYourJedi, sidiousOrHologram, 2, opponent),
                new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.wherePresent(self, Filters.Luke), 2, opponent));
    }
}
