package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.effects.CancelCardResultEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Tauntaun Handler
 */
public class Card1104_020 extends AbstractRebel {
    public Card1104_020() {
        super(Side.LIGHT, 2, 2, 1, 1, 3, "Tauntaun Handler", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Corman Quien is a typical Scout at Echo Base. Captured, tanned and trained the native tauntauns.");
        setGameText("Creature vehicles he 'rides' are power +2. While 'riding' a Tauntaun, adds one battle destiny. May cancel any result of Tauntaun Bones.");
        addIcons(Icon.WARRIOR, Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.creature_vehicle, Filters.hasAboard(self)), 2));
        modifiers.add(new AddsBattleDestinyModifier(self, new AboardCondition(self, Filters.tauntaun), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPerformingGameTextAction(game, effect, Filters.title("Tauntaun Bones"))) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel result of Tauntaun Bones");
            action.appendEffect(
                    new CancelCardResultEffect(action, effect));
            return Collections.singletonList(action);
        }
        return null;
    }
}
