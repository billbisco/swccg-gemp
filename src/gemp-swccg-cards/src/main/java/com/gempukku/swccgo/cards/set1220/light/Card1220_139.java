package com.gempukku.swccgo.cards.set1220.light;

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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardsEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Away Put Your Weapon
 */
public class Card1220_139 extends AbstractUsedOrLostInterrupt {
    public Card1220_139() {
        super(Side.LIGHT, 5, Title.Away_Put_Your_Weapon, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I mean you no harm.'");
        setGameText("USED: An opponent's just drawn [Permanent Weapon] weapon destiny is -3. (Immune to Sense.) LOST: Once per game, retrieve up to 5 weapons (may take one retireved card into hand).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, opponent, Icon.PERMANENT_WEAPON)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Subtract 3 from weapon destiny");
            action.setImmuneTo(Title.Sense);
            action.allowResponses("Make opponent's permanent weapon destiny -3",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, -3));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId retrieveId = GameTextActionId.LEGACY__AWAY_PUT_YOUR_WEAPON_V__RETRIEVE_WEAPONS;
        if (GameConditions.isOncePerGame(game, self, retrieveId)
                && GameConditions.hasLostPile(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, retrieveId, CardSubtype.LOST);
            action.setText("Retrieve up to 5 weapons");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.allowResponses("Retrieve up to 5 weapons",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new RetrieveCardsEffect(action, playerId, 5, true, Filters.weapon) {
                                        @Override
                                        public boolean mayBeTakenIntoHand() {
                                            return true;
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
