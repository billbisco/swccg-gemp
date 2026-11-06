package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.List;

/** Legacy Virtual Block 6.1: Dianoga. */
public class Card1261_019 extends AbstractCreature {
    public Card1261_019() {
        super(Side.DARK, 3, 4, null, 4, 0, "Dianoga", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Garbage squid' from Vodran's jungles. Changes color to match last meal. When unfed, turns transparent. Eats almost anything. Flexible eyestalk. 7 tentacles. Up to 6 meters long.");
        setGameText("* Ferocity = 2 + destiny. Habitat: swamps, jungles, Trash Compactor, and Dark Waters. Whenever this creature eats an opponent's card, opponent loses 2 Force.");
        addModelType(ModelType.SWAMP);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.or(Filters.swamp, Filters.jungle, Filters.Trash_Compactor, Filters.sameSiteAs(self, Filters.Dark_Waters));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new DefinedByGameTextFerocityModifier(self, 2, 1));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
            PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justEatenBy(game, result, Filters.opponents(self), self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 2 Force");
            action.appendEffect(new LoseForceEffect(action, game.getOpponent(self.getOwner()), 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
