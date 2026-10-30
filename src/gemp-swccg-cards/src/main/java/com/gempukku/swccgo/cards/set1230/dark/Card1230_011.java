package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayInitiateForceDrainCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfForceDrainModifierEffect;
import com.gempukku.swccgo.logic.effects.ForceDrainEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainBonusesMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ResetForceDrainModifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Renz
 */
public class Card1230_011 extends AbstractImperial {
    public Card1230_011() {
        super(Side.DARK, 2, 2, 2, 2, 4, Title.Renz, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Leader of one of the Emperor's finest legions of troops. His command was placed at the disposal of Colonel Dyer. Always on the lookout for Rebel activity.");
        setGameText("Once per game, if at a battleground site, may initiate a Force drain that cannot be canceled; Force drain is +X (this bonus may not be canceled; ignore other modifiers) where X = the number of unique (•) Stormtroopers at battleground sites.");
        addIcons(Icon.WARRIOR, Icon.ENDOR);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LIEUTENANT_RENZ_V__INITIATE_FORCE_DRAIN;
        final PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && GameConditions.isAtLocation(game, self, Filters.battleground_site)
                && location != null
                && GameConditions.canInitiateForceDrainAtLocation(game, playerId, location)) {

            int icons = game.getModifiersQuerying().getIconCount(game.getGameState(), location, Icon.DARK_FORCE);
            int uniqueStormtroopers = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.unique, Filters.stormtrooper, Filters.at(Filters.battleground_site)));
            final int resetAmount = icons + uniqueStormtroopers;

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Initiate Force drain");
            action.setActionMsg("Initiate a Force drain that cannot be canceled (reset to " + resetAmount + ")");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendCost(
                    new PayInitiateForceDrainCostEffect(action, location, playerId));
            action.appendEffect(
                    new AddUntilEndOfForceDrainModifierEffect(action,
                            new ForceDrainsMayNotBeCanceledModifier(self, location, playerId),
                            "Force drain may not be canceled"));
            action.appendEffect(
                    new AddUntilEndOfForceDrainModifierEffect(action,
                            new ForceDrainBonusesMayNotBeCanceledModifier(self, self, location),
                            "Force drain bonuses may not be canceled"));
            action.appendEffect(
                    new AddUntilEndOfForceDrainModifierEffect(action,
                            new ResetForceDrainModifier(self, location, resetAmount),
                            "Force drain = " + resetAmount));
            action.appendEffect(
                    new ForceDrainEffect(action, location));
            return Collections.singletonList(action);
        }
        return null;
    }
}
