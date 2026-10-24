package com.gempukku.swccgo.cards.set1200.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
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
 * Title: Traffic Control (V)
 */
public class Card1200_012 extends AbstractDefensiveShield {
    public Card1200_012() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Traffic Control", ExpansionSet.VIRTUAL_SHIELDS, Rarity.V);
        setVirtualSuffix(true);
        setLore("Expert traffic controllers coordinate launches faster than automated launch systems. Rebel bases scramble fighters quickly during Imperial attacks.");
        setGameText("Plays on table. 'Three' on An Unusual Amount Of Fear is treated as 'Four'. You lose no Force to Boonta Eve Podrace. Opponent's retrieval from Boonta Eve Podrace may not be canceled. While you occupy three battlegrounds, Watto's Box is suspended.");
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
        modifiers.add(new ModifyGameTextModifier(self, Filters.title(Title.An_Unusual_Amount_Of_Fear), ModifyGameTextType.LEGACY__AN_UNUSUAL_AMOUNT_OF_FEAR__THREE_IS_FOUR));
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.Boonta_Eve_Podrace, playerId));
        modifiers.add(new ForceRetrievalFromCardMayNotBeCanceledModifier(self, Filters.Boonta_Eve_Podrace, opponent));
        modifiers.add(new SuspendsCardModifier(self, Filters.Wattos_Box, new OccupiesCondition(playerId, 3, Filters.battleground)));
        return modifiers;
    }
}
