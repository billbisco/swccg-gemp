package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresDeploymentRestrictionsFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Effect
 * Title: At Last We Are Getting Results
 */
public class Card1112_023 extends AbstractNormalEffect {
    public Card1112_023() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "At Last We Are Getting Results", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("First sign that your blockade is going well: the Queen's on the comlink.");
        setGameText("If Invasion on table, deploy on table. Maul ignores objective deployment restrictions. Your [Presence] droids are defense value +2. Your starships are deploy -1. Immunity to attrition of opponent's cards is -3. Immune to Alter.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.Invasion);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IgnoresDeploymentRestrictionsFromCardModifier(self, Filters.Maul, null, self.getOwner(), Filters.Objective));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.your(self), Filters.droid, Icon.PRESENCE), 2));
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.your(self), Filters.starship), -1));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.opponents(self), -3));
        return modifiers;
    }
}
