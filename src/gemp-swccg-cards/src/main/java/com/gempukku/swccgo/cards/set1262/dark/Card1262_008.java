package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.LimitDestinyDrawsAddedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddBattleDestinyDrawsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.2
 * Type: Effect
 * Title: Drop! (V)
 */
public class Card1262_008 extends AbstractNormalEffect {
    public Card1262_008() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Drop, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Anakin had to heed Qui-Gon's advice to avoid the rapidly approaching storm.");
        setGameText("Deploy on table. [Permanent weapon] and [Maintenance] cards do not add battle destiny draws. During battle, neither player may add more than two destiny to power (or total power) or one destiny to total attrition. Once per turn, may place a card from hand in Used Pile. (Immune to Alter.)");
        addIcons(Icon.EPISODE_I, Icon.CORUSCANT);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotAddBattleDestinyDrawsModifier(self, Filters.or(Icon.PERMANENT_WEAPON, Icon.MAINTENANCE)));
        modifiers.add(new LimitDestinyDrawsAddedModifier(self, new DuringBattleCondition(), false, 2, null));
        modifiers.add(new LimitDestinyDrawsAddedModifier(self, new DuringBattleCondition(), true, 1, null));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__DROP_V__PLACE_CARD_FROM_HAND_IN_USED_PILE;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasHand(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place card from hand in Used Pile");
            action.setActionMsg("Place a card from hand in Used Pile");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new PutCardFromHandOnUsedPileEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
