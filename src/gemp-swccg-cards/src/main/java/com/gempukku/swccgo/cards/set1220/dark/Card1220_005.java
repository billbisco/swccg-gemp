package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeAboardModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Lennox
 */
public class Card1220_005 extends AbstractImperial {
    public Card1220_005() {
        super(Side.DARK, 2, 2, 2, 2, 5, "Captain Lennox", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Captain of the Imperial Star Destroyer Tyrant. An able leader. Unlike most Imperial officers, he is dedicated to his ship and crew. Finds political maneuvering distasteful.");
        setGameText("Adds 3 to power of any Star Destroyer he pilots. Deploys free aboard Tyrant, and, while aboard, draws one battle destiny if not able to otherwise and your total power here is +1 for each of opponent's starfighters here.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.HOTH);
        addKeywords(Keyword.CAPTAIN);
        setMatchingStarshipFilter(Filters.Tyrant);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeAboardModifier(self, Filters.Tyrant));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.Star_Destroyer));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AboardCondition(self, Filters.Tyrant), 1));
        modifiers.add(new TotalPowerModifier(self, Filters.here(self), new AboardCondition(self, Filters.Tyrant),
                new PresentEvaluator(self, Filters.and(Filters.opponents(self), Filters.starfighter)), self.getOwner()));
        return modifiers;
    }
}
