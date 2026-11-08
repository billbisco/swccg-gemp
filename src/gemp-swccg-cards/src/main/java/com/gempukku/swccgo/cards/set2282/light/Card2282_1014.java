package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ResetForfeitEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Subtype: Alien
 * Title: Lando Calrissian, Unlikely Hero
 * PC-E1 version of 1282_14: the PC website card list text (Dec 2012). Still Luxury Yacht (the original title of Lady
 * Luck), and the battle ability targets a card here first, then draws destiny ("target is forfeit = 0").
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_1014 extends Card2282_014 {
    public Card2282_1014() {
        super();
        setGameText("Adds 3 to anything he pilots. May reveal from hand to take Luxury Yacht into hand from Reserve Deck; reshuffle; and deploy both simultaneously. While piloting (or with a female), adds one battle destiny. During battle, may target a card here. Draw destiny. If destiny < twice the number of scoundrels here, target is forfeit = 0.");
        setLegacyVersion(new LegacyVersion("1282_14", "PC-E1", "PC website card list (2012-12-12)", LocalDate.of(2012, 12, 12), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        final Filter targetFilter = Filters.and(Filters.here(self), Filters.forfeitMayBeReduced);

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Target a card here");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target a card here", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Draw destiny to make " + GameUtils.getCardLink(targetedCard) + " forfeit = 0",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard target = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            if (totalDestiny == null)
                                                                return;
                                                            int scoundrelCount = Filters.countActive(game, self, Filters.and(Filters.scoundrel, Filters.here(self)));
                                                            if (totalDestiny < 2 * scoundrelCount) {
                                                                action.appendEffect(
                                                                        new ResetForfeitEffect(action, target, 0));
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
