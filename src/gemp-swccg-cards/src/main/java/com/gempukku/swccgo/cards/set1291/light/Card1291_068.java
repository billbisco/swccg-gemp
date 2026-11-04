package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Starship
 * Subtype: Capital
 * Title: Acclamator-Class Assault Ship
 */
public class Card1291_068 extends AbstractCapitalStarship {
    public Card1291_068() {
        super(Side.LIGHT, 2, 5, 6, 5, null, 4, 6, "Acclamator-Class Assault Ship", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setGameText("May add 4 pilots, 4 passengers, and 4 vehicles. Adds 1 to attrition against opponent for each piloted [Republic] starship here. Permanent pilot provides ability of 2. Concussions Missiles may deploy aboard.");
        addModelType(ModelType.ACCLAMATOR_CLASS_ASSAULT_SHIP);
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.CLONE_ARMY, Icon.REPUBLIC, Icon.EPISODE_I);
        setPilotCapacity(4);
        setPassengerCapacity(4);
        setVehicleCapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // The attrition is added in battles here (the printed text counts starships "here").
        modifiers.add(new AttritionModifier(self, Filters.here(self), new HereEvaluator(self, Filters.and(Filters.piloted, Icon.REPUBLIC, Filters.starship)), opponent));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.Concussion_Missiles), self));
        return modifiers;
    }
}
