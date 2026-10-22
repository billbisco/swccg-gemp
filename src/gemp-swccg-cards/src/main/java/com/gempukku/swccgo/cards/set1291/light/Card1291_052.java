package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Effect
 * Title: Much to Learn, You Still Have
 */
public class Card1291_052 extends AbstractNormalEffect {
    public Card1291_052() {
        super(Side.LIGHT, 2, PlayCardZoneOption.ATTACHED, "Much to Learn, You Still Have", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("Yoda calls upon the Force often to try and foresee what destiny has in store.");
        setGameText("Deploy on any location. While on a battleground, adds one [Light Side Force] icon and one [Dark Side Force] icon. While on opponents non-battleground planet location (except a Kashyyyk location or docking bay), their gametext here is canceled and your Force Generation is +1. [Clone Army Icon] troopers are power + 1.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.location;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter opponentNonBattlegroundPlanet = Filters.and(Filters.opponents(self), Filters.planet_location,
                Filters.not(Filters.battleground), Filters.not(Filters.docking_bay), Filters.not(Filters.partOfSystem("Kashyyyk")));
        AtCondition onBattleground = new AtCondition(self, Filters.battleground);
        AtCondition onOpponentNonBattleground = new AtCondition(self, opponentNonBattlegroundPlanet);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, Filters.sameLocation(self), onBattleground, Icon.LIGHT_FORCE, 1));
        modifiers.add(new IconModifier(self, Filters.sameLocation(self), onBattleground, Icon.DARK_FORCE, 1));
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.sameLocation(self), onOpponentNonBattleground, opponent));
        modifiers.add(new ForceGenerationModifier(self, Filters.sameLocation(self), onOpponentNonBattleground, 1, playerId));
        modifiers.add(new PowerModifier(self, Filters.and(Icon.CLONE_ARMY, Filters.trooper), 1));
        return modifiers;
    }
}
