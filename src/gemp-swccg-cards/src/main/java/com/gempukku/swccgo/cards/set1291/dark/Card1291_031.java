package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotSeatOccupiedCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendPermanentPilotModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Starship
 * Subtype: Starfighter
 * Title: Fanblade Starfighter
 */
public class Card1291_031 extends AbstractStarfighter {
    public Card1291_031() {
        super(Side.DARK, 3, 3, 3, null, 5, 3, 3, "Fanblade Starfighter", Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setGameText("May add 1 pilot (suspends permanent pilot) and 1 passenger. Permanent pilot provides ability of 2. Forfeit +2 while with a [Clone Army] card. Unless defending, power +2 and adds one battle destiny.");
        addIcons(Icon.NAV_COMPUTER, Icon.PILOT, Icon.SEPARATIST, Icon.EPISODE_I);
        addModelType(ModelType.GINIVEX_CLASS_STARFIGHTER);
        setPilotCapacity(1);
        setPassengerCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendPermanentPilotModifier(self, new HasPilotSeatOccupiedCondition(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition unlessDefending = new NotCondition(new DefendingBattleCondition(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, new WithCondition(self, Icon.CLONE_ARMY), 2));
        modifiers.add(new PowerModifier(self, unlessDefending, 2));
        modifiers.add(new AddsBattleDestinyModifier(self, unlessDefending, 1));
        return modifiers;
    }
}
