package com.gempukku.swccgo.cards.set1271.light;

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

/** Virtual Block 7.1: Oh, He's Not Dead, Not Yet. */
public class Card1271_041 extends AbstractNormalEffect {
    public Card1271_041() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Oh, He's Not Dead, Not Yet",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("'Where did you dig up that old fossil?' 'I don't think he exists anymore.' 'Surely he must be dead by now.' 'I can't believe he's gone.' 'Oh, he's not dead, not yet.' Obi's back!");
        setGameText("Deploy on table. Once per game, if Vader on table, may deploy Obi-Wan from Reserve Deck; reshuffle. May place this Effect in Used Pile to take a [Grabber] into hand from Reserve Deck; reshuffle. If you just attempted to take into hand or deploy a card from Reserve Deck, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card; reshuffle.");
        addIcons(Icon.SETUP);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                      PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId deployId = GameTextActionId.LEGACY__OH_HES_NOT_DEAD_NOT_YET__DEPLOY_CHARACTER;
        if (GameConditions.isOncePerGame(game, self, deployId)
                && GameConditions.canSpot(game, self, Filters.Vader)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId, true, false, Persona.OBIWAN)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployId);
            action.setText("Deploy Obi-Wan from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action,
                    Filters.and(Filters.ObiWan, Filters.canBeTargetedBy(self)), true));
            actions.add(action);
        }
        GameTextActionId grabberId = GameTextActionId.LEGACY__OH_HES_NOT_DEAD_NOT_YET__TAKE_GRABBER;
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
        GameTextActionId retryId = GameTextActionId.LEGACY__OH_HES_NOT_DEAD_NOT_YET__SEARCH_FORCE_PILE;
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
