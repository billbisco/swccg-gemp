package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LegacySearchNotFoundResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Virtual Block 7.1: I Want Every Part Of This Ship Checked. */
public class Card1271_010 extends AbstractNormalEffect {
    public Card1271_010() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "I Want Every Part Of This Ship Checked",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("Imperials use sensitive equipment to search captured ships for shielded compartments. Scanning crew BT-445 planned to search the Millennium Falcon.");
        setGameText("Deploy on table. Once per game, if Obi-Wan on table, may deploy Vader from Reserve Deck; reshuffle. May place this Effect in Used Pile to take a [Grabber] into hand from Reserve Deck; reshuffle. If you just attempted to take a card into hand or deploy a card from Reserve Deck, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card instead.");
        addIcons(Icon.SETUP);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                      PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId deployId = GameTextActionId.LEGACY__I_WANT_EVERY_PART_OF_THIS_SHIP_CHECKED__DEPLOY_CHARACTER;
        if (GameConditions.isOncePerGame(game, self, deployId)
                && GameConditions.canSpot(game, self, Filters.ObiWan)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId, true, false, Persona.VADER)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployId);
            action.setText("Deploy Vader from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action,
                    Filters.and(Filters.Vader, Filters.canBeTargetedBy(self)), true));
            actions.add(action);
        }
        GameTextActionId grabberId = GameTextActionId.LEGACY__I_WANT_EVERY_PART_OF_THIS_SHIP_CHECKED__TAKE_GRABBER;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, grabberId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, grabberId);
            action.setText("Place in Used Pile to take a Grabber");
            action.appendCost(new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                    Filters.and(Icon.GRABBER, Filters.canBeTargetedBy(self)), true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
                                                                                 EffectResult effectResult, PhysicalCard self,
                                                                                 int gameTextSourceCardId) {
        GameTextActionId retryId = GameTextActionId.LEGACY__I_WANT_EVERY_PART_OF_THIS_SHIP_CHECKED__SEARCH_FORCE_PILE;
        if (!LegacySearchNotFoundResult.isOwnSearchNotFound(effectResult, playerId, Zone.RESERVE_DECK)
                || !GameConditions.canSearchForcePile(game, playerId, self, retryId))
            return null;
        LegacySearchNotFoundResult failed = (LegacySearchNotFoundResult) effectResult;
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, retryId);
        action.setText("Place in Lost Pile to repeat the search in Force Pile");
        action.appendCost(new PlaceCardInLostPileFromTableEffect(action, self));
        action.appendEffect(failed.createSearchOfOtherPile(action, Zone.FORCE_PILE, true));
        return Collections.singletonList(action);
    }
}
