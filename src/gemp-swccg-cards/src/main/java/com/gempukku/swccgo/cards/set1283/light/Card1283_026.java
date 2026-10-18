package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Mission
 * Title: Kessel Run (V)
 */
public class Card1283_026 extends AbstractMission {
    public Card1283_026() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Kessel Run", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V, false);
        setVirtualSuffix(true);
        setGameText("Deploy on Infiltration or non-[Virtual Block 4] Watch Your Step. May deploy Kessel from Reserve Deck; reshuffle. May target a scoundrel or smuggler at a non-Kessel system (target is immune to Set For Stun) If target is lost, place cards here in Used Pile. When target reaches Kessel, draw five 'parsec' destinies and stack them here (or this Mission is lost). Whenever you win a battle (or at start of your turn), take a card here in hand. When target returns to original system; Mission completed (counts as two Kessel Runs), Infiltration flips, may take into hand any one card from Reserve Deck; reshuffle. If total 'parsec' destiny < 12 when completed, may subtract 1 from each of opponent's destiny draws. May not be canceled.");
        addIcons(Icon.SETUP);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.title("Infiltration"), Filters.title(Title.Watch_Your_Step));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.title("Infiltration"), Filters.title(Title.Watch_Your_Step));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeCanceledModifier(self, self));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.or(Filters.scoundrel, Filters.smuggler), Filters.at(Filters.and(Filters.system, Filters.not(Filters.Kessel_system)))), Filters.Set_For_Stun));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId deployId = GameTextActionId.KESSEL_RUN_V__DEPLOY_KESSEL;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId, Title.Kessel)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, deployId);
            action.setText("Deploy Kessel from Reserve Deck");
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.Kessel_system, true));
            actions.add(action);
        }
        GameTextActionId takeId = GameTextActionId.KESSEL_RUN_V__TAKE_CARD_FROM_RESERVE;
        if (GameConditions.hasStackedCards(game, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, takeId);
            action.setText("Take a card into hand from Reserve Deck");
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.any, true));
            actions.add(action);
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLost(game, effectResult, Filters.or(Filters.scoundrel, Filters.smuggler))
                && GameConditions.hasStackedCards(game, self)) {
            Collection<PhysicalCard> stacked = Filters.filterStacked(game, Filters.stackedOn(self));
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place cards here in Used Pile");
            action.appendEffect(new PutStackedCardsInUsedPileEffect(action, self.getOwner(), stacked, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
