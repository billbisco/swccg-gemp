package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AboardAsPassengerCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Imperial
 * Title: Sandtrooper
 */
public class Card1115_058 extends AbstractImperial {
    public Card1115_058() {
        super(Side.DARK, 3, 2, 2, 1, 3, "Sandtrooper", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Equipped for harsh, high-temperature environments. Primarily assigned to search-and-destroy missions, but sometimes beaks up local disputes or recover stolen droids.");
        setGameText("Deploys only to Tatooine or any desert. Immune to Sandwhirl. Your desert locations are destiny = 4. While 'riding' a dewback, power +3 and may not be targeted by lightsabers.");
        addIcons(Icon.WARRIOR, Icon.SPECIAL_EDITION);
        addKeywords(Keyword.SANDTROOPER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Deploys_at_Tatooine, Filters.locationAndCardsAtLocation(Filters.desert));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition ridingDewback = new AboardAsPassengerCondition(self, Filters.Dewback);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Sandwhirl));
        modifiers.add(new ResetDestinyModifier(self, Filters.and(Filters.your(self), Filters.desert), 4));
        modifiers.add(new PowerModifier(self, ridingDewback, 3));
        modifiers.add(new MayNotBeTargetedByModifier(self, ridingDewback, Filters.lightsaber));
        return modifiers;
    }
}
