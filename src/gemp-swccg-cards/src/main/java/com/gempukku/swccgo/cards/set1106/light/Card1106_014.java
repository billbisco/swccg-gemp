package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Effect
 * Title: Leia Of Alderaan
 */
public class Card1106_014 extends AbstractNormalEffect {
    public Card1106_014() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Leia_Of_Alderaan, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("The face that launched a thousand starships.");
        setGameText("Deploy on Leia. At same location, your unique (•) Rebels are deploy -1 and their immunity to attrition is +1. Dark side Republic characters are deploy +2 and forfeit -2. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Leia;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.Leia;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourUniqueRebels = Filters.and(Filters.your(self), Filters.unique, Filters.Rebel);
        Filter darkRepublicCharacters = Filters.and(Filters.Republic_character, Filters.owner(game.getDarkPlayer()));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourUniqueRebels, -1, Filters.sameLocation(self)));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(yourUniqueRebels, Filters.atSameLocation(self)), 1));
        modifiers.add(new DeployCostModifier(self, darkRepublicCharacters, 2));
        modifiers.add(new ForfeitModifier(self, darkRepublicCharacters, -2));
        return modifiers;
    }
}
