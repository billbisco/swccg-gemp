package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 2
 * Type: Effect
 * Title: Maneuvering Flaps (V)
 *
 * Original version of 1220_128 (Virtual Block 2 release sheet, Dec 2009, card #128): no "May not be suspended" (added by
 * the AR 2.0 errata, by 2012-08-27). Same id scheme as the published sheet id even though the current card's class is
 * Card601_237.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_128 extends AbstractNormalEffect {
    public Card2220_128() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Maneuvering_Flaps, Uniqueness.UNRESTRICTED, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Enhanced steering mechanisms on Rebel T-47s provide increased maneuverability in planetary atmospheres.");
        setGameText("Deploy on table. During your deploy phase, may reveal an unpiloted combat vehicle from hand to [upload] its matching pilot character (or vice versa) and deploy both simultaneously. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1220_128", "",
                "Virtual Block 2 release sheet (2009-12-07), card #128; superseded by 2012-02-07 (PC forum \"updated errata\")", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 2, 7)));
        addIcons(Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_2);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter unpilotedCombatVehicle = Filters.and(Filters.unpiloted, Filters.combat_vehicle);
        Filter filter = Filters.and(Filters.or(Filters.pilot, unpilotedCombatVehicle), Filters.isUniquenessOnTableNotReached);

        GameTextActionId gameTextActionId = GameTextActionId.MANEUVERING_FLAPS__DEPLOY_MATCHING_COMBAT_VEHICLE_OR_PILOT;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.hasInHand(game, playerId, filter)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal pilot or unpiloted combat vehicle from hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, filter) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final Filter searchFilter;
                            if (Filters.character.accepts(game, selectedCard)) {
                                action.setActionMsg("Take " + GameUtils.getCardLink(selectedCard) + "'s matching unpiloted combat vehicle from Reserve Deck and deploy both simultaneously");
                                searchFilter = Filters.and(unpilotedCombatVehicle, Filters.matchingVehicle(selectedCard));
                            }
                            else {
                                action.setActionMsg("Take " + GameUtils.getCardLink(selectedCard) + "'s matching pilot from Reserve Deck and deploy both simultaneously");
                                searchFilter = Filters.matchingPilot(selectedCard);
                            }
                            // Perform result(s)
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
