package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotSeatOccupiedCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.MayUseGetToYourShips;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendPermanentPilotModifier;

import java.util.List;

/** Legacy Block 6: Republic Starfighter. */
public class Card1263_033 extends AbstractStarfighter implements MayUseGetToYourShips {
    public Card1263_033() {
        super(Side.LIGHT, 2, 3, 3, null, 4, 5, 5, "Republic Starfighter",
                Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setGameText("Permanent pilot provides ability of 1. May add 1 pilot (suspends permanent pilot). Pilots deploy -1 aboard (-2 if a Jedi). May be revealed or deployed using Get To Your Ships!.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.REPUBLIC, Icon.SCOMP_LINK, Icon.DEATH_STAR_II, Icon.EPISODE_I);
        addModelType(ModelType.RECONNAISSANCE_FIGHTER);
        setPilotCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new SuspendPermanentPilotModifier(self, new HasPilotSeatOccupiedCondition(self)));
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return List.of(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new DeployCostToTargetModifier(self, Filters.pilot, null,
                new CardMatchesEvaluator(-1, -2, Filters.Jedi), self));
    }
}
