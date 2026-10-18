package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Rebel
 * Title: Dack Ralter
 */
public class Card1273_022 extends AbstractRebel {
    public Card1273_022() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Dack Ralter", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Born in an Imperial labor camp on Kalist 6. Joined the Rebellion after his escape. Young student of Commander Skywalker. Experienced crackshot gunner.");
        setGameText("Adds 2 to the power of anything he pilots. Heroic. Rogue Squadron Pilot. During battle when Dack (or a starship or vehicle he pilots) fires a weapon, may add 1 to total weapon destiny and hit targets are power -2. If Dack is about to leave table may place him in Used Pile instead.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.ROGUE_SQUADRON, Keyword.GUNNER, Keyword.HEROIC);
        addPersona(Persona.DACK);
        setMatchingVehicleFilter(Filters.Rogue_1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.any, Filters.or(self, Filters.hasPiloting(self)), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersWhenInactiveInPlay(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLost(game, effectResult, self)) {
            PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(justLostCard) + " in Used Pile");
            action.appendEffect(new PutCardFromLostPileInUsedPileEffect(action, playerId, justLostCard, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
