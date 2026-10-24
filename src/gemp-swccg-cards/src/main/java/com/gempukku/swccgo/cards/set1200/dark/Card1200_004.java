package com.gempukku.swccgo.cards.set1200.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.OccupiesMoreThanOpponentCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceRetrievalFromCardMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Shields
 * Type: Defensive Shield
 * Title: Reactor Terminal (V)
 */
public class Card1200_004 extends AbstractDefensiveShield {
    public Card1200_004() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Reactor Terminal", ExpansionSet.VIRTUAL_SHIELDS, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Death Star has many terminals coupled to the main reactor for power distribution throughout the immense space station.");
        setGameText("Plays on table. 'Three' on Fear Is My Ally is treated as 'Four'. You lose no Force to Boonta Eve Podrace. Opponent's retrieval from Boonta Eve Podrace may not be canceled. While you occupy more battlegrounds than opponent, I Did It! is suspended.");
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNRESTRICTED;
    }

    @Override
    public Float getDestiny() {
        return 3f;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.title(Title.Fear_Is_My_Ally), ModifyGameTextType.LEGACY__FEAR_IS_MY_ALLY__THREE_IS_FOUR));
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.Boonta_Eve_Podrace, playerId));
        modifiers.add(new ForceRetrievalFromCardMayNotBeCanceledModifier(self, Filters.Boonta_Eve_Podrace, opponent));
        modifiers.add(new SuspendsCardModifier(self, Filters.I_Did_It, new OccupiesMoreThanOpponentCondition(playerId, Filters.battleground)));
        return modifiers;
    }
}
