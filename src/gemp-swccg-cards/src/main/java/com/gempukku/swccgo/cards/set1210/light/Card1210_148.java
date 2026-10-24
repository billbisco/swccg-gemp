package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Lost
 * Title: Cantina Brawl
 */
public class Card1210_148 extends AbstractLostInterrupt {
    public Card1210_148() {
        super(Side.LIGHT, 5, Title.Cantina_Brawl, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'...watch your step. This place can be a little rough.' The Mos Eisley Cantina harbors smugglers, thieves, cutthroats, criminals and bounty hunters!");
        setGameText("If a battle was just initiated at an interior site where both players have a participating character armed with a weapon, your weapon destiny draws there are +1 (+3 if at Cantina) for remainder of turn.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.interior_site)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.character_with_a_weapon))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(self), Filters.character_with_a_weapon))) {

            final PhysicalCard battleLocation = game.getGameState().getBattleLocation();
            final int amount = Filters.Cantina.accepts(game, battleLocation) ? 3 : 1;
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add " + amount + " to weapon destiny draws");
            action.allowResponses("Your weapon destiny draws there are +" + amount + " for remainder of turn",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new EachWeaponDestinyModifier(self,
                                                    Filters.and(Filters.your(self), Filters.weapon, Filters.at(battleLocation)),
                                                    amount),
                                            "Your weapon destiny draws there are +" + amount));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
