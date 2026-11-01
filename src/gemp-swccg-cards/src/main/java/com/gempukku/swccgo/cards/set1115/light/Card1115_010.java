package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Rebel
 * Title: Fixer
 */
public class Card1115_010 extends AbstractRebel {
    public Card1115_010() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "Fixer", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Adds 2 to power of anything he pilots. Deploys -1 to same site as Camie. Once per turn, may use 1 Force to place a card from Crash Site Memorial in Used Pile. If Crash Site Memorial leaves table (except by your card), place all cards on it in Used Pile.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.sameSiteAs(self, Filters.title("Camie"))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new AddsPowerToPilotedBySelfModifier(self, 2));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard memorial = Filters.findFirstActive(game, self, Filters.title("Crash Site Memorial"));
        if (memorial != null
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasStackedCards(game, memorial, Filters.any)) {
            final PhysicalCard stackedOn = memorial;
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Place a card from Crash Site Memorial in Used Pile");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new ChooseStackedCardEffect(action, playerId, stackedOn, Filters.any) {
                @Override
                protected void cardSelected(PhysicalCard selectedCard) {
                    action.appendEffect(new com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect(action, playerId, selectedCard, false));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
