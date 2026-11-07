package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyNumCardsDrawnInStartingHandEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TakeCardFromVoidIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;

/** Legacy Block 5 Krayt Dragon Howl (V), printing 67E. */
public class Card1250_067 extends AbstractUsedOrStartingInterrupt {
    public Card1250_067() {
        super(Side.LIGHT, 4, "Krayt Dragon Howl", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Using Jedi skill, Obi-Wan Kenobi imitated perfectly the mournful howl of the dangerous krayt dragon to scare Tusken Raiders away from Luke Skywalker.");
        setGameText("USED: Activate 1 Force. STARTING: If Credits Will Do Fine on table, deploy from Reserve Deck Queen's Landing Site (with [Tatooine] Obi-Wan, his matching weapon, and [Virtual Block 1] Obi-Wan's Cape there). When you draw your starting hand, draw five cards instead of eight. Place this Interrupt in hand.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        if (!GameConditions.canActivateForce(game, playerId)) return null;
        PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
        action.setText("Activate 1 Force");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new ActivateForceEffect(action, playerId, 1));
            }
        });
        return List.of(action);
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(String playerId, SwccgGame game, PhysicalCard self) {
        if (!GameConditions.canSpot(game, self, Filters.Credits_Will_Do_Fine)) return null;

        PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy Queen's Landing Site with Obi-Wan, his weapon, and his cape");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                Filter landingSite = Filters.and(Filters.site, Filters.title("Tatooine: Queen's Landing Site"));
                action.appendEffect(new DeployCardFromReserveDeckEffect(action, landingSite, true, false) {
                    @Override
                    protected void cardDeployed(PhysicalCard site) {
                        action.insertEffect(deployObiWan(action, site));
                    }
                });
                action.appendEffect(new ModifyNumCardsDrawnInStartingHandEffect(action, playerId, 5));
                action.appendEffect(new TakeCardFromVoidIntoHandEffect(action, playerId, self));
            }
        });
        return action;
    }

    private DeployCardToLocationFromReserveDeckEffect deployObiWan(PlayInterruptAction action, PhysicalCard site) {
        return new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Icon.TATOOINE, Filters.ObiWan),
                Filters.sameCardId(site), true, false) {
            @Override
            protected void cardDeployed(PhysicalCard obiWan) {
                Filter target = Filters.sameCardId(obiWan);
                Filter cape = Filters.and(Icon.LEGACY_BLOCK_1, Filters.title("Obi-Wan's Cape"));
                action.insertEffect(
                        new DeployCardToTargetFromReserveDeckEffect(action, Filters.matchingWeaponForCharacter(obiWan), target, true, false),
                        new DeployCardToTargetFromReserveDeckEffect(action, cape, target, true, false));
            }
        };
    }
}
