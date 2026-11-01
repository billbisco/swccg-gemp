package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePlacedInUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Effect
 * Title: So This Is How Liberty Dies
 */
public class Card1116_005 extends AbstractNormalEffect {
    public Card1116_005() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "So This Is How Liberty Dies", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("Deploy on Senate. Your [Coruscant] Political Effects are lost. While you have a character here with rebellion agenda, your Force drains at battlegrounds where you have a Rebel are +1 and may not be canceled. May not be canceled or placed in Used Pile.");
        addIcons(Icon.CORUSCANT);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.title(Title.Galactic_Senate), Filters.titleContains("Senate"));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        HereCondition rebellionHere = new HereCondition(self, Filters.and(Filters.your(self), Filters.character, Agenda.REBELLION));
        Filter yourRebelBattlegrounds = Filters.and(Filters.battleground, Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.Rebel)));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeCanceledModifier(self, self));
        modifiers.add(new MayNotBePlacedInUsedPileModifier(self, self));
        modifiers.add(new ForceDrainModifier(self, yourRebelBattlegrounds, rebellionHere, 1, self.getOwner()));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, yourRebelBattlegrounds, rebellionHere, game.getOpponent(self.getOwner()), self.getOwner()));
        return modifiers;
    }
}
