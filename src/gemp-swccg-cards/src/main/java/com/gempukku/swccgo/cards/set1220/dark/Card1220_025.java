package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHavePowerReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Alert My Star Destroyer!
 */
public class Card1220_025 extends AbstractNormalEffect {
    public Card1220_025() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Alert My Star Destroyer!", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("For important Imperial dignitaries, an individual Star Destroyer is placed at their disposal.");
        setGameText("Deploy on table. During your deploy phase, you may reveal a capital starship from hand to take its matching pilot character (or vice versa) from Reserve Deck, and deploy both simultaneously; reshuffle. While piloted by an Imperial, Star Destroyers may not have their power reduced. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starDestroyerPilotedByImperial = Filters.and(Filters.Star_Destroyer, Filters.hasPiloting(self, Filters.Imperial));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHavePowerReducedModifier(self, starDestroyerPilotedByImperial, self.getOwner()));
        modifiers.add(new MayNotHavePowerReducedModifier(self, starDestroyerPilotedByImperial, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter capitalStarship = Filters.capital_starship;
        Filter filter = Filters.and(Filters.or(Filters.pilot, capitalStarship), Filters.isUniquenessOnTableNotReached);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ALERT_MY_STAR_DESTROYER_V__DEPLOY_MATCHING;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.hasInHand(game, playerId, filter)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal capital starship or matching pilot from hand");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, filter) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final Filter searchFilter;
                            if (Filters.character.accepts(game, selectedCard)) {
                                action.setActionMsg("Take " + GameUtils.getCardLink(selectedCard) + "'s matching capital starship from Reserve Deck and deploy both simultaneously");
                                searchFilter = Filters.and(capitalStarship, Filters.matchingStarship(selectedCard));
                            }
                            else {
                                action.setActionMsg("Take " + GameUtils.getCardLink(selectedCard) + "'s matching pilot from Reserve Deck and deploy both simultaneously");
                                searchFilter = Filters.matchingPilot(selectedCard);
                            }
                            action.appendEffect(
                                    new ShowCardOnScreenEffect(action, selectedCard));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, selectedCard, searchFilter, true));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
