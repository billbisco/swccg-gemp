package com.gempukku.swccgo.cards.set1100.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PlaceInUsedPileWhenCanceledModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set: Defensive Shields
 * Type: Defensive Shield
 * Title: Wise Advice
 */
public class Card1100_007 extends AbstractDefensiveShield {
    public Card1100_007() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Wise_Advice, ExpansionSet.VIRTUAL_ORIGINAL_SHIELDS, Rarity.V);
        setVirtualSuffix(true);
        setLore("The guidance of experienced Jedi allowed Luke to confront Vader.");
        setGameText("Plays on table. Your Immediate Effects may play for free. Whenever opponent cancels your card with Sense or Alter, place that canceled card in Used Pile.");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployFreeModifier(self, Filters.and(Filters.your(self), Filters.Immediate_Effect)));
        modifiers.add(new PlaceInUsedPileWhenCanceledModifier(self, Filters.your(self), opponent, Filters.or(Filters.Sense, Filters.Alter)));
        return modifiers;
    }
}
