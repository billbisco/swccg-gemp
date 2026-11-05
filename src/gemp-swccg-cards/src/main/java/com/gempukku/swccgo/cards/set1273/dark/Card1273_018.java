package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.LegacySpiceCards;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeConvertedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Location
 * Subtype: Site
 * Title: Kessel: Spice Mines - Administrator's Office
 */
public class Card1273_018 extends AbstractSite {
    public Card1273_018() {
        super(Side.DARK, "Kessel: Spice Mines - Administrator's Office", Title.Kessel, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLocationDarkSideGameText("If opponent just used this site's game text, may take into hand from Reserve Deck; reshuffle (or retrieve into hand) any one card.");
        setLocationLightSideGameText("Kessel may not be converted. May lose 1 Force to place a 'spice' card in owner's Used Pile.");
        setGameText("Dark: If opponent just used this site's game text, may take into hand from Reserve Deck; reshuffle (or retrieve into hand) any one card. Light: Kessel may not be converted. May lose 1 Force to place a 'spice' card in owner's Used Pile.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.SCOMP_LINK, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeConvertedModifier(self, Filters.title(Title.Kessel)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (LegacySpiceCards.countSpiceCards(game, self) == 0) {
            return null;
        }
        final String playerId = playerOnLightSideOfLocation;
        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, GameTextActionId.LEGACY__ADMINISTRATORS_OFFICE__PLACE_SPICE_IN_USED_PILE);
        action.setText("Place a 'spice' card in owner's Used Pile");
        action.setActionMsg("Lose 1 Force to place a 'spice' card in owner's Used Pile");
        action.appendCost(
                new LoseForceEffect(action, playerId, 1, true));
        final String darkPlayer = game.getOpponent(playerId);
        action.appendEffect(
                new ChooseStackedCardEffect(action, playerId, LegacySpiceCards.SPICE_MINE_OPERATIONS_FILTER) {
                    @Override
                    protected void cardSelected(PhysicalCard selectedCard) {
                        action.appendEffect(
                                new PutStackedCardInUsedPileEffect(action, selectedCard.getOwner(), selectedCard, false));
                        // Dark: if opponent just used this site's game text, may take into hand (or retrieve into hand) any one card
                        if (!self.isLocationGameTextCanceledForPlayer(darkPlayer)) {
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, darkPlayer,
                                            new MultipleChoiceAwaitingDecision(GameUtils.getCardLink(self) + ": opponent just used this site's game text. Choose", new String[]{"Take any one card into hand from Reserve Deck", "Retrieve any one card into hand", "Do nothing"}) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    if (index == 0) {
                                                        action.appendEffect(
                                                                new TakeCardIntoHandFromReserveDeckEffect(action, darkPlayer, Filters.any, true));
                                                    } else if (index == 1) {
                                                        action.appendEffect(
                                                                new RetrieveCardIntoHandEffect(action, darkPlayer, Filters.any));
                                                    }
                                                }
                                            }));
                        }
                    }
                });
        return Collections.singletonList(action);
    }
}
