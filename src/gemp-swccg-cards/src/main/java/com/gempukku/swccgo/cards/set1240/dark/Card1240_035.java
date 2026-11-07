package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresUniqueInGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;

import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: TIE Fighter Construction Facility
 */
public class Card1240_035 extends AbstractNormalEffect {
    public Card1240_035() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "TIE Fighter Construction Facility",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setGameText("Deploy on table. TIE/ads and restricted (•••) TIEs are deploy = 2, forfeit = 4, and immune to attrition < 4. Ignore 'unique (•)' in Watch Your Back's game text. Attrition against opponent is +3 where your capital starship present with a non-squadron TIE. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter ties = Filters.or(ModelType.TIE_AD, Filters.and(Filters.TIE, Uniqueness.RESTRICTED_3));
        Filter nonSquadronTie = Filters.and(Filters.TIE, Filters.not(Filters.squadron));
        Filter ownCapitalWithTie = Filters.and(Filters.your(self), Filters.capital_starship,
                Filters.presentWith(self, nonSquadronTie));
        Filter battleLocations = Filters.wherePresent(self, ownCapitalWithTie);

        return List.of(new ResetDeployCostModifier(self, ties, 2),
                new ResetForfeitModifier(self, ties, 4),
                new ImmuneToAttritionLessThanModifier(self, ties, 4),
                new IgnoresUniqueInGameTextModifier(self, Filters.title(Title.Watch_Your_Back)),
                new AttritionModifier(self, battleLocations, 3, game.getOpponent(self.getOwner())));
    }
}
