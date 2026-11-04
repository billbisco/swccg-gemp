package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyAndChooseInsteadEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Republic
 * Title: Clone Infantry Squad
 */
public class Card1291_036 extends AbstractRepublic {
    public Card1291_036() {
        super(Side.LIGHT, 2, 4, 3, 2, 4, "Clone Infantry Squad", Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setArmor(4);
        setLore("Clone trooper.");
        setGameText("Adds one battle destiny if with another clone. During battle, if you are about to draw a battle destiny, may draw two and choose one.");
        addIcons(Icon.WARRIOR, Icon.CLONE_ARMY, Icon.EPISODE_I);
        addKeywords(Keyword.CLONE_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.and(Filters.other(self), Filters.clone)), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canDrawDestinyAndChoose(game, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw two battle destiny and choose one");
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyAndChooseInsteadEffect(action, 2, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
