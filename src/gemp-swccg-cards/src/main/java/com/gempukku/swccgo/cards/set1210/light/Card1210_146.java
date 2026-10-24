package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.filters.LegacyFilters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used
 * Title: Balanced Attack
 */
public class Card1210_146 extends AbstractUsedInterrupt {
    public Card1210_146() {
        super(Side.LIGHT, 4, Title.Balanced_Attack, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Alliance starfighter pilots cross-train with other starship types to maximize combat efficiency.");
        setGameText("If your corvette, cruiser, and frigate are in battle together, cancel an opponent's non-[Immune to Sense] Interrupt. OR Deploy a frigate (deploys -2) from Reserve Deck to same system as your corvette and cruiser; reshuffle. OR Deploy a cruiser (deploys -3) from Reserve Deck to same system as your corvette and frigate; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.opponents(self), Filters.Interrupt, Filters.not(Filters.immune_to_Sense)))
                && GameConditions.isDuringBattle(game)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.corvette))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Star_Cruiser))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), LegacyFilters.frigate))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId frigateId = GameTextActionId.LEGACY__BALANCED_ATTACK_V__DEPLOY_FRIGATE;
        Filter corvetteAndCruiserSystem = Filters.sameSystemAs(self, Filters.and(Filters.your(self), Filters.corvette));
        Filter alsoCruiserSystem = Filters.sameSystemAs(self, Filters.and(Filters.your(self), Filters.Star_Cruiser));
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, frigateId)
                && GameConditions.canSpotLocation(game, Filters.and(corvetteAndCruiserSystem, alsoCruiserSystem))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, frigateId);
            action.setText("Deploy frigate from Reserve Deck");
            action.allowResponses("Deploy a frigate (deploys -2) from Reserve Deck to same system as your corvette and cruiser",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardToTargetFromReserveDeckEffect(action, LegacyFilters.frigate,
                                            Filters.and(Filters.system, Filters.sameSystemAs(self, Filters.and(Filters.your(self), Filters.corvette)),
                                                    Filters.sameSystemAs(self, Filters.and(Filters.your(self), Filters.Star_Cruiser))),
                                            -2, true));
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId cruiserId = GameTextActionId.LEGACY__BALANCED_ATTACK_V__DEPLOY_CRUISER;
        Filter corvetteAndFrigateSystem = Filters.sameSystemAs(self, Filters.and(Filters.your(self), Filters.corvette));
        Filter alsoFrigateSystem = Filters.sameSystemAs(self, Filters.and(Filters.your(self), LegacyFilters.frigate));
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, cruiserId)
                && GameConditions.canSpotLocation(game, Filters.and(corvetteAndFrigateSystem, alsoFrigateSystem))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, cruiserId);
            action.setText("Deploy cruiser from Reserve Deck");
            action.allowResponses("Deploy a cruiser (deploys -3) from Reserve Deck to same system as your corvette and frigate",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Star_Cruiser,
                                            Filters.and(Filters.system, Filters.sameSystemAs(self, Filters.and(Filters.your(self), Filters.corvette)),
                                                    Filters.sameSystemAs(self, Filters.and(Filters.your(self), LegacyFilters.frigate))),
                                            -3, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
