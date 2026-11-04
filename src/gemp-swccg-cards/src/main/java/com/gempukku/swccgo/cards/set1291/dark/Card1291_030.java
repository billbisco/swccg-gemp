package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeAboardModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Starship
 * Subtype: Starfighter
 * Title: Dooku's Solar Sailer
 */
public class Card1291_030 extends AbstractStarfighter {
    public Card1291_030() {
        super(Side.DARK, 3, 3, 3, null, 4, 6, 5, "Dooku's Solar Sailer", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setGameText("May add 1 pilot and 2 passengers. Dooku deploys free aboard. While Dooku aboard, immune to attrition and opponent must have at least two piloted starships to initiate battle at same system.");
        addIcons(Icon.TRADE_FEDERATION, Icon.SEPARATIST, Icon.PILOT, Icon.NAV_COMPUTER, Icon.EPISODE_I, Icon.PRESENCE);
        addModelType(ModelType.PUNWORCCA_116_CLASS_SLOOP);
        setPilotCapacity(1);
        setPassengerCapacity(2);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition dookuAboard = new HasAboardCondition(self, Filters.Dooku);
        Condition opponentLacksTwoPilotedStarships = new NotCondition(new HereCondition(self, 2,
                Filters.and(Filters.owner(opponent), Filters.piloted, Filters.starship)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeAboardModifier(self, Filters.Dooku, self));
        modifiers.add(new ImmuneToAttritionModifier(self, dookuAboard));
        modifiers.add(new MayNotInitiateBattleAtLocationModifier(self, Filters.and(Filters.system, Filters.here(self)),
                new AndCondition(dookuAboard, opponentLacksTwoPilotedStarships), opponent));
        return modifiers;
    }
}
