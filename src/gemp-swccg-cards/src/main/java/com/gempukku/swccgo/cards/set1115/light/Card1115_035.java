package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Rebel
 * Title: Wes Janson (V)
 */
public class Card1115_035 extends AbstractRebel {
    public Card1115_035() {
        super(Side.LIGHT, 2, 2, 2, 2, 5, "Wes Janson", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Served for many years as a gunner with friend Jek Porkins in the Tierfon Yellow Aces. Expert marksmanship helped the Alliance earn some of its earliest victories.");
        setGameText("Adds 2 to power of anything he pilots. While piloting Red 3, it gains [Nav Computer] and Wes draws one battle destiny if unable to otherwise. While aboard a starship or vehicle, its total weapon destiny is +1; if with Jek and it hits a target, opponent loses 1 Force from Reserve Deck.");
        addIcons(Icon.HOTH, Icon.PILOT, Icon.WARRIOR);
        addPersona(Persona.WES);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new IconModifier(self, Filters.and(Filters.Red_3, Filters.hasPiloting(self)), new PilotingCondition(self, Filters.Red_3), Icon.NAV_COMPUTER));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Red_3), 1));
        modifiers.add(new TotalWeaponDestinyModifier(self,
                Filters.or(Filters.hasAboard(self), Filters.attachedTo(Filters.hasAboard(self))),
                new AboardCondition(self, Filters.or(Filters.starship, Filters.vehicle)), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justHitBy(game, effectResult, Filters.any, Filters.any, Filters.hasAboard(self))
                && GameConditions.isWith(game, self, Filters.Jek)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 1 Force");
            action.appendEffect(new LoseForceFromReserveDeckEffect(action, game.getOpponent(self.getOwner()), 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
