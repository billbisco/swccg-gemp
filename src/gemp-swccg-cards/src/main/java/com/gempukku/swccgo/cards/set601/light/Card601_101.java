package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 1
 * Type: Effect
 * Title: Rycar Ryjerd (V)
 * Text: PC-E1, the errata posted on the PC forum on 2014-03-27 (p874174); the release-sheet text is 2210_136 (legacy batch B41).
 */
public class Card601_101 extends AbstractNormalEffect {
    public Card601_101() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Rycar_Ryjerd, Uniqueness.UNRESTRICTED, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("A Bimm trader and smuggler of starship weapons. Trusts no one. Does business with anyone. Teaches smuggler apprentices. Has mastered Jawa language.");
        setGameText("Deploy on table. If your character is about to be lost from table, each player may place all their devices and Effects on that character in their Used Pile. Twice per game, may ▲ a device (or Effect) that deploys on a character. (Immune to Alter.)");
        addIcons(Icon.LEGACY_BLOCK_1);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId  = GameTextActionId.LEGACY__RYCAR_RYJERD_V__UPLOAD_CARD;

        final Filter filter = Filters.and(Filters.or(Filters.device, Filters.Effect), Filters.deploys_on_characters);

        // Check condition(s)
        if (GameConditions.isTwicePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a device (or an Effect) that deploys on a character into hand from Reserve Deck");

            action.appendUsage(
                    new TwicePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, filter, true));
            actions.add(action);
        }

        return actions;
    }

    /**
     * PC-E1 (PC forum "Current Rulings/Errata" p874174, 2014-03-27): each player MAY place all their own devices and
     * Effects on that character in their Used Pile (an optional response for each player). The release-sheet text
     * (2210_136) places all of them, mandatorily, in their owners' Used Piles.
     */
    protected boolean eachPlayerMayPlace() {
        return true;
    }

    /** Your character about to be lost (or forfeited) from table, or null. */
    private static PhysicalCard characterAboutToBeLost(SwccgGame game, EffectResult effectResult, PhysicalCard self, Filter toSave) {
        Filter aboutToBeLost = Filters.and(Filters.your(self), Filters.character, Filters.hasAttached(toSave));
        if (TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, effectResult, aboutToBeLost)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, aboutToBeLost)) {
            return ((AboutToLeaveTableResult) effectResult).getCardAboutToLeaveTable();
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (eachPlayerMayPlace())
            return null;
        Filter toSave = Filters.and(Filters.canBeTargetedBy(self), Filters.or(Filters.Effect, Filters.device));
        PhysicalCard card = characterAboutToBeLost(game, effectResult, self, toSave);
        if (card != null) {
            Collection<PhysicalCard> toPlaceInUsedPile = Filters.filterActive(game, self, Filters.and(Filters.attachedTo(card), toSave));
            if (!toPlaceInUsedPile.isEmpty()) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place devices and effects in Used Pile");
                action.appendEffect(
                        new PlaceCardsInUsedPileFromTableEffect(action, self.getOwner(), toPlaceInUsedPile));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return placeOwnCardsInUsedPile(playerId, game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return placeOwnCardsInUsedPile(playerId, game, effectResult, self, gameTextSourceCardId);
    }

    private List<OptionalGameTextTriggerAction> placeOwnCardsInUsedPile(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (!eachPlayerMayPlace())
            return null;
        Filter toSave = Filters.and(Filters.owner(playerId), Filters.canBeTargetedBy(self), Filters.or(Filters.Effect, Filters.device));
        PhysicalCard card = characterAboutToBeLost(game, effectResult, self, toSave);
        if (card != null) {
            Collection<PhysicalCard> toPlaceInUsedPile = Filters.filterActive(game, self, Filters.and(Filters.attachedTo(card), toSave));
            if (!toPlaceInUsedPile.isEmpty()) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
                action.setText("Place your devices and Effects on " + GameUtils.getFullName(card) + " in Used Pile");
                action.setActionMsg("Place " + toPlaceInUsedPile.size() + " device(s) and Effect(s) on " + GameUtils.getCardLink(card) + " in Used Pile");
                action.appendEffect(
                        new PlaceCardsInUsedPileFromTableEffect(action, playerId, toPlaceInUsedPile));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
