package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Rebel
 * Title: Theron Nett
 */
public class Card1115_032 extends AbstractRebel {
    public Card1115_032() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Theron Nett", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Experienced smuggler from Ord Mantell. Piloted Red 10 at the Battle of Yavin. Long-time wingman of Garven Dreis.");
        setGameText("Adds 2 to power of anything he pilots. During your deploy phase, if piloting Red 10, may target one of your starships; deploy a non-[Theed Palace] astromech or matching pilot aboard target from Reserve Deck; reshuffle.");
        addIcons(Icon.PILOT, Icon.SPECIAL_EDITION);
        addKeywords(Keyword.RED_SQUADRON, Keyword.SMUGGLER);
        setMatchingStarshipFilter(Filters.Red_10);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THERON_NETT_V__DEPLOY_ABOARD;

        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.isPiloting(game, self, Filters.Red_10)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.starship))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy astromech or matching pilot aboard");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose your starship", Filters.and(Filters.your(self), Filters.starship)) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Deploy a non-[Theed Palace] astromech or matching pilot aboard " + GameUtils.getCardLink(targetedCard),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard starship = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new DeployCardAboardFromReserveDeckEffect(action,
                                                            Filters.or(Filters.and(Filters.astromech_droid, Filters.not(Icon.THEED_PALACE)), Filters.matchingPilot(starship)),
                                                            Filters.sameCardId(starship), true));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
