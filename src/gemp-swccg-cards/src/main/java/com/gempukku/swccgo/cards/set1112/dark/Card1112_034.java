package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Imperial Code Cylinder
 */
public class Card1112_034 extends AbstractUsedOrStartingInterrupt {
    public Card1112_034() {
        super(Side.DARK, 4, Title.Imperial_Code_Cylinder, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial officers are issued coded cylinders which access computer information via Scomp links. Each cylinder is coded to the officer's own security clearance.");
        setGameText("USED: Take Executor: Control Station, Homing Beacon, or Kessel into hand from Reserve Deck; reshuffle. STARTING: If Conference Room on table, deploy Ord Mantell, Death Star: War Room, There Is No Try, and two always [Immune to Alter] Effects (except Endor Shield or You Cannot Hide Forever). Place Interrupt in Lost Pile.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, GameTextActionId.LEGACY__THERE_IS_NO_TRY_V__DEPLOY_LASER_GATE_OR_POLITICAL_EFFECT)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Take card into hand from Reserve Deck");
            action.allowResponses("Take Executor: Control Station, Homing Beacon, or Kessel into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(
                                            Filters.title(Title.Control_Station), Filters.title("Homing Beacon"), Filters.title(Title.Kessel)), true));
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        if (!Filters.canSpot(game, self, Filters.Death_Star_Conference_Room)) {
            return null;
        }
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy cards from Reserve Deck");
        action.allowResponses("Deploy Ord Mantell, Death Star: War Room, There Is No Try, and two always Immune to Alter Effects",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Ord_Mantell), true));
                        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Death_Star_War_Room), true));
                        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title(Title.There_Is_No_Try), true));
                        action.appendEffect(new DeployCardsFromReserveDeckEffect(action, Filters.and(
                                Filters.Effect,
                                Filters.always_immune_to_Alter,
                                Filters.not(Filters.or(Filters.title(Title.Endor_Shield), Filters.title(Title.You_Cannot_Hide_Forever)))),
                                2, 2, false, true));
                        action.appendEffect(new PutCardFromVoidInLostPileEffect(action, playerId, self));
                    }
                });
        return action;
    }
}
