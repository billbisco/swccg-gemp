package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Imperial
 * Title: Warrant Officer M'Kae
 */
public class Card1220_018 extends AbstractImperial {
    public Card1220_018() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Warrant Officer M'Kae", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Hard-working Avenger signal officer. Coordinates and tests tractor beam operations. Apprises Executor of his ship's activities and relays orders from the flagship to his captain.");
        setGameText("Adds 2 to power of anything he pilots. If your starship is about to be lost, may place devices aboard in owner's Used Pile. Once per turn, may deploy a device that deploys on a starship from Reserve Deck; reshuffle. Once per game, if just forfeited, may take the bottom card of your Lost Pile into hand.");
        addIcons(Icon.PILOT, Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__MKAE_V__DEPLOY_STARSHIP_DEVICE;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a starship device from Reserve Deck");
            action.setActionMsg("Deploy a device that deploys on a starship from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.device, Filters.starship, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        if ((TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, effectResult, Filters.and(Filters.your(self), Filters.starship))
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, Filters.and(Filters.your(self), Filters.starship)))
                && effectResult instanceof AboutToLeaveTableResult) {
            PhysicalCard starship = ((AboutToLeaveTableResult) effectResult).getCardAboutToLeaveTable();
            Collection<PhysicalCard> devices = Filters.filter(game.getGameState().getAttachedCards(starship), game, Filters.device);
            if (devices.isEmpty()) {
                devices = Filters.filterAllOnTable(game, Filters.and(Filters.device, Filters.attachedTo(starship)));
            }
            if (!devices.isEmpty()) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place devices aboard in Used Pile");
                action.appendEffect(
                        new PlaceCardsInUsedPileFromTableEffect(action, devices));
                actions.add(action);
            }
        }

        GameTextActionId lostPileId = GameTextActionId.LEGACY__MKAE_V__TAKE_BOTTOM_LOST_PILE;
        if (TriggerConditions.justForfeited(game, effectResult, playerId, self)
                && GameConditions.isOncePerGame(game, self, lostPileId)
                && GameConditions.hasLostPile(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, lostPileId);
            action.setText("Take bottom card of Lost Pile into hand");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.bottomOfLostPile(playerId), false));
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }
}
