package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 4: Expanded Universe
 * Type: Character
 * Subtype: Alien
 * Title: Fixer
 */
public class Card1240_070 extends AbstractAlien {
    public Card1240_070() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "Fixer", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLore("Careful manipulator of funds and Imperial taxation codes. Buys droids from Jawas and sells them to the Alliance.");
        setGameText("Adds 2 to power of anything he pilots. Deploys -1 to same site as Camie. Once per turn, may use 1 Force to place a card from Crash Site Memorial in Used Pile. If Crash Site Memorial leaves table (except by your card), place all cards on it in Used Pile.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.LEGACY_BLOCK_4);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new DeployCostToLocationModifier(self, -1, Filters.sameSiteAs(self, Filters.title("Camie"))));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new AddsPowerToPilotedBySelfModifier(self, 2));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard memorial = Filters.findFirstActive(game, self, Filters.title(Title.Crash_Site_Memorial));
        if (memorial != null
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasStackedCards(game, memorial, Filters.any)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Place a card from Crash Site Memorial in Used Pile");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new ChooseStackedCardEffect(action, playerId, memorial, Filters.any) {
                @Override
                protected void cardSelected(PhysicalCard selectedCard) {
                    action.appendEffect(new PutStackedCardInUsedPileEffect(action, playerId, selectedCard, false));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isAboutToLeaveTableExceptFromSourceCard(game, effectResult,
                Filters.title(Title.Crash_Site_Memorial), Filters.your(self))) {
            PhysicalCard memorial = ((AboutToLeaveTableResult) effectResult).getCardAboutToLeaveTable();
            if (GameConditions.hasStackedCards(game, memorial)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place Memorial's cards in Used Pile");
                action.appendEffect(new PutStackedCardsInUsedPileEffect(action, self.getOwner(),
                        new ArrayList<>(game.getGameState().getStackedCards(memorial)), false));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
