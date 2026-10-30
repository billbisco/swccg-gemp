package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardsEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Set 3: Revolution
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Advanced Preparation
 */
public class Card1103_002 extends AbstractUsedOrLostInterrupt {
    public Card1103_002() {
        super(Side.LIGHT, 6, "Advanced Preparation", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Early warning and careful planning allow the Rebels to achieve maximum readiness while still maintaining optimal flexibility.");
        setGameText("USED: Once per game, deploy Attack Run, Death Star: Trench, and/or one proton torpedoes from under your Starting Effect. (Immune to Sense.) LOST: Retrieve 1 Force. OR If One In A Million on table, retrieve any one card.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId usedActionId = GameTextActionId.OVS__ADVANCED_PREPARATION__DEPLOY_FROM_STARTING_EFFECT;
        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        Filter stackedFilter = Filters.or(Filters.Attack_Run, Filters.Death_Star_Trench, Filters.Proton_Torpedoes);

        if (GameConditions.isOncePerGame(game, self, usedActionId)
                && startingEffect != null
                && GameConditions.hasStackedCards(game, startingEffect, stackedFilter)) {
            final PhysicalCard stackedOn = startingEffect;
            final PlayInterruptAction action = new PlayInterruptAction(game, self, usedActionId, CardSubtype.USED);
            action.setText("Deploy cards from under Starting Effect");
            action.setImmuneTo(Title.Sense);
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.allowResponses("Deploy Attack Run, Death Star: Trench, and/or one proton torpedoes from under Starting Effect",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployStackedCardsEffect(action, stackedOn, Filters.Attack_Run, 0, 1, false));
                            action.appendEffect(
                                    new DeployStackedCardsEffect(action, stackedOn, Filters.Death_Star_Trench, 0, 1, false));
                            action.appendEffect(
                                    new DeployStackedCardsEffect(action, stackedOn, Filters.Proton_Torpedoes, 0, 1, false));
                        }
                    }
            );
            actions.add(action);
        }

        final PlayInterruptAction retrieveForce = new PlayInterruptAction(game, self, CardSubtype.LOST);
        retrieveForce.setText("Retrieve 1 Force");
        retrieveForce.allowResponses(
                new RespondablePlayCardEffect(retrieveForce) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        retrieveForce.appendEffect(
                                new RetrieveForceEffect(retrieveForce, playerId, 1));
                    }
                }
        );
        actions.add(retrieveForce);

        if (GameConditions.canSpot(game, self, Filters.One_In_A_Million)) {
            final PlayInterruptAction retrieveAny = new PlayInterruptAction(game, self, CardSubtype.LOST);
            retrieveAny.setText("Retrieve any one card");
            retrieveAny.allowResponses(
                    new RespondablePlayCardEffect(retrieveAny) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            retrieveAny.appendEffect(
                                    new RetrieveCardEffect(retrieveAny, playerId, Filters.any));
                        }
                    }
            );
            actions.add(retrieveAny);
        }

        return actions;
    }
}
