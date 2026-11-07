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
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotAddBattleDestinyDrawsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 6.2: Drop! (V). */
public class Card1262_008 extends AbstractNormalEffect {
    public Card1262_008() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Drop, Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Anakin had to heed Qui-Gon's advice to avoid the rapidly approaching storm.");
        setGameText("Deploy on table. [Permanent Weapon] and [Maintenance] cards do not add battle destiny draws. During battle, neither player may add more than two destiny to power (or total power) or one destiny to total attrition. Once per turn, may place a card from hand in Used Pile. (Immune to Alter.)");
        addIcons(Icon.EPISODE_I, Icon.CORUSCANT, Icon.LEGACY_BLOCK_6);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayNotAddBattleDestinyDrawsModifier(self, Filters.or(Icon.PERMANENT_WEAPON, Icon.MAINTENANCE)));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int sourceId) {
        var id = GameTextActionId.OTHER_CARD_ACTION_1;
        if (!GameConditions.isOncePerTurn(game, self, playerId, sourceId, id)
                || !GameConditions.hasInHand(game, playerId, Filters.any)) return null;
        var action = new TopLevelGameTextAction(self, sourceId, id);
        action.setText("Place a card from hand in Used Pile");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new PutCardFromHandOnUsedPileEffect(action, playerId));
        return List.of(action);
    }
}
