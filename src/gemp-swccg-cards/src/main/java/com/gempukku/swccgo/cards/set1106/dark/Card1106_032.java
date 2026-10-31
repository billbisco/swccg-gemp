package com.gempukku.swccgo.cards.set1106.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.PhaseCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePlacedOutOfPlayModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Effect
 * Title: Despair
 */
public class Card1106_032 extends AbstractNormalEffect {
    public Card1106_032() {
        super(Side.DARK, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Despair, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("The carbonite froze more than just Han's body.");
        setGameText("Deploy on table. During your battle phase, while you have a frozen captive, opponent's cards may not move. At same site as Jabba's Prize, opponent's cards are deploy +1 and your Force drains are +2. My Favorite Decoration may not be placed out of play. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter jabbasPrizeSite = Filters.sameSiteAs(self, SpotOverride.INCLUDE_CAPTIVE, Filters.Jabbas_Prize);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveModifier(self, Filters.and(Filters.opponents(self), Filters.onTable),
                new AndCondition(new PhaseCondition(Phase.BATTLE, playerId),
                        new UnlessCondition(new CantSpotCondition(self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.your(self), Filters.frozenCaptive))))));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle, Filters.weapon, Filters.device, Filters.Effect)), 1, jabbasPrizeSite));
        modifiers.add(new ForceDrainModifier(self, jabbasPrizeSite, 2, playerId));
        modifiers.add(new MayNotBePlacedOutOfPlayModifier(self, Filters.My_Favorite_Decoration));
        return modifiers;
    }
}
