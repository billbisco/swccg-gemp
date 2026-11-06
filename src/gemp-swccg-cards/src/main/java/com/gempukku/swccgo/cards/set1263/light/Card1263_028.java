package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Firefight (V), Virtual Block 6.3. */
public class Card1263_028 extends AbstractUsedInterrupt {
    public Card1263_028() {
        super(Side.LIGHT, 5, "Firefight", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Lieutenant Page effectively deployed his detachment of Rebel commandos during the attack at the control bunker.");
        setGameText("If a battle was just initiated (except at a Coruscant site), each of your troopers and [Republic] starfighters present is power +1 and immune to attrition for remainder of turn.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self) {
        Filter affected = Filters.and(Filters.your(self), Filters.presentInBattle,
                Filters.or(Filters.trooper, Filters.and(Icon.REPUBLIC, Filters.starfighter)));
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.not(Filters.Coruscant_site))
                && GameConditions.isDuringBattleWithParticipant(game, affected)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add power and immunity to troopers and Republic starfighters");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    Collection<PhysicalCard> cards = Filters.filterActive(game, self, affected);
                    action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                            new PowerModifier(self, Filters.in(cards), 1), null));
                    action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                            new ImmuneToAttritionModifier(self, Filters.in(cards)), null));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
