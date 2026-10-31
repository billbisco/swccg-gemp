package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
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
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: We'll Find Han
 */
public class Card1106_023 extends AbstractUsedOrLostInterrupt {
    public Card1106_023() {
        super(Side.LIGHT, 4, Title.Well_Find_Han, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I promise.' 'Auuuuuug!'");
        setGameText("USED: Deploy Han (except Han, Chewie, And The Falcon) from Reserve Deck; reshuffle. LOST: Deploy Han (except Han, Chewie, And The Falcon) from your Lost Pile.");
        addIcons(Icon.CLOUD_CITY);
    }

    private Filter hanExceptCombo() {
        return Filters.and(Filters.Han, Filters.not(Filters.title("Han, Chewie, And The Falcon")));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final Filter hanFilter = hanExceptCombo();

        GameTextActionId reserveId = GameTextActionId.LEGACY__WELL_FIND_HAN_V__DEPLOY_HAN_RESERVE;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, reserveId, Persona.HAN)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, reserveId, CardSubtype.USED);
            action.setText("Deploy Han from Reserve Deck");
            action.allowResponses("Deploy Han (except Han, Chewie, And The Falcon) from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, hanFilter, true));
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId lostId = GameTextActionId.LEGACY__WELL_FIND_HAN_V__DEPLOY_HAN_LOST;
        if (GameConditions.canDeployCardFromLostPile(game, playerId, self, lostId, Persona.HAN)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, lostId, CardSubtype.LOST);
            action.setText("Deploy Han from Lost Pile");
            action.allowResponses("Deploy Han (except Han, Chewie, And The Falcon) from Lost Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromLostPileEffect(action, hanFilter, false));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
