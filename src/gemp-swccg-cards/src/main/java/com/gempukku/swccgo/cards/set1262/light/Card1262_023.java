package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotAddBattleDestinyDrawsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Thrown Back (V), final Light master page 30. */
public class Card1262_023 extends AbstractNormalEffect {
    public Card1262_023() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Thrown_Back, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Being sent to dispose of two Jedi is the battle droid equivalent of a really bad day at the office.");
        setGameText("Deploy on table. [Permanent weapon] and [Maintenance] cards do not add battle destiny draws. During battle, neither player may add more than two destiny to power (or total power) or one destiny to total attrition. Once per turn, may place a card from hand in Used Pile. (Immune to Alter.)");
        addIcons(Icon.EPISODE_I, Icon.CORUSCANT);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayNotAddBattleDestinyDrawsModifier(self, Filters.or(Icon.PERMANENT_WEAPON, Icon.MAINTENANCE)));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self,
            int gameTextSourceCardId) {
        if (!GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                || !GameConditions.hasHand(game, playerId)) {
            return null;
        }
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
        action.setText("Place a card from hand in Used Pile");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new PutCardFromHandOnUsedPileEffect(action, playerId));
        return List.of(action);
    }
}
