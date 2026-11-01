package com.gempukku.swccgo.cards.set1117.dark;

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
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Effect
 * Title: TIE Fighter Construction Facility
 */
public class Card1117_021 extends AbstractNormalEffect {
    public Card1117_021() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "TIE Fighter Construction Facility", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("");
        setGameText("Deploy on table. TIE/ads and restricted (•••) TIEs are deploy = 2, forfeit = 4, and immune to attrition < 4. Ignore \"unique (•)\" in Watch Your Back's game text. Attrition against opponent is +3 where your capital starship present with a non-squadron TIE. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter ties = Filters.and(Filters.your(self), Filters.or(ModelType.TIE_AD, Filters.and(Filters.TIE, Filters.restricted)));
        Filter withCapitalAndTie = Filters.and(
                Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.capital_starship)),
                Filters.sameLocationAs(self, Filters.and(Filters.TIE, Filters.not(Filters.squadron))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetDeployCostModifier(self, ties, 2));
        modifiers.add(new ResetForfeitModifier(self, ties, 4));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, ties, 4));
        modifiers.add(new AttritionModifier(self, withCapitalAndTie, 3, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
